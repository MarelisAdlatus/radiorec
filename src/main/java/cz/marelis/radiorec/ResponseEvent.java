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

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class ResponseEvent {

    private final Object src;
    private final int id;
    private final Object obj;
    private final String msg;

    public ResponseEvent(Object src, int id, Object obj, String msg) {
        this.src = src;
        this.id = id;
        this.obj = obj;
        this.msg = msg;
    }

    public Object getSource() {
        return src;
    }

    public int getId() {
        return id;
    }

    public Object getObject() {
        return obj;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "ResponseEvent{" + "id=" + id + ", msg=" + msg + ", src=" + src.getClass() + ", obj=" + obj + '}';
    }
    
}
