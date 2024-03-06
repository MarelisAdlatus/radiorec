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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class ProgramDirector extends ResponseList<Program> {

    public static String getBrowserPath() {

        String result;

        // create the process
        ProcessBuilder build = new ProcessBuilder();

        if (System.getProperty("os.name").contains("Windows")) {
            build.command("powershell", "(Get-ItemProperty",
                    "'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion"
                    + "\\App Paths\\chrome.exe').'(Default)'");
        } else {
            build.command("bash", "-c", "type -p chromium-browser");
        }

        StringBuilder sb = new StringBuilder();
        try {
            // starting the process
            Process process = build.start();
            // for reading the output from stream
            BufferedReader stdInput = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String s;
            while ((s = stdInput.readLine()) != null) {
                sb.append(s);
            }
            process.waitFor();
            System.out.println("Exit code: " + process.exitValue());
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(RadioRec.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }

        result = sb.toString();
        System.out.println("Browser Path: " + result);
        return result;
    }

    public static String getProgramPageSource(String link) {

        String browserPath = RadioRec.getInstance().prefs.get(RadioRec.PROP_WEB_BROWSER_PATH, "");
        if (browserPath.isEmpty()) {
            return "";
        }

        String[] browserCommand = RadioRec.getInstance().prefs.get(RadioRec.PROP_WEB_BROWSER_COMMAND,
                RadioRec.DEFAULT_WEB_BROWSER_COMMAND).split(" ");

        String cmdStr[] = new String[browserCommand.length + 2];

        cmdStr[0] = browserPath;
        System.arraycopy(browserCommand, 0, cmdStr, 1, browserCommand.length);
        cmdStr[browserCommand.length + 1] = link;

        ProcessBuilder build = new ProcessBuilder();
        build.command(cmdStr);

        StringBuilder sb = new StringBuilder();

        try {
            // starting the process
            Process process = build.start();

            // for reading the output from stream
            BufferedReader stdInput = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String s;
            while ((s = stdInput.readLine()) != null) {
                sb.append(s);
            }

            process.waitFor();

            System.out.println("Exit code: " + process.exitValue());

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(RadioRec.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }

        return sb.toString();
    }

    public static ArrayList<Program> parseProgram(Station station, Document doc) {

        ArrayList<Program> result = new ArrayList<>();

        ZoneId appZone = ZoneId.of(RadioRec.DEFAULT_TIME_ZONE_ID);
        ZoneId programZone = ZoneId.of(station.props.getProperty(Station.PROP_PROGRAM_TIME_ZONE_ID));

        // yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
        String startTimeFormat = station.props.getProperty(Station.PROP_PROGRAM_START_TIME_FORMAT);
        DateTimeFormatter timeStartFormatter = DateTimeFormatter.ofPattern(startTimeFormat)
                .withZone(programZone);

        // yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
        String finishTimeFormat = station.props.getProperty(Station.PROP_PROGRAM_FINISH_TIME_FORMAT);
        DateTimeFormatter timeFinishFormatter = DateTimeFormatter.ofPattern(finishTimeFormat)
                .withZone(programZone);

        ZonedDateTime startTime;
        ZonedDateTime finishTime;
        String titleStr;
        String commentStr;

        // //li[starts-with(@class,'item')]
        String rootXPath = station.props.getProperty(Station.PROP_PROGRAM_ROOT_XPATH);
        // div h3
        String titleCSSQuery = station.props.getProperty(Station.PROP_PROGRAM_TITLE_CSS_QUERY);
        // div p
        String commentCSSQuery = station.props.getProperty(Station.PROP_PROGRAM_COMMENT_CSS_QUERY);
        // data-since
        String startTimeAttr = station.props.getProperty(Station.PROP_PROGRAM_START_TIME_ATTR);
        /// data-till
        String finishTimeAttr = station.props.getProperty(Station.PROP_PROGRAM_FINISH_TIME_ATTR);

        ZonedDateTime now = ZonedDateTime.now(programZone);

        Element element;
        Elements items = doc.selectXpath(rootXPath);

        for (Element item : items) {

            titleStr = "";
            element = item.selectFirst(titleCSSQuery);
            if (element != null) {
                // TODO: result.html() as option
                titleStr = element.text();
            }

            commentStr = "";
            element = item.selectFirst(commentCSSQuery);
            if (element != null) {
                // TODO: result.html() as option
                commentStr = element.text();
            }

            String startTimeStr = item.attr(startTimeAttr);
            startTime = ZonedDateTime.parse(startTimeStr, timeStartFormatter)
                    .withZoneSameInstant(appZone);

            String finishTimeStr = item.attr(finishTimeAttr);
            finishTime = ZonedDateTime.parse(finishTimeStr, timeFinishFormatter)
                    .withZoneSameInstant(appZone);

            if (finishTime.isAfter(now)) {
                Program program = new Program();
                program.setTimeProperty(Program.PROP_TIME_START, startTime);
                program.setTimeProperty(Program.PROP_TIME_FINISH, finishTime);
                program.props.setProperty(Program.PROP_TITLE, titleStr);
                program.props.setProperty(Program.PROP_COMMENT, commentStr);
                result.add(program);
            }
        }
        return result;
    }

    @Override
    public void update(ZonedDateTime time) {
        ArrayList<Program> list = new ArrayList<>();
        for (Program program : this) {
            if (program.isRemove()) {
                list.add(program);
            } else {
                program.update(time);
            }
        }
        removeItems(list);
    }

}
