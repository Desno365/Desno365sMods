/*
 *
 * Copyright 2017 Dennis Motta
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

import com.desno365.mods.R;
import com.desno365.mods.SharedConstants.Keys;
import com.desno365.mods.SharedConstants.NotificationsId;
import com.desno365.mods.Tabs.FragmentTab5;


public class Jukebox extends Mod {

	public static final int viewPagerPosition = 4;

	public Jukebox(Context context) {

		super(context);

		this.nameId = R.string.mod4_title;

		setVersion(context.getResources().getString(R.string.loading));
		setCompatibility(context.getResources().getString(R.string.loading));
		setChangelog(context.getResources().getString(R.string.loading));

		this.NOTIFICATION_ID_NEW_VERSION = NotificationsId.ID_JUKEBOX_NEW_VERSION;
		this.DOWNLOAD_FROM_WEBSITE_LINK = Keys.KEY_JUKEBOX_DOWNLOAD;
	}

	public static FragmentTab5 getFragmentTab() {
		return new FragmentTab5();
	}

}