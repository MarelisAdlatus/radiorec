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

import java.time.ZonedDateTime;

/**
 *
 * @author Marelis Adlatus <software@marelis.cz>
 */
public class StationDirector extends ResponseList<Station> {

    @Override
    public boolean containsItem(Station station) {
        for (Station item : this) {
            if (item.stationFile.equals(station.stationFile)) {
                return true;
            }
        }
        return false;
    }

    public int getPlayingItem() {
        int size = itemsCount();
        for (int idx = 0; idx < size; idx++) {
            Station station = getItem(idx);
            if (station.isStatus(Station.PLAYBACK_RUNS)) {
                return idx;
            }
        }
        return -1;
    }

    @Override
    public void update(ZonedDateTime time) {
        forEach(station -> station.update(time));
    }

}
