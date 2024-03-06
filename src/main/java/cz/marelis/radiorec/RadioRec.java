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

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * 
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class RadioRec implements AppActivityListener, AppTickerListener {

    // TODO: Stations folder for saved station
    
    public final static String PROP_STATIONS_DIR = "stations-dir";
    public final static String PROP_RECORDS_DIR = "records-dir";
    public final static String PROP_RECORDS_SUBFOLDERS = "records-subfolders";
    public final static String PROP_RECORDS_SUBFOLDERS_FORMAT = "records-subfolders-format";
    public final static String PROP_RECORDS_FILENAME_FORMAT = "records-filename-format";
    public final static String PROP_RECORDS_TIME_APPEND = "records-time-append";
    public final static String PROP_TEMP_DIR = "temp-dir";

    public final static String PROP_UI_THEME = "ui-theme";
    public final static String PROP_TIME_ZONE_ID = "time-zone-id";
    public final static String PROP_TIME_FORMAT = "time-format";
    public final static String PROP_WEB_BROWSER_PATH = "web-browser-path";
    public final static String PROP_WEB_BROWSER_COMMAND = "web-browser-command";
    public final static String PROP_PARSE_HTML_THEME = "parse-html-theme";

    public final static String PROP_NULL = "null";

    public final static String UI_THEME_METAL = "metal";
    public final static String UI_THEME_NIMBUS = "nimbus";
    public final static String UI_THEME_CDE_MOTIF = "cde-motif";
    public final static String UI_THEME_WINDOWS = "windows";
    public final static String UI_THEME_WINDOWS_CLASSIC = "windows-classic";
    public final static String UI_THEME_FLATLAF_LIGHT = "flatlaf-light";
    public final static String UI_THEME_FLATLAF_DARK = "flatlaf-dark";

    public final static String DEFAULT_USER_DIR
            = new JFileChooser().getFileSystemView().getDefaultDirectory().getAbsolutePath();

    public final static String DEFAULT_STATIONS_DIR
            = DEFAULT_USER_DIR.concat(File.separator).concat("Marelis")
                    .concat(File.separator).concat("RadioRec");

    public final static String DEFAULT_RECORDS_DIR
            = DEFAULT_STATIONS_DIR.concat(File.separator).concat("Records");

    public final static String DEFAULT_RECORDS_SUBFOLDERS = String.valueOf(false);

    public final static String DEFAULT_RECORDS_SUBFOLDERS_FORMAT
            = "{station}" + File.separator + "{year}" + File.separator + "{month}";
    
    public final static String DEFAULT_RECORDS_FILENAME_FORMAT
            = "{year}{month}{day} {hour}{minute} {station} - {title}";

    public final static String DEFAULT_RECORDS_TIME_APPEND = "00m00s";

    public final static String DEFAULT_TEMP_DIR
            = RadioRec.removeTrailingSlashes(System.getProperty("java.io.tmpdir"));

    public final static String DEFAULT_UI_THEME = UI_THEME_NIMBUS;

    public final static String DEFAULT_TIME_ZONE_ID = "UTC";

    public final static String DEFAULT_TIME_FORMAT = "dd.MM.yy HH:mm:ss";

    public final static String DEFAULT_WEB_BROWSER_COMMAND
            = "--headless --disable-gpu --disable-software-rasterizer --dump-dom";

    public final static String DEFAULT_PARSE_HTML_THEME = "default";

    public final Preferences prefs = Preferences.userNodeForPackage(RadioRec.class);

    public Font fontAwesomeRegular;
    public Font fontAwesomeSolid;

    public final StationDirector stationDirector = new StationDirector();

    private AppActivity appActivity;
    private AppTicker appTicker;

    private final ArrayList<Image> frameIcons = new ArrayList<>();

    private MainFrame mainFrame;

    private static final AtomicBoolean doneFlag = new AtomicBoolean(false);

    private static class Loader {

        static final RadioRec INSTANCE = new RadioRec();
    }

    private RadioRec() {
    }

    // singleton
    public static RadioRec getInstance() {
        return Loader.INSTANCE;
    }

    public void initActivity(String args[]) {
        if (AppActivity.isAppActive(args)) {
            System.out.println("Already active, stop!");
            System.exit(0);
        } else {
            appActivity = new AppActivity();
            appActivity.addAppActivityListener(getInstance());
            new Thread(appActivity).start();
        }
    }

    public void initPrefs() {
        setPrefs(PROP_STATIONS_DIR, DEFAULT_STATIONS_DIR);
        setPrefs(PROP_RECORDS_DIR, DEFAULT_RECORDS_DIR);
        setPrefs(PROP_RECORDS_SUBFOLDERS, DEFAULT_RECORDS_SUBFOLDERS);
        setPrefs(PROP_RECORDS_SUBFOLDERS_FORMAT, DEFAULT_RECORDS_SUBFOLDERS_FORMAT);
        setPrefs(PROP_RECORDS_FILENAME_FORMAT, DEFAULT_RECORDS_FILENAME_FORMAT);
        setPrefs(PROP_RECORDS_TIME_APPEND, DEFAULT_RECORDS_TIME_APPEND);
        setPrefs(PROP_TEMP_DIR, DEFAULT_TEMP_DIR);
        setPrefs(PROP_UI_THEME, UI_THEME_FLATLAF_LIGHT);
        setPrefs(PROP_TIME_ZONE_ID, DEFAULT_TIME_ZONE_ID);
        setPrefs(PROP_TIME_FORMAT, DEFAULT_TIME_FORMAT);
        setPrefs(PROP_WEB_BROWSER_PATH, ProgramDirector.getBrowserPath());
        setPrefs(PROP_WEB_BROWSER_COMMAND, DEFAULT_WEB_BROWSER_COMMAND);
        setPrefs(PROP_PARSE_HTML_THEME, DEFAULT_PARSE_HTML_THEME);
    }

    public void initFonts() {
        InputStream is;
        try {
            is = RadioRec.class.getClassLoader()
                    .getResourceAsStream("fonts/Font Awesome 6 Free-Regular-400.otf");
            fontAwesomeRegular = Font.createFont(Font.TRUETYPE_FONT, is);
            is = RadioRec.class.getClassLoader().
                    getResourceAsStream("fonts/Font Awesome 6 Free-Solid-900.otf");
            fontAwesomeSolid = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(RadioRec.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(2);
        }
    }

    public void initDirs() {
        // Stations directory
        File stationsDir = new File(prefs.get(PROP_STATIONS_DIR, DEFAULT_STATIONS_DIR));
        if (!stationsDir.exists()) {
            System.out.println("Stations directory not found");
            if (!stationsDir.mkdirs()) {
                System.exit(3);
            } else {
                System.out.println("Stations directory created");
            }
        }
        // Records directory
        File recordsDir = new File(prefs.get(PROP_RECORDS_DIR, DEFAULT_RECORDS_DIR));
        if (!recordsDir.exists()) {
            System.out.println("Records directory not found");
            if (!recordsDir.mkdirs()) {
                System.exit(4);
            } else {
                System.out.println("Records directory created");
            }
        }
    }

    public void initStations() {
        try {
            String keys[] = RadioRec.getInstance().prefs.node("stations").keys();
            for (String key : keys) {
                String filePath = RadioRec.getInstance().prefs.node("stations").get(key, "");
                File file = new File(filePath);
                if (file.exists()) {
                    Station station = new Station(file);
                    stationDirector.addItem(station);
                }
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initArgs(String args[]) {
        for (String arg : args) {
            File file = new File(arg);
            if (file.exists()) {
                Station station = new Station(file);
                if (!stationDirector.containsItem(station)) {
                    stationDirector.addItem(station);
                }
            }
        }
    }

    public void initIcons() {
        // Application main icon
        int sizes[] = {16, 20, 24, 28, 30, 31, 32, 40, 42, 47, 48, 56, 57, 60,
            63, 64, 72, 84, 96, 120, 128, 144, 152, 195, 228, 256, 512};
        for (int size : sizes) {
            URL url = MainFrame.class.getClassLoader()
                    .getResource(String.format("icons/RadioRec-%d.png", size));
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                frameIcons.add(icon.getImage());
            }
        }
    }

    public void initLook() {
        try {
            switch (prefs.get(PROP_UI_THEME, DEFAULT_UI_THEME)) {
                case UI_THEME_METAL -> {
                    UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
                }
                case UI_THEME_NIMBUS -> {
                    UIManager.setLookAndFeel(new javax.swing.plaf.nimbus.NimbusLookAndFeel());
                }
                case UI_THEME_CDE_MOTIF -> {
                    UIManager.setLookAndFeel(UIManager.createLookAndFeel("CDE/Motif"));
                }
                case UI_THEME_WINDOWS -> {
                    UIManager.setLookAndFeel(UIManager.createLookAndFeel("Windows"));
                }
                case UI_THEME_WINDOWS_CLASSIC -> {
                    UIManager.setLookAndFeel(UIManager.createLookAndFeel("Windows Classic"));
                }
                case UI_THEME_FLATLAF_LIGHT -> {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                }
                case UI_THEME_FLATLAF_DARK -> {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                }
            }
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(RadioRec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initTicker() {
        appTicker = new AppTicker(1, TimeUnit.SECONDS);
        appTicker.addAppTickerListener(RadioRec.this);
    }

    public void initMain() {
        /* Create and display the form */
        EventQueue.invokeLater(() -> {
            mainFrame = new MainFrame();
            if (!getInstance().frameIcons.isEmpty()) {
                mainFrame.setIconImages(getInstance().frameIcons);
            }
            mainFrame.addWindowListener(mainFrame);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        });
    }

    public static String removeTrailingSlashes(String path) {
        if (path.endsWith("\\") || path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    public void setPrefs(String key, String def) {
        if (prefs.get(key, PROP_NULL).equals(PROP_NULL)) {
            prefs.put(key, def);
        }
    }

    public void savePrefs() {
        try {
            prefs.node("stations").removeNode();
            Preferences parent = prefs.node("stations");
            for (int i = 0; i < stationDirector.itemsCount(); i++) {
                Station station = stationDirector.getItem(i);
                String childPos = String.format("%04d", i);
                parent.put(childPos, station.stationFile.getAbsolutePath());
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(RadioRec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void restart() {
        System.out.println("RadioRec.restart");
        if (mainFrame != null) {
            mainFrame.setVisible(false);
        }
        initLook();
        initMain();
    }

    public void shutdown() {
        System.out.println("RadioRec.shutdown");
        savePrefs();
        appTicker.removeAppTickerListener(RadioRec.this);
        appActivity.removeAppActivityListener(getInstance());
        appActivity.shutdown();
        doneFlag.set(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        EventQueue.invokeLater(() -> {
            /* Start the application */
            RadioRec radioRec = RadioRec.getInstance();
            radioRec.initActivity(args);
            radioRec.initPrefs();
            radioRec.initFonts();
            radioRec.initDirs();
            radioRec.initStations();
            radioRec.initArgs(args);
            radioRec.initIcons();
            radioRec.initLook();
            radioRec.initTicker();
            radioRec.initMain();
        });

        while (!doneFlag.get()) {
            try {
                Thread ct = Thread.currentThread();
                synchronized (ct) {
                    ct.wait(200);
                    ct.notify();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(RadioRec.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.exit(0);
    }

    @Override
    public void appActivityArgsDelivered(AppActivity activity, String[] args) {
        for (String arg : args) {
            File file = new File(arg);
            if (file.exists()) {
                Station station = new Station(file);
                if (!stationDirector.containsItem(station)) {
                    stationDirector.addItem(station);
                }
            }
        }
    }

    @Override
    public void appTickerTick(AppTicker ticker, ZonedDateTime time) {
        if (stationDirector != null) {
            stationDirector.update(time);
        }
    }

}
