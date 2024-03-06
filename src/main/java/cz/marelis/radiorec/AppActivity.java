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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class AppActivity implements Runnable {

    private static final File APP_FILE_DIR = new File(System.getProperty("user.home"), ".RadioRec");

    private static FileChannel fileChannel = null;
    private static FileLock fileLock = null;

    private static final String LOCK_FILE_NAME = "RadioRec.lock";
    private static final String ARGS_FILE_NAME = "RadioRec.args";

    private final ArrayList<AppActivityListener> listeners = new ArrayList<>();

    private volatile boolean shutdown;

    static {
        // Creates the lock file directory, if it yet doesn't exist.
        if (!APP_FILE_DIR.exists()) {
            APP_FILE_DIR.mkdirs();
        }
    }

    public AppActivity() {
    }

    public static boolean isAppActive(String args[]) {

        File lockFile = new File(APP_FILE_DIR, LOCK_FILE_NAME);
        try {
            fileChannel = new RandomAccessFile(lockFile, "rw").getChannel();
            fileLock = fileChannel.tryLock();
            if (fileLock == null) {
                if (args.length > 0) {
                    File fileArgs = new File(APP_FILE_DIR, ARGS_FILE_NAME);
                    FileChannel channel = FileChannel.open(fileArgs.toPath(), StandardOpenOption.READ,
                            StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                    MappedByteBuffer mbb = channel.map(MapMode.READ_WRITE, 0, 8192);
                    CharBuffer charBuf = mbb.asCharBuffer();
                    for (String str : args) {
                        charBuf.put(str.concat("\n"));
                    }
                }
                return true;
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(AppActivity.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(AppActivity.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    fileLock.release();
                    fileChannel.close();
                } catch (IOException ex) {
                    Logger.getLogger(AppActivity.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        return false;
    }

    @Override
    public void run() {
        File file = new File(APP_FILE_DIR, ARGS_FILE_NAME);
        FileChannel channel;
        try {
            channel = FileChannel.open(file.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE);

            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 8192);
            CharBuffer charBuffer = mappedByteBuffer.asCharBuffer();

            while (true) {
                if (charBuffer.get(0) != '\0') {
                    ArrayList<String> list = new ArrayList<>();
                    StringBuilder sb = new StringBuilder();
                    char ch;
                    while ((ch = charBuffer.get()) != 0) {
                        if (ch == '\n') {
                            list.add(sb.toString());
                            sb.setLength(0);
                            continue;
                        }
                        sb.append(ch);
                    }
                    // clean
                    charBuffer.rewind();
                    for (int i = 0; i < 4096; i++) {
                        charBuffer.put(i, '\0');
                    }
                    charBuffer.rewind();
                    // say event
                    if (!list.isEmpty()) {
                        String[] args = new String[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            args[i] = list.get(i);
                        }
                        fireAppActivityArgsDelivered(args);
                    }
                }
                if (shutdown) {
                    System.out.println("Shutdown !");
                    break;
                }
                try {
                    Thread ct = Thread.currentThread();
                    synchronized (ct) {
                        ct.wait(200);
                        ct.notify();
                    }
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted !");
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AppActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void shutdown() {
        shutdown = true;
    }

    public void addAppActivityListener(AppActivityListener listener) {
        listeners.add(listener);
    }

    public void removeAppActivityListener(AppActivityListener listener) {
        listeners.remove(listener);
    }

    private synchronized void fireAppActivityArgsDelivered(String args[]) {
        listeners.forEach(listener -> listener.appActivityArgsDelivered(this, args));
    }
}
