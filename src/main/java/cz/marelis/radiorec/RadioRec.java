/*
 * Copyright 2025 Marek Liška <adlatus@marelis.cz>.
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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Marek Liška <adlatus@marelis.cz>
 */
public class RadioRec implements AppActivityListener, AppTickerListener {

    // TODO: Stations folder for saved station

    /**
     *
     */
    public final static String PROP_STATIONS_DIR = "stations-dir";

    /**
     *
     */
    public final static String PROP_RECORDS_DIR = "records-dir";

    /**
     *
     */
    public final static String PROP_RECORDS_SUBFOLDERS = "records-subfolders";

    /**
     *
     */
    public final static String PROP_RECORDS_SUBFOLDERS_FORMAT = "records-subfolders-format";

    /**
     *
     */
    public final static String PROP_RECORDS_FILENAME_FORMAT = "records-filename-format";

    /**
     *
     */
    public final static String PROP_RECORDS_TIME_APPEND = "records-time-append";

    /**
     *
     */
    public final static String PROP_TEMP_DIR = "temp-dir";

    /**
     *
     */
    public final static String PROP_UI_LOCALE = "ui-locale";

    /**
     *
     */
    public final static String PROP_UI_THEME = "ui-theme";

    /**
     *
     */
    public final static String PROP_UI_SIZE = "ui-size";

    /**
     *
     */
    public final static String PROP_UI_THEME_FONT_SIZE = "ui-theme-font-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_MIN_DIVIDER_LOCATION = "ui-mainform-min-divider-location";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_STATIONS_TABLE_ROW_HEIGHT = "ui-mainform-stations-table-row-height";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_STATIONS_TABLE_ROW_NAME_SIZE = "ui-mainform-stations-table-row-name-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_STATIONS_TABLE_ROW_LINK_SIZE = "ui-mainform-stations-table-row-link-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_STATIONS_TABLE_ROW_PLAY_SIZE = "ui-mainform-stations-table-row-play-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_STATIONS_TABLE_ROW_RECORD_SIZE = "ui-mainform-stations-table-row-record-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_PROGRAM_TABLE_ROW_HEIGHT = "ui-mainform-program-table-row-height";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_PROGRAM_TABLE_ROW_TITLE_SIZE = "ui-mainform-program-table-row-title-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_PROGRAM_TABLE_ROW_COMMENT_SIZE = "ui-mainform-program-table-row-comment-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_PROGRAM_TABLE_ROW_TIME_SIZE = "ui-mainform-program-table-row-time-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_PROGRAM_TABLE_ROW_STATUS_SIZE = "ui-mainform-program-table-row-status-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_PROGRAM_TABLE_ROW_DURATION_SIZE = "ui-mainform-program-table-row-duration-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_RECORD_TABLE_ROW_HEIGHT = "ui-mainform-record-table-row-height";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_RECORD_TABLE_ROW_TITLE_SIZE = "ui-mainform-record-table-row-title-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_RECORD_TABLE_ROW_COMMENT_SIZE = "ui-mainform-record-table-row-comment-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_RECORD_TABLE_ROW_TIME_SIZE = "ui-mainform-record-table-row-time-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_RECORD_TABLE_ROW_STATUS_SIZE = "ui-mainform-record-table-row-status-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_RECORD_TABLE_ROW_DURATION_SIZE = "ui-mainform-record-table-row-duration-size";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_LAST_LEFT = "ui-mainform-last-left";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_LAST_TOP = "ui-mainform-last-top";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_LAST_WIDTH = "ui-mainform-last-width";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_LAST_HEIGHT = "ui-mainform-last-height";

    /**
     *
     */
    public final static String PROP_UI_MAINFORM_LAST_DIVIDER_LOCATION = "ui-mainform-last-divider-location";

    /**
     *
     */
    public final static String PROP_TIME_ZONE_ID = "time-zone-id";

    /**
     *
     */
    public final static String PROP_TIME_FORMAT = "time-format";

    /**
     *
     */
    public final static String PROP_WEB_BROWSER_PATH = "web-browser-path";

    /**
     *
     */
    public final static String PROP_WEB_BROWSER_COMMAND = "web-browser-command";

    /**
     *
     */
    public final static String PROP_PARSE_HTML_THEME = "parse-html-theme";

    /**
     *
     */
    public final static String PROP_NULL = "null";

    /**
     *
     */
    public final static String UI_SIZE_SMALL = "ui-size-small";

    /**
     *
     */
    public final static String UI_SIZE_MEDIUM = "ui-size-medium";

    /**
     *
     */
    public final static String UI_SIZE_LARGE = "ui-size-large";

    /**
     *
     */
    public final static String UI_THEME_LIGHT = "ui-theme-light";

