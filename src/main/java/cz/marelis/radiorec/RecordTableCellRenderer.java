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
import java.awt.Component;
import java.awt.Font;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class RecordTableCellRenderer implements TableCellRenderer {

    private final RecordTableCell panel;
    private final Font regularFont;
    private final Font solidFont;

    public RecordTableCellRenderer() {
        this.panel = new RecordTableCell();
        this.regularFont = RadioRec.getInstance().fontAwesomeRegular.deriveFont(Font.PLAIN, 14f);
        this.solidFont = RadioRec.getInstance().fontAwesomeSolid.deriveFont(Font.PLAIN, 14f);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
            panel.setForeground(table.getSelectionForeground());
        } else {
            panel.setBackground(table.getBackground());
            panel.setForeground(table.getForeground());
        }

        Record record = (Record) value;
        int status = record.getStatus();
        int progress = record.getProgress();

        Color defaultLabelForeground = UIManager.getDefaults().getColor("Label.foreground");

        ZoneId zone = ZoneId.of(RadioRec.getInstance().prefs.get(RadioRec.PROP_TIME_ZONE_ID, RadioRec.DEFAULT_TIME_ZONE_ID));

        ZonedDateTime start = record.getTimeProperty(Program.PROP_TIME_START);
        ZonedDateTime finish = record.getTimeProperty(Program.PROP_TIME_FINISH);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

        JLabel label;
        JProgressBar bar;

        label = panel.getTimeLabel();
        label.setText(DateTimeFormatter.ofPattern("HH:mm:ss").format(start.withZoneSameInstant(zone)));

        label = panel.getStatusLabel();
        bar = panel.getProgressBar();

        switch (status) {
            case Record.RECORD_DONE -> {
                label.setForeground(defaultLabelForeground);
                label.setFont(regularFont);
                label.setText("\uf058");
                bar.setVisible(false);
            }
            case Record.RECORD_RUNNING -> {
                label.setForeground(new Color(255, 51, 51));
                label.setFont(regularFont);
                label.setText("\uf192");
                bar.setVisible(true);
                bar.setValue(progress);
            }
            case Record.RECORD_PENDING -> {
                label.setForeground(defaultLabelForeground);
                label.setFont(regularFont);
                label.setText("\uf017");
                bar.setVisible(false);
            }
            default -> {
                label.setText("");
                bar.setVisible(false);
            }
        }

        label = panel.getDurationLabel();

        switch (status) {
            case Record.RECORD_DONE -> {
                label.setForeground(defaultLabelForeground);
                label.setText("...");
            }
            case Record.RECORD_RUNNING -> {
                label.setForeground(new Color(255, 51, 51));
                label.setText(String.format("%d min.", now.until(finish, ChronoUnit.MINUTES)));
            }
            case Record.RECORD_PENDING -> {
                label.setForeground(new Color(0, 204, 153));
                label.setText(String.format("%d min.", start.until(finish, ChronoUnit.MINUTES)));
            }
            default -> {
                label.setForeground(defaultLabelForeground);
            }
        }

        label = panel.getTitleLabel();
        label.setText(record.props.getProperty(Program.PROP_TITLE));

        label = panel.getCommentLabel();
        String text = record.props.getProperty(Program.PROP_COMMENT);
        label.setText(text.isEmpty() ? " " : text);

        return panel;
    }

}
