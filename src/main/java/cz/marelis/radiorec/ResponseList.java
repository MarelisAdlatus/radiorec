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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 * @param <T>
 */
public abstract class ResponseList<T extends ResponseNode> extends ResponseNode
        implements ResponseListener, Iterable<T> {

    public static final int EVT_ITEM_ADDED = 0;
    public static final int EVT_ITEM_REMOVED = 1;
    public static final int EVT_ITEM_CHANGED = 2;

    private final ArrayList<T> items = new ArrayList<>();

    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }

    public int itemsCount() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clearItems() {
        int size = items.size();
        ArrayList<T> list = new ArrayList<>();
        if (size > 0) {
            items.forEach((T item) -> item.removeResponseListener(ResponseList.this));
            list.addAll(items);
            items.clear();
            fireResponseEvent(new ResponseEvent(this, EVT_ITEM_REMOVED, list, "ResponseList.clearItems"));
        }
    }

    public T getItem(int idx) {
        return items.get(idx);
    }

    public void removeItem(T item) {
        int idx = items.indexOf(item);
        if (idx >= 0) {
            removeItem(idx);
        }
    }

    public T removeItem(int idx) {
        T old = items.remove(idx);
        old.removeResponseListener(this);
        fireResponseEvent(new ResponseEvent(this, EVT_ITEM_REMOVED, old, "ResponseList.removeItem"));
        return old;
    }

    public void removeItems(Collection<? extends T> c) {
        if (c.isEmpty()) {
            return;
        }
        items.removeAll(c);
        c.forEach(item -> item.removeResponseListener(this));
        fireResponseEvent(new ResponseEvent(this, EVT_ITEM_REMOVED, c, "ResponseList.removeItems"));
    }

    public void setItem(int idx, T item) {
        if (item == null) {
            return;
        }
        ResponseNode old = getItem(idx);
        old.removeResponseListener(this);
        items.set(idx, item);
        item.addResponseListener(this);
        fireResponseEvent(new ResponseEvent(this, EVT_ITEM_CHANGED, item, "ResponseList.setItem"));
    }

    public void addItem(T item) {
        if (item == null) {
            return;
        }
        items.add(item);
        item.addResponseListener(this);
        fireResponseEvent(new ResponseEvent(this, EVT_ITEM_ADDED, item, "ResponseList.addItem"));
    }

    public void addItems(Collection<? extends T> c) {
        if (c.isEmpty()) {
            return;
        }
        items.addAll(c);
        c.forEach(item -> item.addResponseListener(this));
        fireResponseEvent(new ResponseEvent(this, EVT_ITEM_ADDED, c, "ResponseList.addItems"));
    }

    public void replaceItems(Collection<? extends T> c) {
        clearItems();
        addItems(c);
    }

    public int indexOfItem(T item) {
        return items.indexOf(item);
    }

    public boolean containsItem(T item) {
        for (var node : items) {
            if (node.props.equals(item.props)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResponseEvent(ResponseEvent re) {
        fireResponseEvent(re);
    }

}
