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

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JTable;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class StationTableDropTarget extends DropTarget {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public synchronized void dragEnter(DropTargetDragEvent dtde) {

        Transferable t = dtde.getTransferable();
        try {
            List<?> fileList = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
            if (fileList != null && !fileList.isEmpty()) {
                for (Object value : fileList) {
                    if (value instanceof File file) {
                        if (!file.getAbsolutePath().endsWith(Station.STATION_FILE_EXT_WITH_DOT)) {
                            dtde.rejectDrag();
                            return;
                        }
                    } else {
                        dtde.rejectDrag();
                        return;
                    }
                }
            } else {
                dtde.rejectDrag();
            }
        } catch (UnsupportedFlavorException | IOException e) {
        }
    }

    @Override
    public synchronized void drop(DropTargetDropEvent dtde) {
        if (!(getComponent() instanceof JTable)) {
            dtde.rejectDrop();
        }
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            JTable table = (JTable) getComponent();
            Point point = dtde.getLocation();
            int row = table.rowAtPoint(point);
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            Transferable t = dtde.getTransferable();
            try {
                List<?> fileList = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
                if (fileList != null && !fileList.isEmpty()) {
                    for (Object value : fileList) {
                        if (value instanceof File file) {
                            if (row < 0) {
                                Station station = new Station(file);
                                RadioRec.getInstance().stationDirector.addItem(station);
                            } else {
                                // TODO: stationModel.insertRow();
                            }
                        }
                    }
                }
            } catch (UnsupportedFlavorException | IOException e) {
            }
        } else {
            dtde.rejectDrop();
        }
    }

}
