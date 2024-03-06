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

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.TimeZone;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class SettingsDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    public boolean restartRequired = false;

    private int themeIndex;

    /**
     * Creates new form SettingsDialog
     * @param parent
     */
    public SettingsDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        setDialog(RadioRec.getInstance().prefs);
    }

    private void setDialog(Preferences prefs) {
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
        themeComboBox.addItem("Metal");
        themeComboBox.addItem("Nimbus");
        themeComboBox.addItem("CDE/Motif");
        themeComboBox.addItem("Windows");
        themeComboBox.addItem("Windows Classic");
        themeComboBox.addItem("FlatLaf Light");
        themeComboBox.addItem("FlatLaf Dark");
        String theme = prefs.get(RadioRec.PROP_UI_THEME, RadioRec.DEFAULT_UI_THEME);
        switch (theme) {
            case RadioRec.UI_THEME_METAL ->
                themeIndex = 0;
            case RadioRec.UI_THEME_NIMBUS ->
                themeIndex = 1;
            case RadioRec.UI_THEME_CDE_MOTIF ->
                themeIndex = 2;
            case RadioRec.UI_THEME_WINDOWS ->
                themeIndex = 3;
            case RadioRec.UI_THEME_WINDOWS_CLASSIC ->
                themeIndex = 4;
            case RadioRec.UI_THEME_FLATLAF_LIGHT ->
                themeIndex = 5;
            case RadioRec.UI_THEME_FLATLAF_DARK ->
                themeIndex = 6;
        }
        themeComboBox.setSelectedIndex(themeIndex);
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
        int sel = themeComboBox.getSelectedIndex();
        /* another look ? */
        if (sel != themeIndex) {
            /* restart main form */
            restartRequired = true;
        }
        switch (sel) {
            case 0 ->
                prefs.put(RadioRec.PROP_UI_THEME, RadioRec.UI_THEME_METAL);
            case 1 ->
                prefs.put(RadioRec.PROP_UI_THEME, RadioRec.UI_THEME_NIMBUS);
            case 2 ->
                prefs.put(RadioRec.PROP_UI_THEME, RadioRec.UI_THEME_CDE_MOTIF);
            case 3 ->
                prefs.put(RadioRec.PROP_UI_THEME, RadioRec.UI_THEME_WINDOWS);
            case 4 ->
                prefs.put(RadioRec.PROP_UI_THEME, RadioRec.UI_THEME_WINDOWS_CLASSIC);
            case 5 ->
                prefs.put(RadioRec.PROP_UI_THEME, RadioRec.UI_THEME_FLATLAF_LIGHT);
            case 6 ->
                prefs.put(RadioRec.PROP_UI_THEME, RadioRec.UI_THEME_FLATLAF_DARK);
        }
        prefs.put(RadioRec.PROP_TIME_ZONE_ID, (String) timeZoneComboBox.getSelectedItem());
        prefs.put(RadioRec.PROP_TIME_FORMAT, timeFormatTextField.getText());
        prefs.put(RadioRec.PROP_WEB_BROWSER_PATH, webBrowserPathTextField.getText());
        prefs.put(RadioRec.PROP_WEB_BROWSER_COMMAND, webBrowserCommandTextField.getText());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contentPanel = new javax.swing.JPanel();
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
        jLabel11 = new javax.swing.JLabel();
        recordTimeAppendTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        themeComboBox = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        timeZoneComboBox = new javax.swing.JComboBox<>();
        detectTimeZoneButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        timeFormatTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        webBrowserPathTextField = new javax.swing.JTextField();
        webBroserPathButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        webBrowserCommandTextField = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        setResizable(false);

        contentPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText("Stations directory");

        stationsDirButton.setText("Select");
        stationsDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stationsDirButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Records directory");

        recordsDirButton.setText("Select");
        recordsDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recordsDirButtonActionPerformed(evt);
            }
        });

        recordSubfoldersCheckBox.setText("Create subfolders");

        jLabel10.setText("Subfolders format");

        jLabel3.setText("Filename format");

        fileNameFormatTextField.setPreferredSize(new java.awt.Dimension(72, 22));

        jLabel4.setText("Temporary directory");

        tempDirButton.setText("Select");
        tempDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tempDirButtonActionPerformed(evt);
            }
        });

        jLabel11.setText("Time append");

        jLabel5.setText("Theme");

        jLabel6.setText("Time zone");

        detectTimeZoneButton.setText("Detect");
        detectTimeZoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detectTimeZoneButtonActionPerformed(evt);
            }
        });

        jLabel7.setText("Time format");

        jLabel8.setText("Web browser path");

        webBroserPathButton.setText("Select");
        webBroserPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                webBroserPathButtonActionPerformed(evt);
            }
        });

        jLabel9.setText("Web browser command");

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(webBrowserCommandTextField)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(webBrowserPathTextField)
                        .addGap(18, 18, 18)
                        .addComponent(webBroserPathButton))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(recordsDirTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                                    .addComponent(stationsDirTextField))
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(recordsDirButton))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(stationsDirButton))))
                            .addComponent(recordSubfoldersCheckBox)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(179, 179, 179)
                                .addComponent(jLabel3))
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(timeFormatTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(timeZoneComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(themeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(detectTimeZoneButton)))
                        .addGap(0, 50, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tempDirTextField)
                            .addComponent(subfoldersFormatTextField))
                        .addGap(18, 18, 18)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fileNameFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addComponent(tempDirButton)
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(recordTimeAppendTextField))))))
                .addContainerGap())
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stationsDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stationsDirButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(recordsDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(recordsDirButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(recordSubfoldersCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileNameFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subfoldersFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tempDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tempDirButton)
                    .addComponent(recordTimeAppendTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(themeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timeZoneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(detectTimeZoneButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(webBrowserPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(webBroserPathButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(webBrowserCommandTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        getDialog(RadioRec.getInstance().prefs);
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton detectTimeZoneButton;
    private javax.swing.JTextField fileNameFormatTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton okButton;
    private javax.swing.JCheckBox recordSubfoldersCheckBox;
    private javax.swing.JTextField recordTimeAppendTextField;
    private javax.swing.JButton recordsDirButton;
    private javax.swing.JTextField recordsDirTextField;
    private javax.swing.JButton stationsDirButton;
    private javax.swing.JTextField stationsDirTextField;
    private javax.swing.JTextField subfoldersFormatTextField;
    private javax.swing.JButton tempDirButton;
    private javax.swing.JTextField tempDirTextField;
    private javax.swing.JComboBox<String> themeComboBox;
    private javax.swing.JTextField timeFormatTextField;
    private javax.swing.JComboBox<String> timeZoneComboBox;
    private javax.swing.JButton webBroserPathButton;
    private javax.swing.JTextField webBrowserCommandTextField;
    private javax.swing.JTextField webBrowserPathTextField;
    // End of variables declaration//GEN-END:variables
}
