/*
 * Copyright 2024 Marelis Adlatus <software@marelis.cz>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.marelis.radiorec;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.MpegFrame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class RecordWorker extends SwingWorker<Void, ResponseEvent> {

    private final Record record;
    private final String link;
    private final File recFile;
    private final File tempFile;
    private int metaInt;
    private final AtomicBoolean finish;
    private final AtomicBoolean cancel;

    public RecordWorker(Record record) {
        this.record = record;
        this.link = record.props.getProperty(Record.PROP_URL);
        this.recFile = getRecFile();
        this.tempFile = getTempFile();
        this.finish = new AtomicBoolean(false);
        this.cancel = new AtomicBoolean(false);
    }

    @Override
    protected Void doInBackground() throws Exception {
        
        HttpURLConnection httpCon = getConnection(link);
        if (httpCon == null) {
            System.out.println("Connection failed");
            record.setStatus(Record.RECORD_FAILED);
            return null;
        }
        if (httpCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
            System.out.println("Wrong response code " + httpCon.getResponseCode());
            record.setStatus(Record.RECORD_FAILED);
            return null;
        }

        metaInt = httpCon.getHeaderFieldInt("icy-metaint", 0);

        if (metaInt == 0) {
            System.out.println("Missing tag icy-metaint");
            record.setStatus(Record.RECORD_FAILED);
            return null;
        }
        
        byte[] buffer = new byte[4096];
        int readed;
        int counter = 0;
        byte[] frame = null;
        byte[] header = new byte[4];
        int framePos = 0;
        int frameSize = 0;
        StringBuilder meta = new StringBuilder();
        int metaSize = 0;
        int metaPos = 0;
        int frames = 0;

        InputStream inputStream = httpCon.getInputStream();
        OutputStream outputStream = new FileOutputStream(tempFile);

        record.setStatus(Record.RECORD_RUNNING);

        while ((readed = inputStream.read(buffer)) > 0) {
            for (int idx = 0; idx < readed; idx++) {
                if (counter < metaInt) {
                    switch (framePos) {
                        case 0 -> {
                            if ((buffer[idx] == (byte) 0xff)) {
                                header[0] = (byte) 0xff;
                                framePos++;
                            }
                        }
                        case 1 -> {
                            if ((buffer[idx] == (byte) 0xfb)) {
                                header[1] = buffer[idx];
                                framePos++;
                            } else {
                                framePos = 0;
                            }
                        }
                        case 2 -> {
                            header[2] = buffer[idx];
                            framePos++;
                        }
                        case 3 -> {
                            header[3] = buffer[idx];
                            MpegFrame mp3frame = new MpegFrame(header);
                            frameSize = mp3frame.getLengthInBytes();
                            frame = new byte[frameSize];
                            frame[0] = header[0];
                            frame[1] = header[1];
                            frame[2] = header[2];
                            frame[3] = header[3];
                            framePos++;
                        }
                        default -> {
                            if (framePos < frameSize) {
                                frame[framePos] = buffer[idx];
                                framePos++;
                                if (framePos == frameSize) {
                                    outputStream.write(frame, 0, frameSize);
                                    framePos = 0;
                                    frames++;
                                }
                            }
                        }

                    }
                    counter++;
                } else {
                    if (metaSize == 0) {
                        metaSize = buffer[idx] * 16;
                        if (metaSize > 0) {
                            System.out.println("Meta data length: " + metaSize);
                        }
                        if (metaSize == 0) {
                            counter = 0;
                        }
                    } else {
                        if (metaPos < metaSize) {
                            meta.append((char) buffer[idx]);
                            metaPos++;
                            if (metaPos == metaSize) {
                                System.out.println("Meta data: " + meta.toString());
                                meta.setLength(0);
                                metaSize = 0;
                                metaPos = 0;
                                counter = 0;
                            }
                        }
                    }
                }
            }
            if (finish.get()) {
                outputStream.close();
                System.out.println("Finish !");
                break;
            }
            if (cancel.get()) {
                outputStream.close();
                System.out.println("Cancel !");
                break;
            }
        }

        System.out.println("Done, frames: " + frames);

        if (cancel.get()) {
            Files.delete(tempFile.toPath());
            record.setStatus(Record.RECORD_CANCELED);
            return null;
        }

        if (finish.get()) {
            record.setStatus(Record.RECORD_STOPPING);
            Path parent = Path.of(recFile.getParent());
            if (Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
            Mp3File mp3file = new Mp3File(tempFile);
            ID3v2 id3v2Tag = getID3v24Tag();
            mp3file.setId3v2Tag(id3v2Tag);
            mp3file.save(recFile.getAbsolutePath());
            Files.delete(tempFile.toPath());
        }

        return null;
    }

    @Override
    protected void done() {
        record.setStatus(Record.RECORD_DONE);
    }

    @Override
    protected void process(List<ResponseEvent> chunks) {
        chunks.forEach(re -> record.fireResponseEvent(re));
    }

    private HttpURLConnection getConnection(String link) {
        try {
            HttpURLConnection result = (HttpURLConnection) URI.create(link).toURL().openConnection();
            result.setRequestProperty("User-Agent", "Java(TM) SE Runtime Environment");
            result.setRequestProperty("Accept", "text/html");
            result.setRequestProperty("Accept-Language", "en-US");
            result.setRequestProperty("Icy-MetaData", "1"); // meta data request
            return result;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private File getRecFile() {
        String dir = record.props.getProperty(Record.PROP_FILE_DIR);
        String name = record.props.getProperty(Record.PROP_FILE_NAME);
        return new File(dir.concat(File.separator).concat(name).concat(".mp3"));
    }

    private File getTempFile() {
        String dir = RadioRec.getInstance().prefs.get(RadioRec.PROP_TEMP_DIR, RadioRec.DEFAULT_TEMP_DIR);
        String name = dir.concat(File.separator).concat("RadioRec-").concat(UUID.randomUUID().toString().concat(".mp3"));
        return new File(name);
    }

    public ID3v24Tag getID3v24Tag() {
        ID3v24Tag result = new ID3v24Tag();
        result.setTrack(record.props.getProperty(Record.PROP_TRACK));
        result.setArtist(record.props.getProperty(Record.PROP_ARTIST));
        result.setTitle(record.props.getProperty(Record.PROP_TITLE));
        result.setAlbum(record.props.getProperty(Record.PROP_ALBUM));
        result.setYear(record.props.getProperty(Record.PROP_YEAR));
        //result.setGenre(Integer.valueOf(record.props.getProperty(Record.PROP_GENRE), 10));
        result.setComment(record.props.getProperty(Record.PROP_COMMENT));
        result.setLyrics(record.props.getProperty(Record.PROP_LYRICS));
        result.setComposer(record.props.getProperty(Record.PROP_COMPOSER));
        result.setPublisher(record.props.getProperty(Record.PROP_PUBLISHER));
        result.setOriginalArtist(record.props.getProperty(Record.PROP_ORIGINAL_ARTIST));
        result.setAlbumArtist(record.props.getProperty(Record.PROP_ALBUM_ARTIST));
        result.setCopyright(record.props.getProperty(Record.PROP_COPYRIGHT));
        result.setUrl(record.props.getProperty(Record.PROP_URL));
        result.setEncoder(record.props.getProperty(Record.PROP_ENCODER));
        return result;
    }

    public void finish() {
        finish.set(true);
    }

    public void cancel() {
        cancel.set(true);
    }
}