    /**
     *
     */
    public final static String UI_THEME_DARK = "ui-theme-dark";

    /**
     *
     */
    public final static String DEFAULT_USER_DIR
            = new JFileChooser().getFileSystemView().getDefaultDirectory().getAbsolutePath();

    /**
     *
     */
    public final static String DEFAULT_STATIONS_DIR
            = DEFAULT_USER_DIR.concat(File.separator).concat("Marelis")
                    .concat(File.separator).concat("RadioRec");

    /**
     *
     */
    public final static String DEFAULT_RECORDS_DIR
            = DEFAULT_STATIONS_DIR.concat(File.separator).concat("Records");

    /**
     *
     */
    public final static String DEFAULT_RECORDS_SUBFOLDERS = String.valueOf(false);

    /**
     *
     */
    public final static String DEFAULT_RECORDS_SUBFOLDERS_FORMAT
            = "{station}" + File.separator + "{year}" + File.separator + "{month}";

    /**
     *
     */
    public final static String DEFAULT_RECORDS_FILENAME_FORMAT
            = "{year}{month}{day} {hour}{minute} {station} - {title}";

    /**
     *
     */
    public final static String DEFAULT_RECORDS_TIME_APPEND = "00m00s";

    /**
     *
     */
    public final static String DEFAULT_TEMP_DIR
            = RadioRec.removeTrailingSlashes(System.getProperty("java.io.tmpdir"));

    /**
     *
     */
    public final static String DEFAULT_UI_LOCALE = "en-US";

    /**
     *
     */
    public final static String DEFAULT_UI_THEME = UI_THEME_LIGHT;

    /**
     *
     */
    public final static String DEFAULT_UI_SIZE = UI_SIZE_MEDIUM;

    /**
     *
     */
    public final static float DEFAULT_UI_THEME_FONT_SIZE = 14f;

    /**
     *
     */
    public final static int DEFAULT_UI_MAINFORM_MIN_DIVIDER_LOCATION = 200;
    
    /**
     *
     */
    public final static int DEFAULT_UI_MAINFORM_STATIONS_TABLE_ROW_HEIGHT = 60;

    /**
     *
     */
    public final static int DEFAULT_UI_MAINFORM_PROGRAM_TABLE_ROW_HEIGHT = 70;

    /**
     *
     */
    public final static int DEFAULT_UI_MAINFORM_RECORD_TABLE_ROW_HEIGHT = 70;
    
    /**
     *
     */
    public final static String DEFAULT_TIME_ZONE_ID = "UTC";

    /**
     *
     */
    public final static String DEFAULT_TIME_FORMAT = "dd.MM.yy HH:mm:ss";

    /**
     *
     */
    public final static String DEFAULT_WEB_BROWSER_COMMAND
            = "--headless --disable-gpu --disable-software-rasterizer --dump-dom";

    /**
     *
     */
    public final static String DEFAULT_PARSE_HTML_THEME = "default";

    /**
     *
     */
    public final Preferences prefs = Preferences.userNodeForPackage(RadioRec.class);

    /**
     *
     */
    public final List<String> availableBundles = new ArrayList<>();

    /**
     *
     */
    public final List<Locale> availableLocales = new ArrayList<>();

    /**
     *
     */
    public ResourceBundle currentBundle;

    /**
     *
     */
    public Font fontAwesomeRegular;

    /**
     *
     */
    public Font fontAwesomeSolid;

    /**
     *
     */
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

    /**
     *
     * @return
     */
    public static RadioRec getInstance() {
        return Loader.INSTANCE;
    }

    /**
     *
     * @param args
     */
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

