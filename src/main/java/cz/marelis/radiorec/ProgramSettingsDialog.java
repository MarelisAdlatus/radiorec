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

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Marek Liška <adlatus@marelis.cz>
 */
public class ProgramSettingsDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    private final Station station;

    private Document htmlSource;

    /**
     *
     */
    public boolean resultOk = false;

    /**
     * Creates new form ProgramParseDialog
     *
     * @param parent
     * @param station
     */
    public ProgramSettingsDialog(java.awt.Frame parent, Station station) {
        super(parent, true);
        this.station = station;
        initComponents();
        setDialog();
    }

    private void setDialog() {
        // Close the dialog after pressing the ESCAPE key
        getRootPane().registerKeyboardAction(e -> {
            setVisible(false);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        // set values
        String themeStr = RadioRec.getInstance().prefs.get(RadioRec.PROP_PARSE_HTML_THEME, RadioRec.DEFAULT_PARSE_HTML_THEME);
        themeComboBox.setSelectedItem(themeStr);
        changeThemeXml(themeStr);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
        textArea.setCodeFoldingEnabled(true);
        textArea.setText("");
        programPageTextField.setText(station.props.getProperty(Station.PROP_PROGRAM_LINK));
        programPageTimeZoneComboBox.setModel(new DefaultComboBoxModel<>(TimeZone.getAvailableIDs()));
        programPageTimeZoneComboBox.getModel()
                .setSelectedItem(station.props.getProperty(Station.PROP_PROGRAM_TIME_ZONE_ID));
        rootXPathTextField.setText(station.props.getProperty(Station.PROP_PROGRAM_ROOT_XPATH));
        titleCSSQueryTextField.setText(station.props.getProperty(Station.PROP_PROGRAM_TITLE_CSS_QUERY));
        commentCSSQueryTextField.setText(station.props.getProperty(Station.PROP_PROGRAM_COMMENT_CSS_QUERY));
        startTimeAttrTextField.setText(station.props.getProperty(Station.PROP_PROGRAM_START_TIME_ATTR));
        startTimeFormatTextField.setText(station.props.getProperty(Station.PROP_PROGRAM_START_TIME_FORMAT));
        finishTimeAttrTextField.setText(station.props.getProperty(Station.PROP_PROGRAM_FINISH_TIME_ATTR));
        finishTimeFormatTextField.setText(station.props.getProperty(Station.PROP_PROGRAM_FINISH_TIME_FORMAT));
    }

    /**
     *
     */
    public void getDialog() {
        station.props.setProperty(Station.PROP_PROGRAM_LINK, programPageTextField.getText());
        station.props.setProperty(Station.PROP_PROGRAM_TIME_ZONE_ID,
                programPageTimeZoneComboBox.getModel().getSelectedItem().toString());
        station.props.setProperty(Station.PROP_PROGRAM_ROOT_XPATH, rootXPathTextField.getText());
        station.props.setProperty(Station.PROP_PROGRAM_TITLE_CSS_QUERY, titleCSSQueryTextField.getText());
        station.props.setProperty(Station.PROP_PROGRAM_COMMENT_CSS_QUERY, commentCSSQueryTextField.getText());
        station.props.setProperty(Station.PROP_PROGRAM_START_TIME_ATTR, startTimeAttrTextField.getText());
        station.props.setProperty(Station.PROP_PROGRAM_START_TIME_FORMAT, startTimeFormatTextField.getText());
        station.props.setProperty(Station.PROP_PROGRAM_FINISH_TIME_ATTR, finishTimeAttrTextField.getText());
        station.props.setProperty(Station.PROP_PROGRAM_FINISH_TIME_FORMAT, finishTimeFormatTextField.getText());
    }

    private void changeThemeXml(String name) {
        Theme theme;
        try {
            theme = Theme.load(getClass().getResourceAsStream(
                    "/org/fife/ui/rsyntaxtextarea/themes/" + name + ".xml"));
            theme.apply(textArea);
        } catch (IOException ex) {
            Logger.getLogger(ProgramSettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        rootXPathTextField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        finishTimeFormatTextField = new javax.swing.JTextField();
        titleCSSQueryTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        commentCSSQueryTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        startTimeAttrTextField = new javax.swing.JTextField();
        programPageTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        finishTimeAttrTextField = new javax.swing.JTextField();
        programPageTimeZoneComboBox = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        startTimeFormatTextField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        textArea = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
        jLabel1 = new javax.swing.JLabel();
        themeComboBox = new javax.swing.JComboBox<>();
        sourceButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        parseButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        parseTable = new javax.swing.JTable();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("cz/marelis/radiorec/Bundle"); // NOI18N
        setTitle(bundle.getString("ProgramSettingsDialog.title")); // NOI18N

        okButton.setText(bundle.getString("ProgramSettingsDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(bundle.getString("ProgramSettingsDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
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

        jLabel12.setText(bundle.getString("ProgramSettingsDialog.jLabel12.text")); // NOI18N

        jLabel8.setText(bundle.getString("ProgramSettingsDialog.jLabel8.text")); // NOI18N

        jLabel9.setText(bundle.getString("ProgramSettingsDialog.jLabel9.text")); // NOI18N

        jLabel4.setText(bundle.getString("ProgramSettingsDialog.jLabel4.text")); // NOI18N

        jLabel3.setText(bundle.getString("ProgramSettingsDialog.jLabel3.text")); // NOI18N

        jLabel10.setText(bundle.getString("ProgramSettingsDialog.jLabel10.text")); // NOI18N

        jLabel5.setText(bundle.getString("ProgramSettingsDialog.jLabel5.text")); // NOI18N

        jLabel11.setText(bundle.getString("ProgramSettingsDialog.jLabel11.text")); // NOI18N

        jLabel7.setText(bundle.getString("ProgramSettingsDialog.jLabel7.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(programPageTextField)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8)
                            .addComponent(jLabel4)
                            .addComponent(jLabel11)
                            .addComponent(startTimeAttrTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                            .addComponent(titleCSSQueryTextField)
                            .addComponent(startTimeFormatTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(finishTimeAttrTextField)
                            .addComponent(commentCSSQueryTextField)
                            .addComponent(finishTimeFormatTextField)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel12))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(programPageTimeZoneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rootXPathTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(programPageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(programPageTimeZoneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rootXPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleCSSQueryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(commentCSSQueryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startTimeAttrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(finishTimeAttrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startTimeFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(finishTimeFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(129, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(bundle.getString("ProgramSettingsDialog.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        scrollPane.setViewportView(textArea);

        jLabel1.setText(bundle.getString("ProgramSettingsDialog.jLabel1.text")); // NOI18N

        themeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "dark", "default", "druid", "eclipse", "idea", "monokai", "vs" }));
        themeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                themeComboBoxActionPerformed(evt);
            }
        });

        sourceButton.setText(bundle.getString("ProgramSettingsDialog.sourceButton.text")); // NOI18N
        sourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(sourceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 547, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(themeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sourceButton)
                    .addComponent(jLabel1)
                    .addComponent(themeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("ProgramSettingsDialog.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        parseButton.setText(bundle.getString("ProgramSettingsDialog.parseButton.text")); // NOI18N
        parseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parseButtonActionPerformed(evt);
            }
        });

        parseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Start", "Finish", "Title", "Comment"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        parseTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        parseTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(parseTable);
        if (parseTable.getColumnModel().getColumnCount() > 0) {
            parseTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            parseTable.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ProgramSettingsDialog.parseTable.columnModel.title0")); // NOI18N
            parseTable.getColumnModel().getColumn(1).setPreferredWidth(80);
            parseTable.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ProgramSettingsDialog.parseTable.columnModel.title1")); // NOI18N
            parseTable.getColumnModel().getColumn(2).setPreferredWidth(180);
            parseTable.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ProgramSettingsDialog.parseTable.columnModel.title2")); // NOI18N
            parseTable.getColumnModel().getColumn(3).setPreferredWidth(300);
            parseTable.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ProgramSettingsDialog.parseTable.columnModel.title3")); // NOI18N
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(parseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(parseButton)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("ProgramSettingsDialog.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        resultOk = true;
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void parseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parseButtonActionPerformed

        parseButton.setEnabled(false);

        ZoneId zone = ZoneId.of(programPageTimeZoneComboBox.getModel().getSelectedItem().toString());
        // yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
        String startTimeFormat = startTimeFormatTextField.getText();
        DateTimeFormatter timeStartFormatter = DateTimeFormatter.ofPattern(startTimeFormat)
                .withZone(zone);
        // yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
        String finishTimeFormat = finishTimeFormatTextField.getText();
        DateTimeFormatter timeFinishFormatter = DateTimeFormatter.ofPattern(finishTimeFormat)
                .withZone(zone);
        // //li[starts-with(@class,'item')]
        String rootXPath = rootXPathTextField.getText();
        // div h3
        String titleCSSQuery = titleCSSQueryTextField.getText();
        // div p
        String commentCSSQuery = commentCSSQueryTextField.getText();
        // data-since
        String startTimeAttr = startTimeAttrTextField.getText();
        /// data-till
        String finishTimeAttr = finishTimeAttrTextField.getText();
        ZonedDateTime now = ZonedDateTime.now(zone);

        SwingWorker<ArrayList<Program>, Integer> worker = new SwingWorker<>() {
            @Override
            protected ArrayList<Program> doInBackground() throws Exception {

                ArrayList<Program> result = new ArrayList<>();

                if (htmlSource == null) {
                    parseButton.setEnabled(true);
                    return result;
                } else {
                    if (htmlSource.toString().isEmpty()) {
                        parseButton.setEnabled(true);
                        return result;
                    }
                }

                ZonedDateTime startTime;
                ZonedDateTime finishTime;
                String titleStr;
                String commentStr;

                Element element;
                Elements items = htmlSource.selectXpath(rootXPath);
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
                    startTime = ZonedDateTime.parse(startTimeStr, timeStartFormatter);

                    String finishTimeStr = item.attr(finishTimeAttr);
                    finishTime = ZonedDateTime.parse(finishTimeStr, timeFinishFormatter);

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
            protected void done() {
                try {
                    ArrayList<Program> programs = get();
                    DefaultTableModel model = (DefaultTableModel) parseTable.getModel();
                    model.setRowCount(0);
                    ZoneId programZone = ZoneId.of(RadioRec.getInstance().prefs.get(RadioRec.PROP_TIME_ZONE_ID, RadioRec.DEFAULT_TIME_ZONE_ID));
                    ZonedDateTime timeStart, timeFinish;
                    for (Program p : programs) {
                        timeStart = p.getTimeProperty(Program.PROP_TIME_START).withZoneSameInstant(programZone);
                        timeFinish = p.getTimeProperty(Program.PROP_TIME_FINISH).withZoneSameInstant(programZone);
                        model.addRow(new String[]{
                            DateTimeFormatter.ofPattern("HH:mm:ss").format(timeStart),
                            DateTimeFormatter.ofPattern("HH:mm:ss").format(timeFinish),
                            p.props.getProperty(Program.PROP_TITLE),
                            p.props.getProperty(Program.PROP_COMMENT)
                        });
                    }
                    System.out.println(String.format("Program loaded, found %d items", programs.size()));
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                parseButton.setEnabled(true);
            }
        };
        worker.execute();
    }//GEN-LAST:event_parseButtonActionPerformed

    private void sourceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceButtonActionPerformed

        sourceButton.setEnabled(false);

        String link = programPageTextField.getText();

        SwingWorker<Document, Integer> worker = new SwingWorker<>() {
            @Override
            protected Document doInBackground() throws Exception {
                String html = ProgramDirector.getProgramPageSource(link);
                return Jsoup.parse(html);
            }

            @Override
            protected void done() {
                try {
                    htmlSource = get();
                    textArea.setText(htmlSource.toString());
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(StationDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
                sourceButton.setEnabled(true);
            }
        };
        worker.execute();
    }//GEN-LAST:event_sourceButtonActionPerformed

    private void themeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_themeComboBoxActionPerformed
        String themeStr = (String) themeComboBox.getSelectedItem();
        changeThemeXml(themeStr);
        RadioRec.getInstance().prefs.put(RadioRec.PROP_PARSE_HTML_THEME, themeStr);
    }//GEN-LAST:event_themeComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField commentCSSQueryTextField;
    private javax.swing.JTextField finishTimeAttrTextField;
    private javax.swing.JTextField finishTimeFormatTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton okButton;
    private javax.swing.JButton parseButton;
    private javax.swing.JTable parseTable;
    private javax.swing.JTextField programPageTextField;
    private javax.swing.JComboBox<String> programPageTimeZoneComboBox;
    private javax.swing.JTextField rootXPathTextField;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton sourceButton;
    private javax.swing.JTextField startTimeAttrTextField;
    private javax.swing.JTextField startTimeFormatTextField;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea textArea;
    private javax.swing.JComboBox<String> themeComboBox;
    private javax.swing.JTextField titleCSSQueryTextField;
    // End of variables declaration//GEN-END:variables
}
