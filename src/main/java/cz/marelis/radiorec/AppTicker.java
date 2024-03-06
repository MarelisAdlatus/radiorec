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

import java.awt.EventQueue;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class AppTicker {

    private ZoneId zone = ZoneId.of("UTC");
    
    private final ArrayList<AppTickerListener> listeners = new ArrayList<>();

    private final Runnable ticker = () -> {
        EventQueue.invokeLater(() -> {
            ZonedDateTime time = ZonedDateTime.now(zone);
            AppTicker.this.fireAppTickerTick(time);
        });
    };

    public AppTicker(int value, TimeUnit unit) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(ticker, 0, value, unit);
    }

    public synchronized void addAppTickerListener(AppTickerListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeAppTickerListener(AppTickerListener listener) {
        listeners.remove(listener);
    }

    private synchronized void fireAppTickerTick(ZonedDateTime time) {
        listeners.forEach(listener -> listener.appTickerTick(this, time));
    }

}
