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

import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class StationTableCellRenderer implements TableCellRenderer {

    private final StationTableCell panel;

    private final Font regularFont;
    private final Font solidFont;

    public StationTableCellRenderer() {
        this.panel = new StationTableCell();
        this.regularFont = RadioRec.getInstance().fontAwesomeRegular.deriveFont(Font.PLAIN, 28f);
        this.solidFont = RadioRec.getInstance().fontAwesomeSolid.deriveFont(Font.PLAIN, 26f);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Station station = (Station) value;

        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
            panel.setForeground(table.getSelectionForeground());
        } else {
            panel.setBackground(table.getBackground());
            panel.setForeground(table.getForeground());
        }

        JLabel label;

        label = panel.getNameLabel();
        label.setText(station.props.getProperty(Station.PROP_STATION_NAME));

        label = panel.getLinkLabel();
        label.setText(station.props.getProperty(Station.PROP_STATION_LINK));

        label = panel.getPlayLabel();
        label.setFont(regularFont);
        if (station.isStatus(Station.PLAYBACK_RUNS)) {
            label.setText("\uf144");
        } else {
            label.setText("");
        }

        label = panel.getRecordLabel();
        label.setFont(regularFont);
        if (station.isRecording()) {
            label.setText("\uf192");
        } else {
            label.setText("");
        }

        return panel;
    }

}
