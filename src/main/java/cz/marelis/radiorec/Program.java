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
import java.time.ZonedDateTime;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class Program extends ResponseNode
        implements Comparable<Program> {

    public final static String PROP_TIME_START = "time-start";
    public final static String PROP_TIME_FINISH = "time-finish";
    public final static String PROP_TITLE = "title";
    public final static String PROP_COMMENT = "comment";

    public final static int STATUS_UNKNOWN = 0;
    public final static int STATUS_PREVIOUS = 1;
    public final static int STATUS_CURRENT = 2;
    public final static int STATUS_FUTURE = 3;

    public Program() {
        props.setProperty(PROP_TIME_START, "");
        props.setProperty(PROP_TIME_FINISH, "");
        props.setProperty(PROP_TITLE, "");
        props.setProperty(PROP_COMMENT, "");
    }

    @Override
    public void update(ZonedDateTime time) {
        ZonedDateTime start = getTimeProperty(Program.PROP_TIME_START);
        ZonedDateTime finish = getTimeProperty(Program.PROP_TIME_FINISH);
        if (time.isAfter(finish)) {
            setStatus(STATUS_PREVIOUS);
            setProgress(100);
            setRemove(true);
        } else {
            if (time.isAfter(start)) {
                setStatus(STATUS_CURRENT);
                double one = (Duration.between(start, finish).toSeconds() / 100.0);
                if (one > 0) {
                    Duration dur = Duration.between(start, time);
                    int newProgress = (int) (dur.toSeconds() / one);
                    setProgress(newProgress);
                }
            } else {
                setStatus(STATUS_FUTURE);
                setProgress(0);
            }
        }
    }

    @Override
    public int compareTo(Program o) {
        ZonedDateTime start1 = getTimeProperty(Program.PROP_TIME_START);
        ZonedDateTime finish1 = getTimeProperty(Program.PROP_TIME_FINISH);
        ZonedDateTime start2 = o.getTimeProperty(Program.PROP_TIME_START);
        ZonedDateTime finish2 = o.getTimeProperty(Program.PROP_TIME_FINISH);
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
