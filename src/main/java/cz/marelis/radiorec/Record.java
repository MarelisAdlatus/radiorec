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

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class Record extends ResponseNode
        implements Comparable<Record> {

    public final static String PROP_FILE_DIR = "file-dir";
    public final static String PROP_FILE_NAME = "file-name";
    public final static String PROP_TIME_START = "time-start";
    public final static String PROP_TIME_FINISH = "time-finish";
    public final static String PROP_TRACK = "track";
    public final static String PROP_ARTIST = "artist";
    public final static String PROP_TITLE = "title";
    public final static String PROP_ALBUM = "album";
    public final static String PROP_YEAR = "year";
    public final static String PROP_GENRE = "genre";
    public final static String PROP_COMMENT = "comment";
    public final static String PROP_LYRICS = "lyrics";
    public final static String PROP_COMPOSER = "composer";
    public final static String PROP_PUBLISHER = "publisher";
    public final static String PROP_ORIGINAL_ARTIST = "original-artist";
    public final static String PROP_ALBUM_ARTIST = "album-artist";
    public final static String PROP_COPYRIGHT = "copyright";
    public final static String PROP_URL = "url";
    public final static String PROP_ENCODER = "encoder";

    public final static String DEFAULT_RECORD_TITLE = "New record";
    
    public final static int RECORD_PENDING = 0;
    public final static int RECORD_STARTUP = 1;
    public final static int RECORD_RUNNING = 2;
    public final static int RECORD_STOPPING = 3;
    public final static int RECORD_CANCELED = 4;
    public final static int RECORD_FAILED = 5;
    public final static int RECORD_DONE = 6;

    RecordWorker recordWorker;

    public Record() {
        props.setProperty(PROP_FILE_DIR, "");
        props.setProperty(PROP_FILE_NAME, "");
        props.setProperty(PROP_TIME_START, "");
        props.setProperty(PROP_TIME_FINISH, "");
        props.setProperty(PROP_TRACK, "");
        props.setProperty(PROP_ARTIST, "");
        props.setProperty(PROP_TITLE, DEFAULT_RECORD_TITLE);
        props.setProperty(PROP_ALBUM, "");
        props.setProperty(PROP_YEAR, "");
        props.setProperty(PROP_GENRE, "");
        props.setProperty(PROP_COMMENT, "");
        props.setProperty(PROP_LYRICS, "");
        props.setProperty(PROP_COMPOSER, "");
        props.setProperty(PROP_PUBLISHER, "");
        props.setProperty(PROP_ORIGINAL_ARTIST, "");
        props.setProperty(PROP_ALBUM_ARTIST, "");
        props.setProperty(PROP_COPYRIGHT, "");
        props.setProperty(PROP_URL, "");
        props.setProperty(PROP_ENCODER, "");
    }

    public void startRecording() {
        setStatus(Record.RECORD_STARTUP);
        recordWorker = new RecordWorker(this);
        recordWorker.execute();
    }

    public void finishRecording() {
        setStatus(Record.RECORD_STOPPING);
        if (recordWorker != null) {
            recordWorker.finish();
        }
    }

    public void cancelRecording() {
        setStatus(Record.RECORD_STOPPING);        
        if (recordWorker != null) {
            recordWorker.cancel();
        }
    }

    public String replaceRecordPattern(String source) {
        ZoneId zone = ZoneId.of(RadioRec.getInstance().prefs.get(RadioRec.PROP_TIME_ZONE_ID, RadioRec.DEFAULT_TIME_ZONE_ID));
        ZonedDateTime start = getTimeProperty(Program.PROP_TIME_START);
        ZonedDateTime finish = getTimeProperty(Program.PROP_TIME_FINISH);
        String result = source
                .replace("{year}", DateTimeFormatter.ofPattern("YYYY").format(finish.withZoneSameInstant(zone)))
                .replace("{month}", DateTimeFormatter.ofPattern("MM").format(finish.withZoneSameInstant(zone)))
                .replace("{day}", DateTimeFormatter.ofPattern("dd").format(finish.withZoneSameInstant(zone)))
                .replace("{hour}", DateTimeFormatter.ofPattern("HH").format(finish.withZoneSameInstant(zone)))
                .replace("{minute}", DateTimeFormatter.ofPattern("mm").format(finish.withZoneSameInstant(zone)))
                .replace("{second}", DateTimeFormatter.ofPattern("ss").format(finish.withZoneSameInstant(zone)))
                .replace("{title}", props.getProperty(Record.PROP_TITLE))
                .replace("{comment}", props.getProperty(Record.PROP_COMMENT))
                .replace("{start}", DateTimeFormatter.ofPattern("HHmmss").format(start.withZoneSameInstant(zone)))
                .replace("{finish}", DateTimeFormatter.ofPattern("HHmmss").format(finish.withZoneSameInstant(zone)));
        return result;
    }

    @Override
    public void update(ZonedDateTime time) {
       
        TemporalAccessor ta = DateTimeFormatter.ofPattern("mm'm'ss's'").withZone(ZoneId.of("UTC"))
                .parse(RadioRec.getInstance().prefs.get(RadioRec.PROP_RECORDS_TIME_APPEND, RadioRec.DEFAULT_RECORDS_TIME_APPEND));

        ZonedDateTime start = getTimeProperty(Record.PROP_TIME_START);
        ZonedDateTime finish = getTimeProperty(Record.PROP_TIME_FINISH)
                .plus(ta.get(ChronoField.MINUTE_OF_HOUR), ChronoUnit.MINUTES)
                .plus(ta.get(ChronoField.SECOND_OF_MINUTE), ChronoUnit.SECONDS);
        
        if (isStatus(RECORD_PENDING)) {
            if (time.isAfter(finish)) {
                setRemove(true);
            } else if (time.isAfter(start) && time.isBefore(finish)) {
                startRecording();
            }
        } else if (isStatus(RECORD_RUNNING)) {
            if (time.isAfter(finish)) {
                finishRecording();
            } else {
                double one = (Duration.between(start, finish).toSeconds() / 100.0);
                if (one > 0) {
                    Duration dur = Duration.between(start, time);
                    int newProgress = (int) (dur.toSeconds() / one);
                    setProgress(newProgress);
                }
            }
        } else if (isStatus(RECORD_DONE)) {
            setRemove(true);
        }
    }

    @Override
    public int compareTo(Record o) {
        ZonedDateTime start1 = getTimeProperty(Record.PROP_TIME_START);
        ZonedDateTime finish1 = getTimeProperty(Record.PROP_TIME_FINISH);
        ZonedDateTime start2 = o.getTimeProperty(Record.PROP_TIME_START);
        ZonedDateTime finish2 = o.getTimeProperty(Record.PROP_TIME_FINISH);
        if (start1.isAfter(start2)) {
            return 1;
        }
        if (start1.isEqual(start2)) {
            if (finish1.isAfter(finish2)) {
                return 1;
            }
            if (finish1.isEqual(finish2)) {
                return 0;
            }
        }
        return -1;
    }

}
