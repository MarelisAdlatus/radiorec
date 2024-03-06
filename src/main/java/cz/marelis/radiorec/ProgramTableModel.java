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

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class ProgramTableModel extends AbstractTableModel {

    public ProgramDirector programDirector;

    public ProgramTableModel() {
    }
    
    @Override
    public int getRowCount() {
        return programDirector != null ? programDirector.itemsCount() : 0;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return programDirector != null ? programDirector.getItem(rowIndex) : null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Program.class;
    }

    @Override
    public String getColumnName(int column) {
        return "Program";
    }

    public ProgramDirector getProgramDirector() {
        return programDirector;
    }

    public void setProgramDirector(ProgramDirector programDirector) {
        this.programDirector = programDirector;
        this.fireTableDataChanged();
    }
    
}
