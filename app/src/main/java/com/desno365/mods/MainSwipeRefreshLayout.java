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

package com.desno365.mods;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;

import com.desno365.mods.Activities.MainActivity;
import com.desno365.mods.Mods.DesnoGuns;
import com.desno365.mods.Mods.Jukebox;
import com.desno365.mods.Mods.Laser;
import com.desno365.mods.Mods.Portal;
import com.desno365.mods.Mods.Turrets;
import com.desno365.mods.Mods.Unreal;


public class MainSwipeRefreshLayout extends SwipeRefreshLayout {

	public MainSwipeRefreshLayout(Context context) {
		super(context);
	}

	public MainSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean canChildScrollUp() {
		try {
			int page = MainActivity.mViewPager.getCurrentItem();
			switch (page) {
				case 0:
					return findViewById(R.id.scroll_tab_home).canScrollVertically(-1);

				case DesnoGuns.viewPagerPosition:
					return findViewById(R.id.scroll_tab_guns).canScrollVertically(-1);

				case Portal.viewPagerPosition:
					return findViewById(R.id.scroll_tab_portal).canScrollVertically(-1);

				case Laser.viewPagerPosition:
					return findViewById(R.id.scroll_tab_laser).canScrollVertically(-1);

				case Turrets.viewPagerPosition:
					return findViewById(R.id.scroll_tab_turrets).canScrollVertically(-1);

				case Jukebox.viewPagerPosition:
					return findViewById(R.id.scroll_tab_jukebox).canScrollVertically(-1);

				case Unreal.viewPagerPosition:
					return findViewById(R.id.scroll_tab_unreal).canScrollVertically(-1);

				default:
					return false;
			}
		} catch (Exception e) {
			Log.e(MainActivity.TAG, "NullPointerException at canChildScrollUp() at SwipeRefreshLayout", e);
			return false;
		}
	}
}
