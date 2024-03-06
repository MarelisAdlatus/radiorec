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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class StationTableTransferHandler extends TransferHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Transferable createTransferable(JComponent c) {
		System.out.println("TransferHandler.createTransferable");

		List<File> files = new ArrayList<>();
		JTable table = (JTable) c;
		int[] rows = table.getSelectedRows();
		for (int row : rows) {
			Station station = (Station) RadioRec.getInstance().stationDirector.getItem(row);
			files.add(station.stationFile);
		}

		return new Transferable() {
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.javaFileListFlavor };
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return flavor.equals(DataFlavor.javaFileListFlavor);
			}

			@Override
			public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
				if (!isDataFlavorSupported(df)) {
					throw new UnsupportedFlavorException(df);
				}
				return files;
			}
		};
	}

	@Override
	public int getSourceActions(JComponent c) {
		System.out.println("TransferHandler.getSourceActions");
		return DnDConstants.ACTION_COPY;
	}
    
}
