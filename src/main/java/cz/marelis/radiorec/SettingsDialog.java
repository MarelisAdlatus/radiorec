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

import java.awt.BorderLayout;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 * JavaFX settings dialog embedded in the current Swing shell.
 *
 * @author Marek Liška <adlatus@marelis.cz>
 */
public class SettingsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final transient RadioRec radioRec = RadioRec.getInstance();
    private final transient ResourceBundle bundle = ResourceBundle.getBundle("cz/marelis/radiorec/Bundle");

    /**
     * True when changed settings need application restart.
     */
    public boolean restartRequired = false;

    private TextField stationsDirTextField;
    private TextField recordsDirTextField;
    private CheckBox recordSubfoldersCheckBox;
    private TextField subfoldersFormatTextField;
    private TextField fileNameFormatTextField;
    private TextField recordTimeAppendTextField;
    private TextField tempDirTextField;
    private ComboBox<String> languageComboBox;
    private ComboBox<String> themeComboBox;
    private ComboBox<String> sizeComboBox;
    private ComboBox<String> timeZoneComboBox;
    private TextField timeFormatTextField;
    private TextField webBrowserPathTextField;
    private TextField webBrowserCommandTextField;

    private int themeIndex;
    private int sizeIndex;

    /**
     * Creates new JavaFX settings dialog.
     *
     * @param parent parent Swing frame
     */
    public SettingsDialog(java.awt.Frame parent) {
        super(parent, true);
        Platform.setImplicitExit(false);
        setTitle(bundle.getString("SettingsDialog.title"));
        setMinimumSize(new java.awt.Dimension(600, 360));
        setResizable(false);
        setLayout(new BorderLayout());

        JFXPanel fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);

        CountDownLatch sceneReady = new CountDownLatch(1);
        Platform.runLater(() -> {
            fxPanel.setScene(createScene(radioRec.prefs, radioRec.availableLocales));
            sceneReady.countDown();
        });
        awaitScene(sceneReady);
        pack();
    }

    private Scene createScene(Preferences prefs, List<Locale> locales) {
        VBox root = new VBox(8);
        root.setPadding(new Insets(8));

        TabPane tabs = new TabPane(
                tab(bundle.getString("SettingsDialog.jToggleButton1.text"), createFilePane()),
                tab(bundle.getString("SettingsDialog.jToggleButton2.text"), createTimePane()),
                tab(bundle.getString("SettingsDialog.jToggleButton3.text"), createBrowserPane()),
                tab(bundle.getString("SettingsDialog.jToggleButton4.text"), createAppearancePane()));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabs, Priority.ALWAYS);

        Button okButton = new Button(bundle.getString("SettingsDialog.okButton.text"));
        okButton.setDefaultButton(true);
        okButton.setOnAction(event -> {
            getDialog(radioRec.prefs);
            closeDialog();
        });

        Button cancelButton = new Button(bundle.getString("SettingsDialog.cancelButton.text"));
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(event -> closeDialog());

        HBox buttons = new HBox(6, okButton, cancelButton);
        buttons.setStyle("-fx-alignment: center-right;");

        root.getChildren().addAll(tabs, buttons);
        setDialog(prefs, locales);

        Scene scene = new Scene(root, 600, 330);
        applyTheme(scene, prefs.get(RadioRec.PROP_UI_THEME, RadioRec.DEFAULT_UI_THEME));
        themeComboBox.setOnAction(event -> applyTheme(scene, selectedTheme()));
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                closeDialog();
            }
        });
        return scene;
    }

    private void applyTheme(Scene scene, String theme) {
        scene.getStylesheets().removeIf(stylesheet ->
                stylesheet.endsWith("/ui-theme-light.css") || stylesheet.endsWith("/ui-theme-dark.css"));
        String cssPath = switch (theme) {
            case RadioRec.UI_THEME_DARK -> "ui-theme-dark.css";
            case RadioRec.UI_THEME_LIGHT -> "ui-theme-light.css";
            default -> "ui-theme-light.css";
        };
        URL cssUrl = SettingsDialog.class.getResource(cssPath);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    private String selectedTheme() {
        return switch (themeComboBox.getSelectionModel().getSelectedIndex()) {
            case 1 -> RadioRec.UI_THEME_DARK;
            case 0 -> RadioRec.UI_THEME_LIGHT;
            default -> RadioRec.DEFAULT_UI_THEME;
        };
    }

    private Tab tab(String title, GridPane content) {
        return new Tab(title, content);
    }

    private GridPane createFilePane() {
        GridPane pane = grid();
        stationsDirTextField = new TextField();
        recordsDirTextField = new TextField();
        recordSubfoldersCheckBox = new CheckBox(bundle.getString("SettingsDialog.recordSubfoldersCheckBox.text"));
        subfoldersFormatTextField = new TextField();
        fileNameFormatTextField = new TextField();
        recordTimeAppendTextField = new TextField();
        tempDirTextField = new TextField();

        addDirectoryRow(pane, 0, bundle.getString("SettingsDialog.jLabel1.text"), stationsDirTextField,
                bundle.getString("SettingsDialog.stationsDirButton.text"), "Select stations directory");
        addDirectoryRow(pane, 2, bundle.getString("SettingsDialog.jLabel2.text"), recordsDirTextField,
                bundle.getString("SettingsDialog.recordsDirButton.text"), "Select records directory");
        pane.add(recordSubfoldersCheckBox, 0, 4, 3, 1);
        addTextRow(pane, 5, bundle.getString("SettingsDialog.jLabel10.text"), subfoldersFormatTextField);
        addTextRow(pane, 7, bundle.getString("SettingsDialog.jLabel3.text"), fileNameFormatTextField);
        addDirectoryRow(pane, 9, bundle.getString("SettingsDialog.jLabel4.text"), tempDirTextField,
                bundle.getString("SettingsDialog.tempDirButton.text"), "Select temporary directory");
        return pane;
    }

    private GridPane createTimePane() {
        GridPane pane = grid();
        timeZoneComboBox = new ComboBox<>();
        timeZoneComboBox.setMaxWidth(Double.MAX_VALUE);
        Button detectTimeZoneButton = new Button(bundle.getString("SettingsDialog.detectTimeZoneButton.text"));
        detectTimeZoneButton.setOnAction(event -> {
            String zone = System.getProperty("user.timezone");
            timeZoneComboBox.getSelectionModel().select((zone != null) ? zone : RadioRec.DEFAULT_TIME_ZONE_ID);
        });
        timeFormatTextField = new TextField();
        recordTimeAppendTextField = new TextField();

        pane.add(new Label(bundle.getString("SettingsDialog.jLabel6.text")), 0, 0, 2, 1);
        pane.add(timeZoneComboBox, 0, 1);
        pane.add(detectTimeZoneButton, 1, 1);
        addTextRow(pane, 2, bundle.getString("SettingsDialog.jLabel7.text"), timeFormatTextField);
        addTextRow(pane, 4, bundle.getString("SettingsDialog.jLabel11.text"), recordTimeAppendTextField);
        return pane;
    }

    private GridPane createBrowserPane() {
        GridPane pane = grid();
        webBrowserPathTextField = new TextField();
        webBrowserCommandTextField = new TextField();
        addFileRow(pane, 0, bundle.getString("SettingsDialog.jLabel8.text"), webBrowserPathTextField,
                bundle.getString("SettingsDialog.webBroserPathButton.text"), "Select web browser");
        addTextRow(pane, 2, bundle.getString("SettingsDialog.jLabel9.text"), webBrowserCommandTextField);
        return pane;
    }

    private GridPane createAppearancePane() {
        GridPane pane = grid();
        languageComboBox = new ComboBox<>();
        themeComboBox = new ComboBox<>();
        sizeComboBox = new ComboBox<>();
        addComboRow(pane, 0, bundle.getString("SettingsDialog.jLabel12.text"), languageComboBox);
        addComboRow(pane, 2, bundle.getString("SettingsDialog.jLabel5.text"), themeComboBox);
        addComboRow(pane, 4, bundle.getString("SettingsDialog.jLabel13.text"), sizeComboBox);
        return pane;
    }

    private GridPane grid() {
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(8));
        pane.setHgap(6);
        pane.setVgap(6);
        ColumnConstraints textColumn = new ColumnConstraints();
        textColumn.setHgrow(Priority.ALWAYS);
        ColumnConstraints buttonColumn = new ColumnConstraints();
        pane.getColumnConstraints().addAll(textColumn, buttonColumn);
        return pane;
    }

    private void addTextRow(GridPane pane, int row, String label, TextField field) {
        field.setMaxWidth(Double.MAX_VALUE);
        pane.add(new Label(label), 0, row, 2, 1);
        pane.add(field, 0, row + 1, 2, 1);
    }

    private void addComboRow(GridPane pane, int row, String label, ComboBox<String> comboBox) {
        comboBox.setMaxWidth(Double.MAX_VALUE);
        pane.add(new Label(label), 0, row, 2, 1);
        pane.add(comboBox, 0, row + 1, 2, 1);
    }

    private void addDirectoryRow(GridPane pane, int row, String label, TextField field, String buttonLabel, String title) {
        addChooserRow(pane, row, label, field, buttonLabel, () -> chooseDirectory(title));
    }

    private void addFileRow(GridPane pane, int row, String label, TextField field, String buttonLabel, String title) {
        addChooserRow(pane, row, label, field, buttonLabel, () -> chooseFile(title));
    }

    private void addChooserRow(GridPane pane, int row, String label, TextField field, String buttonLabel, Chooser chooser) {
        field.setMaxWidth(Double.MAX_VALUE);
        Button button = new Button(buttonLabel);
        button.setOnAction(event -> {
            File file = chooser.choose();
            if (file != null) {
                field.setText(file.getAbsolutePath());
            }
        });
        pane.add(new Label(label), 0, row, 2, 1);
        pane.add(field, 0, row + 1);
        pane.add(button, 1, row + 1);
    }

    private File chooseDirectory(String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        File initialDirectory = new File(RadioRec.DEFAULT_USER_DIR);
        if (initialDirectory.isDirectory()) {
            chooser.setInitialDirectory(initialDirectory);
        }
        return showChooser(() -> chooser.showDialog(null));
    }

    private File chooseFile(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        File initialDirectory = new File(RadioRec.DEFAULT_USER_DIR);
        if (initialDirectory.isDirectory()) {
            chooser.setInitialDirectory(initialDirectory);
        }
        return showChooser(() -> chooser.showOpenDialog(null));
    }

    private File showChooser(Chooser chooser) {
        boolean alwaysOnTop = isAlwaysOnTop();
        setAlwaysOnTopOnEdt(false);
        try {
            return chooser.choose();
        } finally {
            setAlwaysOnTopOnEdt(alwaysOnTop);
            toFrontOnEdt();
        }
    }

    private void setAlwaysOnTopOnEdt(boolean value) {
        runOnEdtAndWait(() -> setAlwaysOnTop(value));
    }

    private void toFrontOnEdt() {
        runOnEdtAndWait(this::toFront);
    }

    private void runOnEdtAndWait(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (Exception ex) {
            throw new IllegalStateException("Swing dialog update failed", ex);
        }
    }

    private void setDialog(Preferences prefs, List<Locale> locales) {
        stationsDirTextField.setText(prefs.get(RadioRec.PROP_STATIONS_DIR, RadioRec.DEFAULT_STATIONS_DIR));
        recordsDirTextField.setText(prefs.get(RadioRec.PROP_RECORDS_DIR, RadioRec.DEFAULT_RECORDS_DIR));
        recordSubfoldersCheckBox.setSelected(
                "true".equals(prefs.get(RadioRec.PROP_RECORDS_SUBFOLDERS, RadioRec.DEFAULT_RECORDS_SUBFOLDERS)));
        subfoldersFormatTextField.setText(
                prefs.get(RadioRec.PROP_RECORDS_SUBFOLDERS_FORMAT, RadioRec.DEFAULT_RECORDS_SUBFOLDERS_FORMAT));
        fileNameFormatTextField.setText(
                prefs.get(RadioRec.PROP_RECORDS_FILENAME_FORMAT, RadioRec.DEFAULT_RECORDS_FILENAME_FORMAT));
        recordTimeAppendTextField.setText(
                prefs.get(RadioRec.PROP_RECORDS_TIME_APPEND, RadioRec.DEFAULT_RECORDS_TIME_APPEND));
        tempDirTextField.setText(prefs.get(RadioRec.PROP_TEMP_DIR, RadioRec.DEFAULT_TEMP_DIR));

        Locale currentLocale = Locale.getDefault();
        String currentLocaleString = localeDisplayName(currentLocale);
        String selectedLocaleDisplayName = null;
        for (Locale locale : locales) {
            String displayName = localeDisplayName(locale);
            languageComboBox.getItems().add(displayName);
            if (displayName.equals(currentLocaleString)) {
                selectedLocaleDisplayName = displayName;
            }
        }
        if (selectedLocaleDisplayName != null) {
            languageComboBox.getSelectionModel().select(selectedLocaleDisplayName);
        }

        themeComboBox.getItems().addAll(
                radioRec.currentBundle.getString("ComboBox.Item.UI.Theme.Light"),
                radioRec.currentBundle.getString("ComboBox.Item.UI.Theme.Dark"));
        String theme = prefs.get(RadioRec.PROP_UI_THEME, RadioRec.DEFAULT_UI_THEME);
        switch (theme) {
            case RadioRec.UI_THEME_LIGHT -> themeIndex = 0;
            case RadioRec.UI_THEME_DARK -> themeIndex = 1;
            default -> themeIndex = 0;
        }
        themeComboBox.getSelectionModel().select(themeIndex);

        sizeComboBox.getItems().addAll(
                radioRec.currentBundle.getString("ComboBox.Item.UI.Size.Small"),
                radioRec.currentBundle.getString("ComboBox.Item.UI.Size.Medium"),
                radioRec.currentBundle.getString("ComboBox.Item.UI.Size.Large"));
        String size = prefs.get(RadioRec.PROP_UI_SIZE, RadioRec.DEFAULT_UI_SIZE);
        switch (size) {
            case RadioRec.UI_SIZE_SMALL -> sizeIndex = 0;
            case RadioRec.UI_SIZE_MEDIUM -> sizeIndex = 1;
            case RadioRec.UI_SIZE_LARGE -> sizeIndex = 2;
            default -> sizeIndex = 1;
        }
        sizeComboBox.getSelectionModel().select(sizeIndex);

        timeZoneComboBox.setItems(FXCollections.observableArrayList(TimeZone.getAvailableIDs()));
        timeZoneComboBox.getSelectionModel().select(
                prefs.get(RadioRec.PROP_TIME_ZONE_ID, RadioRec.DEFAULT_TIME_ZONE_ID));
        timeFormatTextField.setText(prefs.get(RadioRec.PROP_TIME_FORMAT, RadioRec.DEFAULT_TIME_FORMAT));
        webBrowserPathTextField.setText(prefs.get(RadioRec.PROP_WEB_BROWSER_PATH, ""));
        webBrowserCommandTextField.setText(
                prefs.get(RadioRec.PROP_WEB_BROWSER_COMMAND, RadioRec.DEFAULT_WEB_BROWSER_COMMAND));
    }

    private void getDialog(Preferences prefs) {
        prefs.put(RadioRec.PROP_STATIONS_DIR, stationsDirTextField.getText());
        prefs.put(RadioRec.PROP_RECORDS_DIR, recordsDirTextField.getText());
        prefs.put(RadioRec.PROP_RECORDS_SUBFOLDERS, String.valueOf(recordSubfoldersCheckBox.isSelected()));
        prefs.put(RadioRec.PROP_RECORDS_SUBFOLDERS_FORMAT, subfoldersFormatTextField.getText());
        prefs.put(RadioRec.PROP_RECORDS_FILENAME_FORMAT, fileNameFormatTextField.getText());
        prefs.put(RadioRec.PROP_RECORDS_TIME_APPEND, recordTimeAppendTextField.getText());
        prefs.put(RadioRec.PROP_TEMP_DIR, tempDirTextField.getText());

        String selectedLocaleDisplayName = languageComboBox.getSelectionModel().getSelectedItem();
        Locale selectedLocale = getSelectedLocale(selectedLocaleDisplayName);
        Locale currentLocale = Locale.getDefault();
        if (!selectedLocale.equals(currentLocale)) {
            restartRequired = true;
        }
        prefs.put(RadioRec.PROP_UI_LOCALE, selectedLocale.toLanguageTag());

        int selectedTheme = themeComboBox.getSelectionModel().getSelectedIndex();
        if (selectedTheme != themeIndex) {
            restartRequired = true;
        }
        switch (selectedTheme) {
            case 0 -> prefs.put(RadioRec.PROP_UI_THEME, RadioRec.UI_THEME_LIGHT);
            case 1 -> prefs.put(RadioRec.PROP_UI_THEME, RadioRec.UI_THEME_DARK);
            default -> prefs.put(RadioRec.PROP_UI_THEME, RadioRec.DEFAULT_UI_THEME);
        }

        int selectedSize = sizeComboBox.getSelectionModel().getSelectedIndex();
        if (selectedSize != sizeIndex) {
            restartRequired = true;
        }
        switch (selectedSize) {
            case 0 -> prefs.put(RadioRec.PROP_UI_SIZE, RadioRec.UI_SIZE_SMALL);
            case 1 -> prefs.put(RadioRec.PROP_UI_SIZE, RadioRec.UI_SIZE_MEDIUM);
            case 2 -> prefs.put(RadioRec.PROP_UI_SIZE, RadioRec.UI_SIZE_LARGE);
            default -> prefs.put(RadioRec.PROP_UI_SIZE, RadioRec.DEFAULT_UI_SIZE);
        }

        prefs.put(RadioRec.PROP_TIME_ZONE_ID, timeZoneComboBox.getSelectionModel().getSelectedItem());
        prefs.put(RadioRec.PROP_TIME_FORMAT, timeFormatTextField.getText());
        prefs.put(RadioRec.PROP_WEB_BROWSER_PATH, webBrowserPathTextField.getText());
        prefs.put(RadioRec.PROP_WEB_BROWSER_COMMAND, webBrowserCommandTextField.getText());
    }

    private Locale getSelectedLocale(String displayName) {
        for (Locale locale : radioRec.availableLocales) {
            if (localeDisplayName(locale).equals(displayName)) {
                return locale;
            }
        }
        return Locale.getDefault();
    }

    private String localeDisplayName(Locale locale) {
        return locale.getDisplayLanguage() + " (" + locale.getDisplayCountry() + ")";
    }

    private void closeDialog() {
        SwingUtilities.invokeLater(() -> setVisible(false));
    }

    private void awaitScene(CountDownLatch sceneReady) {
        try {
            sceneReady.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("JavaFX settings dialog initialization interrupted", ex);
        }
    }

    @FunctionalInterface
    private interface Chooser {
        File choose();
    }
}
