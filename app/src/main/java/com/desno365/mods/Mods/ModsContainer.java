/*
 * Copyright 2015 Dennis Motta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.desno365.mods.Mods;

import android.content.Context;

public class ModsContainer {

    public DesnoGuns desnoGuns;
    public Jukebox jukebox;
    public Laser laser;
    public Portal portal;
    public Unreal unreal;

    public ModsContainer(Context context) {
        desnoGuns = new DesnoGuns(context);
        jukebox = new Jukebox(context);
        laser = new Laser(context);
        portal = new Portal(context);
        unreal = new Unreal(context);
    }

}
