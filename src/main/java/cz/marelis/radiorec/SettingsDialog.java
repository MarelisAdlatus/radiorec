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

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

/**
 *
 * @author Marek Liška <adlatus@marelis.cz>
 */
public class SettingsDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    private final transient RadioRec radioRec = RadioRec.getInstance();
    
    /**
     *
     */
    public boolean restartRequired = false;

    private int themeIndex;
    private int sizeIndex;

    /**
     * Creates new form SettingsDialog
     *
     * @param parent
     */
    public SettingsDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        setDialog(radioRec.prefs, RadioRec.getInstance().availableLocales);
    }

    private void setDialog(Preferences prefs, List<Locale> locales) {
        // Close the dialog after pressing the ESCAPE key
        getRootPane().registerKeyboardAction(e -> {
            setVisible(false);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        // set values
        stationsDirTextField.setText(prefs.get(RadioRec.PROP_STATIONS_DIR, RadioRec.DEFAULT_STATIONS_DIR));
        recordsDirTextField.setText(prefs.get(RadioRec.PROP_RECORDS_DIR, RadioRec.DEFAULT_RECORDS_DIR));
        recordSubfoldersCheckBox.setSelected(
                "true".equals(prefs.get(RadioRec.PROP_RECORDS_SUBFOLDERS, RadioRec.DEFAULT_RECORDS_SUBFOLDERS)));
        subfoldersFormatTextField.setText(prefs.get(RadioRec.PROP_RECORDS_SUBFOLDERS_FORMAT, RadioRec.DEFAULT_RECORDS_SUBFOLDERS_FORMAT));
        fileNameFormatTextField.setText(prefs.get(RadioRec.PROP_RECORDS_FILENAME_FORMAT, RadioRec.DEFAULT_RECORDS_FILENAME_FORMAT));
        recordTimeAppendTextField.setText(prefs.get(RadioRec.PROP_RECORDS_TIME_APPEND, RadioRec.DEFAULT_RECORDS_TIME_APPEND));
        tempDirTextField.setText(prefs.get(RadioRec.PROP_TEMP_DIR, RadioRec.DEFAULT_TEMP_DIR));
        // Get the current locale (either from prefs or system default)
        Locale currentLocale = Locale.getDefault();
        String currentLocaleString = currentLocale.getDisplayLanguage() + " (" + currentLocale.getDisplayCountry() + ")";
        // Initialize the selected item to null first
        String selectedLocaleDisplayName = null;
        // Loop through available locales and format the language and country
        for (Locale locale : locales) {
            String displayName = locale.getDisplayLanguage() + " (" + locale.getDisplayCountry() + ")";
            languageComboBox.addItem(displayName);
            // Store the matching display name to set it as the selected item later
            if (displayName.equals(currentLocaleString)) {
                selectedLocaleDisplayName = displayName;
            }
        }
        // After adding all items to the comboBox, set the selected item
        if (selectedLocaleDisplayName != null) {
            languageComboBox.setSelectedItem(selectedLocaleDisplayName);
        }
        // UI Theme
        themeComboBox.addItem(radioRec.currentBundle.getString("ComboBox.Item.UI.Theme.Light"));
        themeComboBox.addItem(radioRec.currentBundle.getString("ComboBox.Item.UI.Theme.Dark"));
        String theme = prefs.get(RadioRec.PROP_UI_THEME, RadioRec.DEFAULT_UI_THEME);
        switch (theme) {
            case RadioRec.UI_THEME_LIGHT ->
                themeIndex = 0;
            case RadioRec.UI_THEME_DARK ->
                themeIndex = 1;
        }
        themeComboBox.setSelectedIndex(themeIndex);
        // UI Size
        sizeComboBox.addItem(radioRec.currentBundle.getString("ComboBox.Item.UI.Size.Small"));
        sizeComboBox.addItem(radioRec.currentBundle.getString("ComboBox.Item.UI.Size.Medium"));
        sizeComboBox.addItem(radioRec.currentBundle.getString("ComboBox.Item.UI.Size.Large"));
        String size = prefs.get(RadioRec.PROP_UI_SIZE, RadioRec.DEFAULT_UI_SIZE);
        switch (size) {
            case RadioRec.UI_SIZE_SMALL ->
                sizeIndex = 0;
            case RadioRec.UI_SIZE_MEDIUM ->
                sizeIndex = 1;
            case RadioRec.UI_SIZE_LARGE ->
                sizeIndex = 2;
        }
        sizeComboBox.setSelectedIndex(sizeIndex);
        // Time
        timeZoneComboBox.setModel(new DefaultComboBoxModel<>(TimeZone.getAvailableIDs()));
        timeZoneComboBox.setSelectedItem(prefs.get(RadioRec.PROP_TIME_ZONE_ID, RadioRec.DEFAULT_TIME_ZONE_ID));
        timeFormatTextField.setText(prefs.get(RadioRec.PROP_TIME_FORMAT, RadioRec.DEFAULT_TIME_FORMAT));
        webBrowserPathTextField.setText(prefs.get(RadioRec.PROP_WEB_BROWSER_PATH, ""));
        webBrowserCommandTextField
                .setText(prefs.get(RadioRec.PROP_WEB_BROWSER_COMMAND, RadioRec.DEFAULT_WEB_BROWSER_COMMAND));
    }

    private void getDialog(Preferences prefs) {
        prefs.put(RadioRec.PROP_STATIONS_DIR, stationsDirTextField.getText());
        prefs.put(RadioRec.PROP_RECORDS_DIR, recordsDirTextField.getText());
        prefs.put(RadioRec.PROP_RECORDS_SUBFOLDERS, String.valueOf(recordSubfoldersCheckBox.isSelected()));
        prefs.put(RadioRec.PROP_RECORDS_SUBFOLDERS_FORMAT, subfoldersFormatTextField.getText());
        prefs.put(RadioRec.PROP_RECORDS_FILENAME_FORMAT, fileNameFormatTextField.getText());
        prefs.put(RadioRec.PROP_RECORDS_TIME_APPEND, recordTimeAppendTextField.getText());
        prefs.put(RadioRec.PROP_TEMP_DIR, tempDirTextField.getText());
        // Get selected locale from the languageComboBox
        String selectedLocaleDisplayName = (String) languageComboBox.getSelectedItem();
        Locale selectedLocale = getSelectedLocale(selectedLocaleDisplayName);
        // If the selected locale is different from the current, mark restartRequired as true
        Locale currentLocale = Locale.getDefault();
        if (!selectedLocale.equals(currentLocale)) {
            restartRequired = true;
        }
        // Save the selected locale to preferences
        prefs.put(RadioRec.PROP_UI_LOCALE, selectedLocale.toLanguageTag());
        // Get selected theme from the themeComboBox
        int sel = themeComboBox.getSelectedIndex();
        /* another look ? */
        if (sel != themeIndex) {
            /* restart main form */
            restartRequired = true;
        }
        switch (sel) {
            case 0 ->
                prefs.put(RadioRec.PROP_UI_THEME, RadioRec.UI_THEME_LIGHT);
            case 1 ->
                prefs.put(RadioRec.PROP_UI_THEME, RadioRec.UI_THEME_DARK);
        }
        // UI Size
        sel = sizeComboBox.getSelectedIndex();
        /* another size ? */
        if (sel != sizeIndex) {
            /* restart main form */
            restartRequired = true;
        }
        switch (sel) {
            case 0 ->
                prefs.put(RadioRec.PROP_UI_SIZE, RadioRec.UI_SIZE_SMALL);
            case 1 ->
                prefs.put(RadioRec.PROP_UI_SIZE, RadioRec.UI_SIZE_MEDIUM);
            case 2 ->
                prefs.put(RadioRec.PROP_UI_SIZE, RadioRec.UI_SIZE_LARGE);
        }
        // Time
        prefs.put(RadioRec.PROP_TIME_ZONE_ID, (String) timeZoneComboBox.getSelectedItem());
        prefs.put(RadioRec.PROP_TIME_FORMAT, timeFormatTextField.getText());
        prefs.put(RadioRec.PROP_WEB_BROWSER_PATH, webBrowserPathTextField.getText());
        prefs.put(RadioRec.PROP_WEB_BROWSER_COMMAND, webBrowserCommandTextField.getText());
    }

    private Locale getSelectedLocale(String displayName) {
        // Extract the language and country from the displayName (e.g., "English (United States)")
        for (Locale locale : radioRec.availableLocales) {
            String expectedDisplayName = locale.getDisplayLanguage() + " (" + locale.getDisplayCountry() + ")";
            if (expectedDisplayName.equals(displayName)) {
                return locale;
            }
        }
        // Default locale if not found
        return Locale.getDefault();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        topToolBar = new javax.swing.JToolBar();
        jToggleButton1 = new javax.swing.JToggleButton();
        jToggleButton2 = new javax.swing.JToggleButton();
        jToggleButton3 = new javax.swing.JToggleButton();
        jToggleButton4 = new javax.swing.JToggleButton();
        cardsPanel = new javax.swing.JPanel();
        filePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        stationsDirTextField = new javax.swing.JTextField();
        stationsDirButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        recordsDirTextField = new javax.swing.JTextField();
        recordsDirButton = new javax.swing.JButton();
        recordSubfoldersCheckBox = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        subfoldersFormatTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        fileNameFormatTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tempDirTextField = new javax.swing.JTextField();
        tempDirButton = new javax.swing.JButton();
        timePanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        timeZoneComboBox = new javax.swing.JComboBox<>();
        detectTimeZoneButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        timeFormatTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        recordTimeAppendTextField = new javax.swing.JTextField();
        browserPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        webBrowserPathTextField = new javax.swing.JTextField();
        webBroserPathButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        webBrowserCommandTextField = new javax.swing.JTextField();
        appearancePanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        languageComboBox = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        themeComboBox = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        sizeComboBox = new javax.swing.JComboBox<>();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("cz/marelis/radiorec/Bundle"); // NOI18N
        setTitle(bundle.getString("SettingsDialog.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(600, 300));
        setResizable(false);

        topToolBar.setRollover(true);

        buttonGroup1.add(jToggleButton1);
        jToggleButton1.setSelected(true);
        jToggleButton1.setText(bundle.getString("SettingsDialog.jToggleButton1.text")); // NOI18N
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        topToolBar.add(jToggleButton1);

        buttonGroup1.add(jToggleButton2);
        jToggleButton2.setText(bundle.getString("SettingsDialog.jToggleButton2.text")); // NOI18N
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });
        topToolBar.add(jToggleButton2);

        buttonGroup1.add(jToggleButton3);
        jToggleButton3.setText(bundle.getString("SettingsDialog.jToggleButton3.text")); // NOI18N
        jToggleButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton3ActionPerformed(evt);
            }
        });
        topToolBar.add(jToggleButton3);

        buttonGroup1.add(jToggleButton4);
        jToggleButton4.setText(bundle.getString("SettingsDialog.jToggleButton4.text")); // NOI18N
        jToggleButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton4ActionPerformed(evt);
            }
        });
        topToolBar.add(jToggleButton4);

        cardsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cardsPanel.setLayout(new java.awt.CardLayout());

        jLabel1.setText(bundle.getString("SettingsDialog.jLabel1.text")); // NOI18N

        stationsDirButton.setText(bundle.getString("SettingsDialog.stationsDirButton.text")); // NOI18N
        stationsDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stationsDirButtonActionPerformed(evt);
            }
        });

        jLabel2.setText(bundle.getString("SettingsDialog.jLabel2.text")); // NOI18N

        recordsDirButton.setText(bundle.getString("SettingsDialog.recordsDirButton.text")); // NOI18N
        recordsDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recordsDirButtonActionPerformed(evt);
            }
        });

        recordSubfoldersCheckBox.setText(bundle.getString("SettingsDialog.recordSubfoldersCheckBox.text")); // NOI18N

        jLabel10.setText(bundle.getString("SettingsDialog.jLabel10.text")); // NOI18N

        jLabel3.setText(bundle.getString("SettingsDialog.jLabel3.text")); // NOI18N

        fileNameFormatTextField.setPreferredSize(new java.awt.Dimension(72, 22));

        jLabel4.setText(bundle.getString("SettingsDialog.jLabel4.text")); // NOI18N

        tempDirButton.setText(bundle.getString("SettingsDialog.tempDirButton.text")); // NOI18N
        tempDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tempDirButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout filePanelLayout = new javax.swing.GroupLayout(filePanel);
        filePanel.setLayout(filePanelLayout);
        filePanelLayout.setHorizontalGroup(
            filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(filePanelLayout.createSequentialGroup()
                        .addComponent(tempDirTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tempDirButton))
                    .addGroup(filePanelLayout.createSequentialGroup()
                        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stationsDirTextField)
                            .addComponent(recordsDirTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stationsDirButton)
                            .addComponent(recordsDirButton, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, filePanelLayout.createSequentialGroup()
                        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(filePanelLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(0, 165, Short.MAX_VALUE))
                            .addComponent(subfoldersFormatTextField))
                        .addGap(18, 18, 18)
                        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(fileNameFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(filePanelLayout.createSequentialGroup()
                        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(recordSubfoldersCheckBox)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        filePanelLayout.setVerticalGroup(
            filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stationsDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stationsDirButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(recordsDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(recordsDirButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(recordSubfoldersCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileNameFormatTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(subfoldersFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tempDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tempDirButton))
                .addContainerGap())
        );

        cardsPanel.add(filePanel, "FileCard");

        jLabel6.setText(bundle.getString("SettingsDialog.jLabel6.text")); // NOI18N

        detectTimeZoneButton.setText(bundle.getString("SettingsDialog.detectTimeZoneButton.text")); // NOI18N
        detectTimeZoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detectTimeZoneButtonActionPerformed(evt);
            }
        });

        jLabel7.setText(bundle.getString("SettingsDialog.jLabel7.text")); // NOI18N

        jLabel11.setText(bundle.getString("SettingsDialog.jLabel11.text")); // NOI18N

        javax.swing.GroupLayout timePanelLayout = new javax.swing.GroupLayout(timePanel);
        timePanel.setLayout(timePanelLayout);
        timePanelLayout.setHorizontalGroup(
            timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(timePanelLayout.createSequentialGroup()
                        .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(timePanelLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(0, 286, Short.MAX_VALUE))
                            .addComponent(timeFormatTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(recordTimeAppendTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(timePanelLayout.createSequentialGroup()
                        .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(timePanelLayout.createSequentialGroup()
                                .addComponent(timeZoneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(detectTimeZoneButton)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        timePanelLayout.setVerticalGroup(
            timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timeZoneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(detectTimeZoneButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(timeFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(timePanelLayout.createSequentialGroup()
                        .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(recordTimeAppendTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(171, Short.MAX_VALUE))
        );

        cardsPanel.add(timePanel, "TimeCard");

        jLabel8.setText(bundle.getString("SettingsDialog.jLabel8.text")); // NOI18N

        webBroserPathButton.setText(bundle.getString("SettingsDialog.webBroserPathButton.text")); // NOI18N
        webBroserPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                webBroserPathButtonActionPerformed(evt);
            }
        });

        jLabel9.setText(bundle.getString("SettingsDialog.jLabel9.text")); // NOI18N

        javax.swing.GroupLayout browserPanelLayout = new javax.swing.GroupLayout(browserPanel);
        browserPanel.setLayout(browserPanelLayout);
        browserPanelLayout.setHorizontalGroup(
            browserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, browserPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(browserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(webBrowserCommandTextField)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, browserPanelLayout.createSequentialGroup()
                        .addComponent(webBrowserPathTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(webBroserPathButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, browserPanelLayout.createSequentialGroup()
                        .addGroup(browserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        browserPanelLayout.setVerticalGroup(
            browserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(browserPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(browserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(webBrowserPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(webBroserPathButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(webBrowserCommandTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cardsPanel.add(browserPanel, "BrowserCard");

        jLabel12.setText(bundle.getString("SettingsDialog.jLabel12.text")); // NOI18N

        jLabel5.setText(bundle.getString("SettingsDialog.jLabel5.text")); // NOI18N

        jLabel13.setText(bundle.getString("SettingsDialog.jLabel13.text")); // NOI18N

        javax.swing.GroupLayout appearancePanelLayout = new javax.swing.GroupLayout(appearancePanel);
        appearancePanel.setLayout(appearancePanelLayout);
        appearancePanelLayout.setHorizontalGroup(
            appearancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appearancePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(appearancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(themeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(languageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(401, Short.MAX_VALUE))
        );
        appearancePanelLayout.setVerticalGroup(
            appearancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appearancePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(languageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(themeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(99, Short.MAX_VALUE))
        );

        cardsPanel.add(appearancePanel, "AppearanceCard");

        okButton.setText(bundle.getString("SettingsDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(bundle.getString("SettingsDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(topToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cardsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(topToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cardsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        getDialog(radioRec.prefs);
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void detectTimeZoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detectTimeZoneButtonActionPerformed
        String zone = System.getProperty("user.timezone");
        timeZoneComboBox.getModel().setSelectedItem((zone != null) ? zone : RadioRec.DEFAULT_TIME_ZONE_ID);
    }//GEN-LAST:event_detectTimeZoneButtonActionPerformed

    private void webBroserPathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_webBroserPathButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(RadioRec.DEFAULT_USER_DIR));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Select web broser");
        chooser.setPreferredSize(new Dimension(600, 400));
        chooser.setFileHidingEnabled(false);
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showDialog(this, "Select");
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            webBrowserPathTextField.setText(path);
        }
    }//GEN-LAST:event_webBroserPathButtonActionPerformed

    private void tempDirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tempDirButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(RadioRec.DEFAULT_USER_DIR));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select temporary directory");
        chooser.setPreferredSize(new Dimension(600, 400));
        chooser.setFileHidingEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showDialog(this, "Select");
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            tempDirTextField.setText(path);
        }
    }//GEN-LAST:event_tempDirButtonActionPerformed

    private void recordsDirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recordsDirButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(RadioRec.DEFAULT_USER_DIR));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select records directory");
        chooser.setPreferredSize(new Dimension(600, 400));
        chooser.setFileHidingEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showDialog(this, "Select");
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            recordsDirTextField.setText(path);
        }
    }//GEN-LAST:event_recordsDirButtonActionPerformed

    private void stationsDirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stationsDirButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(RadioRec.DEFAULT_USER_DIR));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select stations directory");
        chooser.setPreferredSize(new Dimension(600, 400));
        chooser.setFileHidingEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showDialog(this, "Select");
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            stationsDirTextField.setText(path);
        }
    }//GEN-LAST:event_stationsDirButtonActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        CardLayout card = (CardLayout) cardsPanel.getLayout();
        card.show(cardsPanel, "FileCard");
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        CardLayout card = (CardLayout) cardsPanel.getLayout();
        card.show(cardsPanel, "TimeCard");
    }//GEN-LAST:event_jToggleButton2ActionPerformed

    private void jToggleButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton3ActionPerformed
        CardLayout card = (CardLayout) cardsPanel.getLayout();
        card.show(cardsPanel, "BrowserCard");
    }//GEN-LAST:event_jToggleButton3ActionPerformed

    private void jToggleButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton4ActionPerformed
        CardLayout card = (CardLayout) cardsPanel.getLayout();
        card.show(cardsPanel, "AppearanceCard");
    }//GEN-LAST:event_jToggleButton4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel appearancePanel;
    private javax.swing.JPanel browserPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel cardsPanel;
    private javax.swing.JButton detectTimeZoneButton;
    private javax.swing.JTextField fileNameFormatTextField;
    private javax.swing.JPanel filePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    private javax.swing.JToggleButton jToggleButton4;
    private javax.swing.JComboBox<String> languageComboBox;
    private javax.swing.JButton okButton;
    private javax.swing.JCheckBox recordSubfoldersCheckBox;
    private javax.swing.JTextField recordTimeAppendTextField;
    private javax.swing.JButton recordsDirButton;
    private javax.swing.JTextField recordsDirTextField;
    private javax.swing.JComboBox<String> sizeComboBox;
    private javax.swing.JButton stationsDirButton;
    private javax.swing.JTextField stationsDirTextField;
    private javax.swing.JTextField subfoldersFormatTextField;
    private javax.swing.JButton tempDirButton;
    private javax.swing.JTextField tempDirTextField;
    private javax.swing.JComboBox<String> themeComboBox;
    private javax.swing.JTextField timeFormatTextField;
    private javax.swing.JPanel timePanel;
    private javax.swing.JComboBox<String> timeZoneComboBox;
    private javax.swing.JToolBar topToolBar;
    private javax.swing.JButton webBroserPathButton;
    private javax.swing.JTextField webBrowserCommandTextField;
    private javax.swing.JTextField webBrowserPathTextField;
    // End of variables declaration//GEN-END:variables
}
