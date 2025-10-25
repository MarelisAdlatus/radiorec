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

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Marek Liška <adlatus@marelis.cz>
 */
public class RecordTableModel extends AbstractTableModel {

    private RecordDirector recordDirector;

    /**
     *
     */
    public RecordTableModel() {
    }
    
    /**
     *
     * @return
     */
    @Override
    public int getRowCount() {
        return recordDirector != null ? recordDirector.itemsCount() : 0;
    }

    /**
     *
     * @return
     */
    @Override
    public int getColumnCount() {
        return 1;
    }

    /**
     *
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return recordDirector != null ? recordDirector.getItem(rowIndex): null;
    }

    /**
     *
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     *
     * @param columnIndex
     * @return
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Record.class;
    }

    /**
     *
     * @param column
     * @return
     */
    @Override
    public String getColumnName(int column) {
        return "Records";
    }

    /**
     *
     * @return
     */
    public RecordDirector getRecordDirector() {
        return recordDirector;
    }

    /**
     *
     * @param recordDirector
     */
    public void setRecordDirector(RecordDirector recordDirector) {
        this.recordDirector = recordDirector;
        this.fireTableDataChanged();
    }

}
