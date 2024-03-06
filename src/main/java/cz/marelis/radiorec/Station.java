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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class Station extends ResponseNode
        implements Comparable<Station>, ResponseListener {

    public final static String STATION_FILE_EXT = "radiorec-station";
    public final static String STATION_FILE_EXT_WITH_DOT = "." + STATION_FILE_EXT;

    public final static String PROP_STATION_NAME = "station-name";
    public final static String PROP_STATION_LINK = "station-link";

    public final static String PROP_RECORD_ADDING = "record-adding";
    public final static String RECORD_ADDING_MANUALLY = "manually";
    public final static String RECORD_ADDING_STREAM = "stream";
    public final static String RECORD_ADDING_PROGRAM = "program";

    public final static String PROP_PROGRAM_LINK = "program-link";
    public final static String PROP_PROGRAM_TIME_ZONE_ID = "program-time-zone-id";
    public final static String PROP_PROGRAM_ROOT_XPATH = "program-root-xpath";
    public final static String PROP_PROGRAM_TITLE_CSS_QUERY = "program-title-css-query";
    public final static String PROP_PROGRAM_COMMENT_CSS_QUERY = "program-comment-css-query";
    public final static String PROP_PROGRAM_START_TIME_ATTR = "program-start-time-attr";
    public final static String PROP_PROGRAM_START_TIME_FORMAT = "program-start-time-format";
    public final static String PROP_PROGRAM_FINISH_TIME_ATTR = "program-finish-time-attr";
    public final static String PROP_PROGRAM_FINISH_TIME_FORMAT = "program-finish-time-format";

    public final static String DEFAULT_STATION_NAME = "New station";
    public final static String DEFAULT_PROGRAM_TIME_ZONE_ID = "UTC";
    public final static String DEFAULT_RECORD_ADDING = RECORD_ADDING_MANUALLY;

    public final static int EVT_PLAYBACK_RUN = 0;
    public final static int EVT_PLAYBACK_STOP = 1;
    public final static int EVT_SAVE_TO_FILE = 2;

    public final ProgramDirector programDirector;
    public final RecordDirector recordDirector;

    public File stationFile;

    public final static int PLAYBACK_STOPPED = 0;
    public final static int PLAYBACK_RUNS = 1;
    public final static int PLAYBACK_FAILED = 2;

    private Player player;

    public Station(String name) {
        props.setProperty(PROP_STATION_NAME, !name.equals("") ? name : DEFAULT_STATION_NAME);
        props.setProperty(PROP_STATION_LINK, "");
        props.setProperty(PROP_PROGRAM_LINK, "");
        props.setProperty(PROP_PROGRAM_ROOT_XPATH, "");
        props.setProperty(PROP_PROGRAM_TITLE_CSS_QUERY, "");
        props.setProperty(PROP_PROGRAM_COMMENT_CSS_QUERY, "");
        props.setProperty(PROP_PROGRAM_START_TIME_ATTR, "");
        props.setProperty(PROP_PROGRAM_START_TIME_FORMAT, "");
        props.setProperty(PROP_PROGRAM_FINISH_TIME_ATTR, "");
        props.setProperty(PROP_PROGRAM_FINISH_TIME_FORMAT, "");
        props.setProperty(PROP_PROGRAM_TIME_ZONE_ID, DEFAULT_PROGRAM_TIME_ZONE_ID);
        props.setProperty(PROP_RECORD_ADDING, DEFAULT_RECORD_ADDING);
        programDirector = new ProgramDirector();
        recordDirector = new RecordDirector();
        initDirectors();
    }

    public Station(File file) {
        this(file.getName());
        loadFromFile(file);
    }

    private void initDirectors() {
        programDirector.addResponseListener(this);
        recordDirector.addResponseListener(this);
    }

    private void loadFromFile(File file) {
        stationFile = file;
        FileInputStream fis;
        Document doc = null;

        try {
            fis = new FileInputStream(stationFile);
            doc = Jsoup.parse(fis, null, "", Parser.xmlParser());
            fis.close();
            //doc.normalise();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Station.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Station.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (doc == null) {
            return;
        }

        Elements elements;

        Element stationElement = doc.getElementsByTag("station").get(0);

        props.clear();
        elements = stationElement.getElementsByTag("props").get(0).getElementsByTag("prop");
        for (Element element : elements) {
            Attributes attrs = element.attributes();
            for (Attribute attr : attrs) {
                String propName = attr.getKey();
                String propVal = attr.getValue();
                props.setProperty(propName, propVal);
            }
        }

        programDirector.clearItems();
        elements = stationElement.getElementsByTag("progs").get(0).getElementsByTag("prog");
        for (Element element : elements) {
            Attributes attrs = element.attributes();
            Program program = new Program();
            for (Attribute attr : attrs) {
                String propName = attr.getKey();
                String propVal = attr.getValue();
                program.props.setProperty(propName, propVal);
            }
            programDirector.addItem(program);
        }

        recordDirector.clearItems();
        elements = stationElement.getElementsByTag("recs").get(0).getElementsByTag("rec");
        for (Element element : elements) {
            Attributes attrs = element.attributes();
            Record record = new Record();
            for (Attribute attr : attrs) {
                String propName = attr.getKey();
                String propVal = attr.getValue();
                record.props.setProperty(propName, propVal);
            }
            recordDirector.addItem(record);
        }
    }

    public String getStationXML() {
        Document document = Jsoup.parse("", Parser.xmlParser());

        XmlDeclaration declaration = new XmlDeclaration("xml", false);
        declaration.attr("version", "1.0");
        declaration.attr("encoding", "UTF-8");

        document.appendChild(declaration);

        Element stationElement = document.createElement("station");
        document.appendChild(stationElement);

        stationElement.attr("name", props.getProperty(Station.PROP_STATION_NAME));

        Element list, item;

        list = document.createElement("props");
        stationElement.appendChild(list);

        for (Object key : props.keySet()) {
            item = document.createElement("prop");
            item.attr((String) key, props.getProperty((String) key));
            list.appendChild(item);
        }

        list = document.createElement("progs");
        stationElement.appendChild(list);

        for (Program program : programDirector) {
            item = document.createElement("prog");
            for (Object key : program.props.keySet()) {
                item.attr((String) key, program.props.getProperty((String) key));
            }
            list.appendChild(item);
        }

        list = document.createElement("recs");
        stationElement.appendChild(list);

        for (Record record : recordDirector) {
            item = document.createElement("rec");
            for (Object key : record.props.keySet()) {
                item.attr((String) key, record.props.getProperty((String) key));
            }
            list.appendChild(item);
        }

        return document.toString();
    }

    public void saveToFile(File file) {

        String xml = getStationXML();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try ( OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream(file), StandardCharsets.UTF_8)) {
                    writer.write(xml);
                } catch (IOException ex) {
                    Logger.getLogger(Station.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            protected void done() {
                stationFile = file;
                Station.this.fireResponseEvent(new ResponseEvent(Station.this, EVT_SAVE_TO_FILE, null, "Station.saveToFile"));
            }
        };
        worker.execute();
    }

    public void saveToFile() {
        saveToFile(stationFile);
    }

    public String getRecordDirName() {
        String dir = RadioRec.getInstance().prefs.get(RadioRec.PROP_RECORDS_DIR, RadioRec.DEFAULT_RECORDS_DIR);
        boolean subFolders = String.valueOf(true)
                .equals(RadioRec.getInstance().prefs.get(RadioRec.PROP_RECORDS_SUBFOLDERS, RadioRec.DEFAULT_RECORDS_SUBFOLDERS));
        if (subFolders) {
            String zone = RadioRec.getInstance().prefs.get(RadioRec.PROP_TIME_ZONE_ID, RadioRec.DEFAULT_TIME_ZONE_ID);
            ZonedDateTime time = ZonedDateTime.now(ZoneId.of(zone));
            String subDir = RadioRec.getInstance().prefs.get(RadioRec.PROP_RECORDS_SUBFOLDERS_FORMAT, RadioRec.DEFAULT_RECORDS_SUBFOLDERS_FORMAT);
            subDir = replaceTimePattern(subDir, time);
            subDir = replaceStationPattern(subDir);
            return dir.concat(File.separator).concat(subDir);
        }
        return dir;
    }

    public String getRecordFileName(Record record) {
        String fileName = RadioRec.getInstance().prefs.get(RadioRec.PROP_RECORDS_FILENAME_FORMAT, RadioRec.DEFAULT_RECORDS_FILENAME_FORMAT);
        fileName = record.replaceRecordPattern(fileName);
        fileName = replaceStationPattern(fileName);
        return fileName;
    }

    public String replaceTimePattern(String source, ZonedDateTime time) {
        String result = source
                    .replace("{year}", String.format("%04d", time.getYear()))
                    .replace("{month}", String.format("%02d", time.getMonthValue()))
                    .replace("{day}", String.format("%02d", time.getDayOfMonth()))
                    .replace("{hour}", String.format("%02d", time.getHour()))
                    .replace("{minute}", String.format("%02d", time.getMinute()))
                    .replace("{second}", String.format("%02d", time.getSecond()));
        return result;
    }
    
    public String replaceStationPattern(String source) {
        String result = source
                .replace("{station}", props.getProperty(Station.PROP_STATION_NAME));
        return result;
    }
    
    public void stopPlaying() {
        if (player != null) {
            player.close();
        }
    }

    public void startPlaying() {

        if (!isStatus(Station.PLAYBACK_STOPPED)) {
            return;
        }

        String link = props.getProperty(PROP_STATION_LINK);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    HttpURLConnection httpCon;
                    httpCon = (HttpURLConnection) URI.create(link).toURL().openConnection();
                    int responseCode = httpCon.getResponseCode();
                    System.out.println("Response Code: " + responseCode);

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        System.out.println("Server returned response code " + responseCode + ". Connection failed.");
                    } else {
                        InputStream is = httpCon.getInputStream();
                        player = new Player(is);
                        player.play();
                    }
                } catch (IOException | JavaLayerException ex) {
                    Logger.getLogger(Station.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            protected void done() {
                setStatus(PLAYBACK_STOPPED);
            }
        };
        worker.execute();

        setStatus(PLAYBACK_RUNS);
    }

    public boolean isRecording() {
        for (Record record : recordDirector) {
            if (record.isStatus(Record.RECORD_RUNNING)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Station o) {
        return 0;
    }

    @Override
    public void onResponseEvent(ResponseEvent re) {
        if (re.getSource() instanceof RecordDirector) {
            saveToFile();
        }
        fireResponseEvent(re);
    }

    @Override
    public void update(ZonedDateTime time) {
        programDirector.update(time);
        recordDirector.update(time);
    }

}
