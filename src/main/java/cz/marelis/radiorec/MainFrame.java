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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableRowSorter;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class MainFrame extends JFrame
        implements ResponseListener, WindowListener, ListSelectionListener {

    private static final long serialVersionUID = 1L;

    private Font buttonsFontRegular;
    private Font buttonsFontSolid;

    private StationTableModel stationTableModel;
    private ProgramTableModel programTableModel;
    private RecordTableModel recordTableModel;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initFonts();
        initModels();
        initComponents();
        initSorters();
    }

    private void initModels() {
        StationDirector sd = RadioRec.getInstance().stationDirector;
        sd.addResponseListener(this);
        stationTableModel = new StationTableModel();
        stationTableModel.setStationDirector(sd);
        programTableModel = new ProgramTableModel();
        recordTableModel = new RecordTableModel();
    }

    private void initSorters() {
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        // station's table
        TableRowSorter<StationTableModel> stationRowSorter = new TableRowSorter<>(stationTableModel);
        stationsTable.setRowSorter(stationRowSorter);
        stationRowSorter.setComparator(0, (Station o1, Station o2) -> o1.compareTo(o2));
        //stationRowSorter.setSortable(0, true);
        stationRowSorter.setSortKeys(sortKeys);
        stationRowSorter.setSortsOnUpdates(true);
        // program's table
        TableRowSorter<ProgramTableModel> programRowSorter = new TableRowSorter<>(programTableModel);
        recordTable.setRowSorter(programRowSorter);
        programRowSorter.setComparator(0, (Program o1, Program o2) -> o1.compareTo(o2));
        //programRowSorter.setSortable(0, true);
        programRowSorter.setSortKeys(sortKeys);
        programRowSorter.setSortsOnUpdates(true);
        // record's table
        TableRowSorter<RecordTableModel> recordRowSorter = new TableRowSorter<>(recordTableModel);
        recordTable.setRowSorter(recordRowSorter);
        recordRowSorter.setComparator(0, (Record o1, Record o2) -> o1.compareTo(o2));
        //recordRowSorter.setSortable(0, true);
        recordRowSorter.setSortKeys(sortKeys);
        recordRowSorter.setSortsOnUpdates(true);
    }

    private void initFonts() {
        buttonsFontRegular = RadioRec.getInstance().fontAwesomeRegular.deriveFont(Font.PLAIN, 24f);
        buttonsFontSolid = RadioRec.getInstance().fontAwesomeSolid.deriveFont(Font.PLAIN, 24f);
    }

    private void updateButtons() {
        //System.out.println("MainFrame.updateButtons");
        int playing = RadioRec.getInstance().stationDirector.getPlayingItem();
        if (playing >= 0) {
            playStationButton.setForeground(new Color(0, 204, 153));
        } else {
            playStationButton.setForeground(UIManager.getDefaults().getColor("Button.foreground"));
        }

        Station station = getSelectedStation();

        if (station == null) {
            System.out.println("MainFrame.updateButtons ... station == null");
            editStationButton.setEnabled(false);
            removeStationButton.setEnabled(false);
            playStationButton.setEnabled(false);

            recordProgramButton.setEnabled(false);
            updateProgramButton.setEnabled(false);
            removeProgramButton.setEnabled(false);
            return;
        }

        boolean manually = station.props.getProperty(Station.PROP_RECORD_ADDING).equals(Station.RECORD_ADDING_MANUALLY);
        int[] programRows = programTable.getSelectedRows();

        editStationButton.setEnabled(true);
        removeStationButton.setEnabled(true);
        playStationButton.setEnabled(true);

        System.out.println("MainFrame.updateButtons ... recordProgramButton.setEnabled(" + (manually && (programRows.length > 0)) + ")");
        recordProgramButton.setEnabled(manually && (programRows.length > 0));
        updateProgramButton.setEnabled(true);
        removeProgramButton.setEnabled(!station.programDirector.isEmpty());
    }

    private void updateStatusPanel(String text, int progress) {
        statusLabel.setVisible(!text.equals(""));
        statusLabel.setText(text);
        statusProgressBar.setVisible(progress >= 0);
        statusProgressBar.setValue(progress);
    }

    private Station getSelectedStation() {
        if (stationsTable.getRowCount() == 0) {
            return null;
        }
        int row = stationsTable.getSelectedRow();
        if (row >= RadioRec.getInstance().stationDirector.itemsCount()) {
            return null;
        }
        return (row >= 0) ? RadioRec.getInstance().stationDirector.getItem(row) : null;
    }

    private void editStationDialog(int idx) {
        Station station = RadioRec.getInstance().stationDirector.getItem(idx);
        StationDialog dialog = new StationDialog(this, station);
        dialog.setModal(true);
        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        dialog.setVisible(true);
        if (dialog.resultOk) {
            station.saveToFile();
            RadioRec.getInstance().stationDirector.setItem(idx, station);
        }
        dialog.dispose();
    }

    private void editRecordDialog(int idx) {
        Station station = getSelectedStation();
        if (station == null) {
            return;
        }
        Record record = station.recordDirector.getItem(idx);
        record.setIrremovable(true);
        RecordDialog dialog = new RecordDialog(this, record);
        dialog.setModal(true);
        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        dialog.setVisible(true);
        if (dialog.resultOk) {
            station.recordDirector.setItem(idx, record);
        }
        dialog.dispose();
        record.setIrremovable(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainToolBar = new javax.swing.JToolBar();
        newStationButton = new javax.swing.JButton();
        openStationButton = new javax.swing.JButton();
        editStationButton = new javax.swing.JButton();
        removeStationButton = new javax.swing.JButton();
        playStationButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        settingsButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        splitPane = new javax.swing.JSplitPane();
        stationsPanel = new javax.swing.JPanel();
        stationsScrollPane = new javax.swing.JScrollPane();
        stationsTable = new javax.swing.JTable();
        tabbedPane = new javax.swing.JTabbedPane();
        recordPanel = new javax.swing.JPanel();
        recordScrollPane = new javax.swing.JScrollPane();
        recordTable = new javax.swing.JTable();
        jToolBar2 = new javax.swing.JToolBar();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        addRecordButton = new javax.swing.JButton();
        removeRecordButton = new javax.swing.JButton();
        programPanel = new javax.swing.JPanel();
        programScrollPane = new javax.swing.JScrollPane();
        programTable = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        recordProgramButton = new javax.swing.JButton();
        updateProgramButton = new javax.swing.JButton();
        removeProgramButton = new javax.swing.JButton();
        statusPanel = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        statusLabel = new javax.swing.JLabel();
        statusProgressBar = new javax.swing.JProgressBar();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("RadioRec");
        setMinimumSize(new java.awt.Dimension(0, 300));
        setPreferredSize(new java.awt.Dimension(730, 470));

        mainToolBar.setRollover(true);

        newStationButton.setFont(buttonsFontRegular);
        newStationButton.setText("\uf0fe");
        newStationButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newStationButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newStationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newStationButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(newStationButton);

        openStationButton.setFont(buttonsFontRegular);
        openStationButton.setText("\uf07c");
        openStationButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openStationButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openStationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openStationButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(openStationButton);

        editStationButton.setFont(buttonsFontRegular);
        editStationButton.setText("\uf044");
        editStationButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editStationButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        editStationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editStationButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(editStationButton);

        removeStationButton.setFont(buttonsFontRegular);
        removeStationButton.setText("\uf2ed");
        removeStationButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeStationButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeStationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeStationButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(removeStationButton);

        playStationButton.setFont(buttonsFontSolid);
        playStationButton.setText("\uf04b");
        playStationButton.setFocusable(false);
        playStationButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playStationButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        playStationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playStationButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(playStationButton);
        mainToolBar.add(filler1);

        settingsButton.setFont(buttonsFontSolid);
        settingsButton.setText("\uf013");
        settingsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        settingsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(settingsButton);

        helpButton.setFont(buttonsFontRegular);
        helpButton.setText("\uf059");
        helpButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        helpButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(helpButton);

        splitPane.setDividerLocation(310);

        stationsTable.setModel(stationTableModel);
        stationsTable.setDragEnabled(true);
        stationsTable.setDropMode(javax.swing.DropMode.INSERT_ROWS);
        stationsTable.setRowHeight(48);
        stationsTable.setShowGrid(true);
        stationsTable.getTableHeader().setReorderingAllowed(false);
        stationsTable.setDefaultRenderer(Station.class, new StationTableCellRenderer());
        stationsTable.getSelectionModel().addListSelectionListener(this);
        stationsTable.setIntercellSpacing(new java.awt.Dimension(1,1));
        stationsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stationsTableMouseClicked(evt);
            }
        });
        stationsTable.setDropTarget(new cz.marelis.radiorec.StationTableDropTarget());
        stationsTable.setTransferHandler(new cz.marelis.radiorec.StationTableTransferHandler());
        stationsScrollPane.setViewportView(stationsTable);

        javax.swing.GroupLayout stationsPanelLayout = new javax.swing.GroupLayout(stationsPanel);
        stationsPanel.setLayout(stationsPanelLayout);
        stationsPanelLayout.setHorizontalGroup(
            stationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(stationsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );
        stationsPanelLayout.setVerticalGroup(
            stationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(stationsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
        );

        splitPane.setLeftComponent(stationsPanel);

        recordTable.setModel(recordTableModel);
        recordTable.setRowHeight(70);
        recordTable.setShowGrid(true);
        recordTable.setTableHeader(null);
        recordTable.setDefaultRenderer(Record.class, new RecordTableCellRenderer());
        recordTable.getSelectionModel().addListSelectionListener(this);
        recordTable.setIntercellSpacing(new java.awt.Dimension(1,1));
        recordTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                recordTableMouseClicked(evt);
            }
        });
        recordScrollPane.setViewportView(recordTable);

        jToolBar2.setRollover(true);
        jToolBar2.add(filler5);

        addRecordButton.setFont(buttonsFontSolid);
        addRecordButton.setText("\u002b");
        addRecordButton.setFocusable(false);
        addRecordButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addRecordButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addRecordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRecordButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(addRecordButton);

        removeRecordButton.setFont(buttonsFontRegular);
        removeRecordButton.setText("\uf2ed");
        removeRecordButton.setFocusable(false);
        removeRecordButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeRecordButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeRecordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeRecordButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(removeRecordButton);

        javax.swing.GroupLayout recordPanelLayout = new javax.swing.GroupLayout(recordPanel);
        recordPanel.setLayout(recordPanelLayout);
        recordPanelLayout.setHorizontalGroup(
            recordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(recordScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        recordPanelLayout.setVerticalGroup(
            recordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, recordPanelLayout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(recordScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Record", recordPanel);

        programTable.setModel(programTableModel);
        programTable.setRowHeight(70);
        programTable.setShowGrid(true);
        programTable.setTableHeader(null);
        programTable.setDefaultRenderer(Program.class, new ProgramTableCellRenderer());
        programTable.getSelectionModel().addListSelectionListener(this);
        programTable.setIntercellSpacing(new java.awt.Dimension(1,1));
        programTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                programTableMouseClicked(evt);
            }
        });
        programScrollPane.setViewportView(programTable);

        jToolBar1.setRollover(true);
        jToolBar1.add(filler2);

        recordProgramButton.setFont(buttonsFontSolid);
        recordProgramButton.setText("\u002b");
        recordProgramButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        recordProgramButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        recordProgramButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recordProgramButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(recordProgramButton);

        updateProgramButton.setFont(buttonsFontSolid);
        updateProgramButton.setText("\uf2f9");
        updateProgramButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        updateProgramButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        updateProgramButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateProgramButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(updateProgramButton);

        removeProgramButton.setFont(buttonsFontRegular);
        removeProgramButton.setText("\uf2ed");
        removeProgramButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeProgramButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeProgramButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeProgramButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(removeProgramButton);

        javax.swing.GroupLayout programPanelLayout = new javax.swing.GroupLayout(programPanel);
        programPanel.setLayout(programPanelLayout);
        programPanelLayout.setHorizontalGroup(
            programPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
            .addComponent(programScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        programPanelLayout.setVerticalGroup(
            programPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, programPanelLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(programScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Program", programPanel);

        splitPane.setRightComponent(tabbedPane);

        statusPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        statusPanel.add(filler3, gridBagConstraints);

        statusLabel.setText("status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        statusPanel.add(statusLabel, gridBagConstraints);

        statusProgressBar.setValue(25);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        statusPanel.add(statusProgressBar, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        statusPanel.add(filler4, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(splitPane)
                    .addComponent(mainToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

        @Override
    public void windowOpened(WindowEvent e) {
        System.out.println("MainFrame.windowOpened");
        updateStatusPanel("Loaded " + RadioRec.getInstance().stationDirector.itemsCount() + " stations", -1);
        if (stationsTable.getRowCount() > 0) {
            stationsTable.getSelectionModel().setSelectionInterval(0, 0);
        }
        updateButtons();
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("MainFrame.windowClosing");
        RadioRec.getInstance().stationDirector.removeResponseListener(this);
        RadioRec.getInstance().shutdown();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        System.out.println("MainFrame.windowClosed");
    }

    @Override
    public void windowIconified(WindowEvent e) {
        System.out.println("MainFrame.windowIconified");
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        System.out.println("MainFrame.windowDeiconified");
    }

    @Override
    public void windowActivated(WindowEvent e) {
        System.out.println("MainFrame.windowActivated");
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        System.out.println("MainFrame.windowDeactivated");
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        System.out.println("MainFrame.valueChanged");
        if (e.getSource() == stationsTable.getSelectionModel()) {
            Station station = getSelectedStation();
            if (station != null) {
                programTableModel.setProgramDirector(station.programDirector);
                recordTableModel.setRecordDirector(station.recordDirector);
            }

        } else if (e.getSource() == programTable.getSelectionModel()) {

        } else if (e.getSource() == recordTable.getSelectionModel()) {

        }
        updateButtons();
    }

    private void newStationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newStationButtonActionPerformed
        Station station = new Station(Station.DEFAULT_STATION_NAME);
        StationDialog dialog = new StationDialog(this, station);
        dialog.setModal(true);
        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        dialog.setVisible(true);

        if (dialog.resultOk) {

            JFileChooser fc = new JFileChooser() {
                @Override
                public void approveSelection() {
                    File f = getSelectedFile();
                    if (f.exists() && getDialogType() == SAVE_DIALOG) {
                        int result = JOptionPane.showConfirmDialog(this,
                                "The file exists, overwrite?", "Existing file",
                                JOptionPane.YES_NO_CANCEL_OPTION);
                        switch (result) {
                            case JOptionPane.YES_OPTION -> {
                                super.approveSelection();
                                return;
                            }
                            case JOptionPane.NO_OPTION -> {
                                return;
                            }
                            case JOptionPane.CLOSED_OPTION -> {
                                return;
                            }
                            case JOptionPane.CANCEL_OPTION -> {
                                cancelSelection();
                                return;
                            }
                        }
                    }
                    super.approveSelection();
                }
            };

            fc.setSelectedFile(new File(station.props.getProperty(Station.PROP_STATION_NAME)));
            fc.setCurrentDirectory(new File(RadioRec.getInstance().prefs.get(RadioRec.PROP_STATIONS_DIR, RadioRec.DEFAULT_STATIONS_DIR)));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setDialogTitle("Save station");
            fc.setPreferredSize(new Dimension(600, 400));
            fc.setMultiSelectionEnabled(false);
            fc.addChoosableFileFilter(new FileNameExtensionFilter("RadioRec Station", Station.STATION_FILE_EXT));
            fc.setAcceptAllFileFilterUsed(false);
            int result = fc.showDialog(this, "Save");
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                // add extension if not exist
                if (!FilenameUtils.getExtension(file.getName())
                        .equalsIgnoreCase(Station.STATION_FILE_EXT)) {
                    file = new File(file.getParentFile(),
                            FilenameUtils.getBaseName(file.getName())
                                    .concat(Station.STATION_FILE_EXT_WITH_DOT));
                }
                station.saveToFile(file);
                RadioRec.getInstance().stationDirector.addItem(station);
            }
        }
        dialog.dispose();
    }//GEN-LAST:event_newStationButtonActionPerformed

    private void openStationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openStationButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(RadioRec.getInstance().prefs.get(RadioRec.PROP_STATIONS_DIR, RadioRec.DEFAULT_STATIONS_DIR)));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setDialogTitle("Load station");
        fc.setPreferredSize(new Dimension(600, 400));
        fc.setMultiSelectionEnabled(true);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("RadioRec Station", Station.STATION_FILE_EXT));
        fc.setAcceptAllFileFilterUsed(false);
        int result = fc.showDialog(this, "Load");
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = fc.getSelectedFiles();
            for (File file : files) {
                System.out.println("add station file: " + file.getAbsolutePath());
                Station station = new Station(file);
                RadioRec.getInstance().stationDirector.addItem(station);
            }
        }
    }//GEN-LAST:event_openStationButtonActionPerformed

    private void editStationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editStationButtonActionPerformed
        int sel = stationsTable.getSelectedRow();
        if (sel >= 0) {
            editStationDialog(sel);
        }
    }//GEN-LAST:event_editStationButtonActionPerformed

    private void removeStationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeStationButtonActionPerformed
        int sel = stationsTable.getSelectedRow();
        if (sel >= 0) {
            RadioRec.getInstance().stationDirector.removeItem(sel);
        }
    }//GEN-LAST:event_removeStationButtonActionPerformed

    private void recordProgramButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recordProgramButtonActionPerformed
        int[] rowIndices = programTable.getSelectedRows();
        if (rowIndices.length == 0) {
            return;
        }
        Station station = getSelectedStation();
        if (station == null) {
            return;
        }
        for (int pos : rowIndices) {
            Program program = station.programDirector.getItem(pos);
            Record record = new Record();
            record.props.setProperty(Record.PROP_URL, station.props.getProperty(Station.PROP_STATION_LINK));
            record.props.setProperty(Record.PROP_TITLE, program.props.getProperty(Program.PROP_TITLE));
            record.props.setProperty(Record.PROP_COMMENT, program.props.getProperty(Program.PROP_COMMENT));
            record.props.setProperty(Record.PROP_TIME_START, program.props.getProperty(Program.PROP_TIME_START));
            record.props.setProperty(Record.PROP_TIME_FINISH, program.props.getProperty(Program.PROP_TIME_FINISH));
            record.props.setProperty(Record.PROP_FILE_DIR, station.getRecordDirName());
            record.props.setProperty(Record.PROP_FILE_NAME, station.getRecordFileName(record));
            if (!station.recordDirector.containsItem(record)) {
                station.recordDirector.addItem(record);
            }
        }
    }//GEN-LAST:event_recordProgramButtonActionPerformed

    private void updateProgramButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateProgramButtonActionPerformed
        Station station = getSelectedStation();

        if (station == null) {
            return;
        }

        SwingWorker<ArrayList<Program>, Integer> worker = new SwingWorker<>() {
            @Override
            protected ArrayList<Program> doInBackground() throws Exception {
                String html = ProgramDirector.getProgramPageSource(station.props.getProperty(Station.PROP_PROGRAM_LINK));
                if (html == null) {
                    System.out.println("Oops! Web page not loaded");
                    return null;
                }
                ArrayList<Program> programs = ProgramDirector.parseProgram(station, Jsoup.parse(html));
                return programs;
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Program> programs = get();
                    System.out.println(String.format("Program loaded, found %d items", programs.size()));
                    station.programDirector.replaceItems(programs);
                    station.saveToFile();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_updateProgramButtonActionPerformed

    private void removeProgramButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeProgramButtonActionPerformed
        Station station = getSelectedStation();
        if (station == null) {
            return;
        }
        if (!station.programDirector.isEmpty()) {
            station.programDirector.clearItems();
        }
    }//GEN-LAST:event_removeProgramButtonActionPerformed

    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        SettingsDialog dialog = new SettingsDialog(this);
        dialog.setModal(true);
        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        if (dialog.restartRequired) {
            RadioRec.getInstance().restart();
        }
        dialog.dispose();
    }//GEN-LAST:event_settingsButtonActionPerformed

    private void stationsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stationsTableMouseClicked
        // double click
        if (evt.getClickCount() == 2) {
            Point pnt = evt.getPoint();
            int sel = stationsTable.rowAtPoint(pnt);
            if (sel >= 0) {
                editStationDialog(sel);
            }
        } else {
            // do something else
        }
    }//GEN-LAST:event_stationsTableMouseClicked

    private void programTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_programTableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_programTableMouseClicked

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_helpButtonActionPerformed

    private void playStationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playStationButtonActionPerformed
        int playing = RadioRec.getInstance().stationDirector.getPlayingItem();
        if (playing >= 0) {
            Station station = RadioRec.getInstance().stationDirector.getItem(playing);
            station.stopPlaying();
        } else {
            int sel = stationsTable.getSelectedRow();
            if (sel >= 0) {
                Station station = RadioRec.getInstance().stationDirector.getItem(sel);
                station.startPlaying();
            }
        }
    }//GEN-LAST:event_playStationButtonActionPerformed

    private void removeRecordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeRecordButtonActionPerformed
        int[] sel = recordTable.getSelectedRows();

        if (sel.length == 0) {
            return;
        }

        Station station = getSelectedStation();
        if (station == null) {
            return;
        }

        ArrayList<Record> list = new ArrayList<>();
        for (int item : sel) {
            Record record = station.recordDirector.getItem(item);
            if (record.isStatus(Record.RECORD_RUNNING)) {
                int result = JOptionPane.showConfirmDialog(this,
                        "Finish or cancel the recording '" + record.props.getProperty(Record.PROP_TITLE) + "' ?", "Delete a record",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                switch (result) {
                    case JOptionPane.YES_OPTION -> {
                        record.finishRecording();
                    }
                    case JOptionPane.NO_OPTION -> {
                    }
                    case JOptionPane.CANCEL_OPTION -> {
                        record.cancelRecording();
                    }
                    case JOptionPane.CLOSED_OPTION -> {
                    }
                }
            } else {
                list.add(record);
            }
        }

        station.recordDirector.removeItems(list);
    }//GEN-LAST:event_removeRecordButtonActionPerformed

    private void addRecordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRecordButtonActionPerformed
        Station station = getSelectedStation();
        if (station == null) {
            return;
        }
        Record record = new Record();
        record.props.setProperty(Record.PROP_FILE_NAME, RadioRec.DEFAULT_RECORDS_FILENAME_FORMAT);
        ZoneId zone = ZoneId.of(RadioRec.getInstance().prefs.get(RadioRec.PROP_TIME_ZONE_ID, RadioRec.DEFAULT_TIME_ZONE_ID));
        ZonedDateTime now = ZonedDateTime.now(zone);
        record.setTimeProperty(Record.PROP_TIME_START, now);
        record.setTimeProperty(Record.PROP_TIME_FINISH, now.plusHours(1));
        RecordDialog dialog = new RecordDialog(this, record);
        dialog.setModal(true);
        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        dialog.setVisible(true);
        if (dialog.resultOk) {
            record.props.setProperty(Record.PROP_URL, station.props.getProperty(Station.PROP_STATION_LINK));
            record.props.setProperty(Record.PROP_FILE_DIR, station.getRecordDirName());
            record.props.setProperty(Record.PROP_FILE_NAME, station.getRecordFileName(record));
            station.recordDirector.addItem(record);
            //station.saveToFile();
        }
        dialog.dispose();
    }//GEN-LAST:event_addRecordButtonActionPerformed

    private void recordTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_recordTableMouseClicked
        // double click
        if (evt.getClickCount() == 2) {
            Point pnt = evt.getPoint();
            int sel = recordTable.rowAtPoint(pnt);
            if (sel >= 0) {
                editRecordDialog(sel);
            }
        } else {
            // do something else
        }
    }//GEN-LAST:event_recordTableMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addRecordButton;
    private javax.swing.JButton editStationButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JButton helpButton;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar mainToolBar;
    private javax.swing.JButton newStationButton;
    private javax.swing.JButton openStationButton;
    private javax.swing.JButton playStationButton;
    private javax.swing.JPanel programPanel;
    private javax.swing.JScrollPane programScrollPane;
    private javax.swing.JTable programTable;
    private javax.swing.JPanel recordPanel;
    private javax.swing.JButton recordProgramButton;
    private javax.swing.JScrollPane recordScrollPane;
    private javax.swing.JTable recordTable;
    private javax.swing.JButton removeProgramButton;
    private javax.swing.JButton removeRecordButton;
    private javax.swing.JButton removeStationButton;
    private javax.swing.JButton settingsButton;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JPanel stationsPanel;
    private javax.swing.JScrollPane stationsScrollPane;
    private javax.swing.JTable stationsTable;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JProgressBar statusProgressBar;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JButton updateProgramButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onResponseEvent(ResponseEvent re) {
        //System.out.println("MainFrame.onResponseEvent: " + re.toString());

        Object source = re.getSource();

        if (source instanceof StationDirector) {

            stationTableModel.fireTableDataChanged();
            updateButtons();

        } else if (source instanceof ProgramDirector) {

            programTableModel.fireTableDataChanged();
            updateButtons();

        } else if (source instanceof RecordDirector) {

            recordTableModel.fireTableDataChanged();
            updateButtons();

        } else if (source instanceof Station station) {

            int idx = RadioRec.getInstance().stationDirector.indexOfItem(station);
            if (idx >= 0) {
                stationTableModel.fireTableRowsUpdated(idx, idx);
                updateButtons();
            }

        } else if (source instanceof Program program) {

            Station station = getSelectedStation();
            if (station == null) {
                return;
            }
            int idx = station.programDirector.indexOfItem(program);
            if (idx >= 0) {
                programTableModel.fireTableRowsUpdated(idx, idx);
                updateButtons();
            }

        } else if (source instanceof Record record) {

            Station station = getSelectedStation();
            if (station == null) {
                return;
            }
            int idx = RadioRec.getInstance().stationDirector.indexOfItem(station);
            if (idx >= 0) {
                stationTableModel.fireTableRowsUpdated(idx, idx);
            }
            idx = station.recordDirector.indexOfItem(record);
            if (idx >= 0) {
                recordTableModel.fireTableRowsUpdated(idx, idx);
            }
            updateButtons();
        }
    }

}
