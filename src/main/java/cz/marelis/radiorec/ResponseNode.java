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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public abstract class ResponseNode {

    public final static int EVT_STATUS_CHANGED = 0;
    public final static int EVT_PROGRESS_CHANGED = 1;
    public final static int EVT_IRREMOVABLE_CHANGED = 2;
    public final static int EVT_REMOVE_CHANGED = 2;

    public final Properties props = new Properties();

    private final static String TIME_FORMAT = "yyyyMMddHHmmss";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

    private final AtomicInteger status = new AtomicInteger();

    private final AtomicInteger progress = new AtomicInteger();

    private final AtomicBoolean irremovable = new AtomicBoolean();

    private final AtomicBoolean remove = new AtomicBoolean();

    private final ArrayList<ResponseListener> listeners = new ArrayList<>();
    
    public void setTimeProperty(String propName, ZonedDateTime zdt) {
        props.put(propName, formatter.format(zdt.withZoneSameInstant(ZoneId.of("UTC"))));
    }
    
    public ZonedDateTime getTimeProperty(String propName) {
        LocalDateTime local = LocalDateTime.parse(props.getProperty(propName), formatter);
        return local.atZone(ZoneId.of("UTC"));
    }

    public int getStatus() {
        return status.get();
    }

    public void setStatus(int val) {
        int old = status.get();
        if (val != old) {
            status.set(val);
            fireResponseEvent(new ResponseEvent(this, EVT_STATUS_CHANGED, null, "ResponseNode.setStatus"));
        }
    }

    public boolean isStatus(int val) {
        return status.get() == val;
    }

    public int getProgress() {
        return progress.get();
    }

    public void setProgress(int val) {
        int old = progress.get();
        if (val != old) {
            progress.set(val);
            fireResponseEvent(new ResponseEvent(this, EVT_PROGRESS_CHANGED, null, "ResponseNode.setProgress"));
        }
    }

    public void setIrremovable(boolean val) {
        boolean old = irremovable.get();
        if (val != old) {
            irremovable.set(val);
            fireResponseEvent(new ResponseEvent(this, EVT_REMOVE_CHANGED, null, "ResponseNode.setRemove"));
        }
    }

    public boolean isIrremovable() {
        return irremovable.get();
    }
    
    public void setRemove(boolean val) {
        if (isIrremovable()) {
            return;
        }
        boolean old = remove.get();
        if (val != old) {
            remove.set(val);
            fireResponseEvent(new ResponseEvent(this, EVT_REMOVE_CHANGED, null, "ResponseNode.setRemove"));
        }
    }

    public boolean isRemove() {
        return remove.get();
    }

    public synchronized void addResponseListener(ResponseListener rel) {
        listeners.add(rel);
    }

    public synchronized void removeResponseListener(ResponseListener rel) {
        listeners.remove(rel);
    }

    public synchronized void fireResponseEvent(ResponseEvent re) {
        listeners.forEach(listener -> listener.onResponseEvent(re));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ResponseNode{");
        sb.append("props=").append(props);
        sb.append(", formatter=").append(formatter);
        sb.append(", status=").append(status);
        sb.append(", progress=").append(progress);
        sb.append(", remove=").append(remove);
        sb.append(", listeners=").append(listeners);
        sb.append('}');
        return sb.toString();
    }
    
    public abstract void update(ZonedDateTime time);
    
}