    /**
     *
     */
    public void initLocales() {
        Pattern localePattern = Pattern.compile("Bundle_(.+)\\.properties$");
        // Load the list of bundle filenames from available-bundles.txt
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("cz/marelis/radiorec/available-bundles.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                availableBundles.add(line);
                // Use regex to extract the locale part
                Matcher matcher = localePattern.matcher(line);
                if (matcher.find()) {
                    String localePart = matcher.group(1);
                    availableLocales.add(Locale.forLanguageTag(localePart.replace('_', '-')));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(RadioRec.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }

    /**
     *
     */
    public void initPrefs() {
        setPrefs(PROP_STATIONS_DIR, DEFAULT_STATIONS_DIR);
        setPrefs(PROP_RECORDS_DIR, DEFAULT_RECORDS_DIR);
        setPrefs(PROP_RECORDS_SUBFOLDERS, DEFAULT_RECORDS_SUBFOLDERS);
        setPrefs(PROP_RECORDS_SUBFOLDERS_FORMAT, DEFAULT_RECORDS_SUBFOLDERS_FORMAT);
        setPrefs(PROP_RECORDS_FILENAME_FORMAT, DEFAULT_RECORDS_FILENAME_FORMAT);
        setPrefs(PROP_RECORDS_TIME_APPEND, DEFAULT_RECORDS_TIME_APPEND);
        setPrefs(PROP_TEMP_DIR, DEFAULT_TEMP_DIR);
        setPrefs(PROP_UI_THEME, DEFAULT_UI_THEME);
        setPrefs(PROP_UI_SIZE, DEFAULT_UI_SIZE);
        setPrefs(PROP_UI_THEME_FONT_SIZE, String.valueOf(DEFAULT_UI_THEME_FONT_SIZE));
        setPrefs(PROP_UI_MAINFORM_MIN_DIVIDER_LOCATION, String.valueOf(DEFAULT_UI_MAINFORM_MIN_DIVIDER_LOCATION));
        setPrefs(PROP_UI_MAINFORM_STATIONS_TABLE_ROW_HEIGHT, String.valueOf(DEFAULT_UI_MAINFORM_STATIONS_TABLE_ROW_HEIGHT));
        setPrefs(PROP_UI_MAINFORM_PROGRAM_TABLE_ROW_HEIGHT, String.valueOf(DEFAULT_UI_MAINFORM_PROGRAM_TABLE_ROW_HEIGHT));
        setPrefs(PROP_UI_MAINFORM_RECORD_TABLE_ROW_HEIGHT, String.valueOf(DEFAULT_UI_MAINFORM_RECORD_TABLE_ROW_HEIGHT));
        setPrefs(PROP_TIME_ZONE_ID, DEFAULT_TIME_ZONE_ID);
        setPrefs(PROP_TIME_FORMAT, DEFAULT_TIME_FORMAT);
        setPrefs(PROP_WEB_BROWSER_PATH, ProgramDirector.getBrowserPath());
        setPrefs(PROP_WEB_BROWSER_COMMAND, DEFAULT_WEB_BROWSER_COMMAND);
        setPrefs(PROP_PARSE_HTML_THEME, DEFAULT_PARSE_HTML_THEME);
    }

    /**
     *
     */
    public void initFonts() {
        InputStream is;
        try {
            is = RadioRec.class.getClassLoader()
                    .getResourceAsStream("cz/marelis/radiorec/fonts/Font Awesome 6 Free-Regular-400.otf");
            fontAwesomeRegular = Font.createFont(Font.TRUETYPE_FONT, is);

            is = RadioRec.class.getClassLoader()
                    .getResourceAsStream("cz/marelis/radiorec/fonts/Font Awesome 6 Free-Solid-900.otf");
            fontAwesomeSolid = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(RadioRec.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(2);
        }
    }

    /**
     *
     */
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

    /**
     *
     */
    public void initStations() {
        try {
            String[] keys = RadioRec.getInstance().prefs.node("stations").keys();
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

    /**
     *
     * @param args
     */
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

    /**
     *
     */
    public void initIcons() {
        // Application main icon
        int sizes[] = {16, 20, 24, 28, 30, 31, 32, 40, 42, 47, 48, 56, 57, 60,
            63, 64, 72, 84, 96, 120, 128, 144, 152, 195, 228, 256, 512};
        for (int size : sizes) {
            URL url = MainFrame.class.getClassLoader()
                    .getResource(String.format("cz/marelis/radiorec/icons/RadioRec-%d.png", size));
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                frameIcons.add(icon.getImage());
            }
        }
    }

    /**
     *
     */
    public void initLocale() {
        // Define fallback and UI locales
        Locale fallbackLocale = Locale.forLanguageTag(DEFAULT_UI_LOCALE);
        String savedLocale = prefs.get(PROP_UI_LOCALE, "");
        Locale uiLocale = savedLocale.isEmpty()
                ? Locale.getDefault()
                : Locale.forLanguageTag(savedLocale);
        // Check if the UI locale is available in the list of locales
        if (!availableLocales.contains(uiLocale)) {
            // If UI locale is not available, check if fallback locale is available
            if (availableLocales.contains(fallbackLocale)) {
                uiLocale = fallbackLocale;
            } else {
                // If neither the UI nor the fallback locale is available, use the default locale
                uiLocale = Locale.getDefault();
            }
        }
        // Save the selected locale to preferences
        prefs.put(PROP_UI_LOCALE, uiLocale.toLanguageTag());
        // Set the application-wide default locale if it differs from the current default
        if (!Locale.getDefault().equals(uiLocale)) {
            Locale.setDefault(uiLocale);
        }
        // Initialize the global ResourceBundle for the selected locale
        try {
            currentBundle = ResourceBundle.getBundle("cz.marelis.radiorec.Bundle", uiLocale);
        } catch (MissingResourceException ex) {
            Logger.getLogger(RadioRec.class.getName()).log(Level.WARNING,
                    "Resource bundle for locale {0} not found. Using default.",
                    uiLocale);
            currentBundle = ResourceBundle.getBundle("cz.marelis.radiorec.Bundle", Locale.getDefault());
        }
    }

    /**
     *
     */
    public void initLook() {
        // Theme
        try {
            switch (prefs.get(PROP_UI_THEME, DEFAULT_UI_THEME)) {
                case UI_THEME_LIGHT -> {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                }
                case UI_THEME_DARK -> {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                }
            }
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(RadioRec.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Size
        String size = prefs.get(PROP_UI_SIZE, DEFAULT_UI_SIZE);
        switch (size) {
            case UI_SIZE_SMALL -> {
                prefs.putFloat(PROP_UI_THEME_FONT_SIZE, 12f);
                prefs.putInt(PROP_UI_MAINFORM_MIN_DIVIDER_LOCATION, 100);
                prefs.putInt(PROP_UI_MAINFORM_STATIONS_TABLE_ROW_HEIGHT, 50);
                prefs.putInt(PROP_UI_MAINFORM_PROGRAM_TABLE_ROW_HEIGHT, 60);
                prefs.putInt(PROP_UI_MAINFORM_RECORD_TABLE_ROW_HEIGHT, 60);
            }
            case UI_SIZE_MEDIUM -> {
                prefs.putFloat(RadioRec.PROP_UI_THEME_FONT_SIZE, 14f);
                prefs.putInt(PROP_UI_MAINFORM_MIN_DIVIDER_LOCATION, 200);
                prefs.putInt(PROP_UI_MAINFORM_STATIONS_TABLE_ROW_HEIGHT, 60);
                prefs.putInt(PROP_UI_MAINFORM_PROGRAM_TABLE_ROW_HEIGHT, 70);
                prefs.putInt(PROP_UI_MAINFORM_RECORD_TABLE_ROW_HEIGHT, 70);
            }
            case UI_SIZE_LARGE -> {
                prefs.putFloat(RadioRec.PROP_UI_THEME_FONT_SIZE, 16f);
                prefs.putInt(PROP_UI_MAINFORM_MIN_DIVIDER_LOCATION, 400);
                prefs.putInt(PROP_UI_MAINFORM_STATIONS_TABLE_ROW_HEIGHT, 70);
                prefs.putInt(PROP_UI_MAINFORM_PROGRAM_TABLE_ROW_HEIGHT, 80);
                prefs.putInt(PROP_UI_MAINFORM_RECORD_TABLE_ROW_HEIGHT, 80);
            }
        }

        Font originalFont = UIManager.getFont("defaultFont");
        float fontSize = prefs.getFloat(PROP_UI_THEME_FONT_SIZE, DEFAULT_UI_THEME_FONT_SIZE);
        Font newFont = originalFont.deriveFont(fontSize);
        UIManager.put("defaultFont", newFont);
    }

    /**
     *
     */
    public void initTicker() {
        appTicker = new AppTicker(1, TimeUnit.SECONDS);
        appTicker.addAppTickerListener(RadioRec.this);
    }

    /**
     *
     */
    public void initMain() {
        /* Create and display the form */
        EventQueue.invokeLater(() -> {
            mainFrame = new MainFrame();
            if (!getInstance().frameIcons.isEmpty()) {
                mainFrame.setIconImages(getInstance().frameIcons);
            }
            mainFrame.addWindowListener(mainFrame);
            mainFrame.setSize(new Dimension(1024, 800));
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        });
    }

    /**
     *
     * @param path
     * @return
     */
    public static String removeTrailingSlashes(String path) {
        if (path.endsWith("\\") || path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     *
     * @param key
     * @param def
     */
    public void setPrefs(String key, String def) {
        if (prefs.get(key, PROP_NULL).equals(PROP_NULL)) {
            prefs.put(key, def);
        }
    }

    /**
     *
     */
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

    /**
     *
     */
    public void restart() {
        System.out.println("RadioRec.restart");
        if (mainFrame != null) {
            mainFrame.setVisible(false);
        }
        initLocale();
        initLook();
        initMain();
    }

    /**
     *
     */
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
            radioRec.initLocales();
            radioRec.initPrefs();
            radioRec.initFonts();
            radioRec.initDirs();
            radioRec.initStations();
            radioRec.initArgs(args);
            radioRec.initIcons();
            radioRec.initLocale();
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

    /**
     *
     * @param activity
     * @param args
     */
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

    /**
     *
     * @param ticker
     * @param time
     */
    @Override
    public void appTickerTick(AppTicker ticker, ZonedDateTime time) {
        if (stationDirector != null) {
            stationDirector.update(time);
        }
    }

}
