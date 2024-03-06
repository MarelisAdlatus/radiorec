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
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class ProgramParseDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    private final Station station;

    private Document htmlSource;

    public boolean resultOk = false;

    /**
     * Creates new form ProgramParseDialog
     *
     * @param parent
     * @param station
     */
    public ProgramParseDialog(java.awt.Frame parent, Station station) {
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
            Logger.getLogger(ProgramParseDialog.class.getName()).log(Level.SEVERE, null, ex);
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
        sourceButton = new javax.swing.JButton();
        parseButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        searchTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        themeComboBox = new javax.swing.JComboBox<>();
        jCheckBox1 = new javax.swing.JCheckBox();
        jTextField1 = new javax.swing.JTextField();
        jCheckBox2 = new javax.swing.JCheckBox();
        jTextField2 = new javax.swing.JTextField();
        jCheckBox3 = new javax.swing.JCheckBox();
        scrollPane = new javax.swing.JScrollPane();
        textArea = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
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
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setTitle("Program Web Page");

        sourceButton.setText("Source");
        sourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceButtonActionPerformed(evt);
            }
        });

        parseButton.setText("Parse");
        parseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parseButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
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
                .addContainerGap()
                .addComponent(sourceButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(parseButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(okButton)
                    .addComponent(sourceButton)
                    .addComponent(parseButton))
                .addContainerGap())
        );

        jLabel1.setText("Theme");

        themeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "dark", "default", "druid", "eclipse", "idea", "monokai", "vs" }));
        themeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                themeComboBoxActionPerformed(evt);
            }
        });

        jCheckBox1.setText("XPath");

        jCheckBox2.setText("CSS Query");

        jCheckBox3.setText("Search");

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addComponent(jCheckBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(themeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(themeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        scrollPane.setViewportView(textArea);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane)
                    .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Source", jPanel1);

        jLabel12.setText("Finish Time Format");

        jLabel8.setText("Title CSS Query");

        jLabel9.setText("Comment CSS Query");

        jLabel4.setText("Start Time Attribute");

        jLabel3.setText("Page");

        jLabel10.setText("Finish Time Attribute");

        jLabel5.setText("Time zone");

        jLabel11.setText("Start Time Format");

        jLabel7.setText("Root XPath");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(programPageTextField)
                    .addComponent(rootXPathTextField)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(programPageTimeZoneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8)
                            .addComponent(jLabel4)
                            .addComponent(jLabel11)
                            .addComponent(titleCSSQueryTextField)
                            .addComponent(startTimeAttrTextField)
                            .addComponent(startTimeFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(finishTimeFormatTextField)
                            .addComponent(commentCSSQueryTextField)
                            .addComponent(finishTimeAttrTextField)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel12))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(274, 274, 274))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(programPageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(programPageTimeZoneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rootXPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleCSSQueryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(commentCSSQueryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startTimeAttrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(finishTimeAttrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startTimeFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(finishTimeFormatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Settings", jPanel2);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Start", "Finish", "Title", "Comment"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
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
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Parse", jPanel3);

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

    private void themeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_themeComboBoxActionPerformed
        String themeStr = (String) themeComboBox.getSelectedItem();
        changeThemeXml(themeStr);
        RadioRec.getInstance().prefs.put(RadioRec.PROP_PARSE_HTML_THEME, themeStr);
    }//GEN-LAST:event_themeComboBoxActionPerformed

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
                    System.out.println(String.format("Program loaded, found %d items", programs.size()));
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                parseButton.setEnabled(true);
            }
        };
        worker.execute();
    }//GEN-LAST:event_parseButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField commentCSSQueryTextField;
    private javax.swing.JTextField finishTimeAttrTextField;
    private javax.swing.JTextField finishTimeFormatTextField;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
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
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JButton okButton;
    private javax.swing.JButton parseButton;
    private javax.swing.JTextField programPageTextField;
    private javax.swing.JComboBox<String> programPageTimeZoneComboBox;
    private javax.swing.JTextField rootXPathTextField;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JButton sourceButton;
    private javax.swing.JTextField startTimeAttrTextField;
    private javax.swing.JTextField startTimeFormatTextField;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea textArea;
    private javax.swing.JComboBox<String> themeComboBox;
    private javax.swing.JTextField titleCSSQueryTextField;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
