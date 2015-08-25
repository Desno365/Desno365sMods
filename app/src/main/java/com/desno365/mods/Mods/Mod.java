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
import android.content.Intent;
import android.net.Uri;


public class Mod {

	private Context context;

	public int ID;
	public int nameId; // id of the string containing the name

	private String version;
	private String compatibility;
	private String changelog;

	public int NOTIFICATION_ID_NEW_VERSION; // id for a notification
	public String DOWNLOAD_FROM_WEBSITE_LINK; // link to website
	public String THREAD_LINK; // link to minecraftforum thread

	public Mod(Context context) {
		this.context = context;
	}

	public String getName() {
		return context.getString(this.nameId);
	}

	public Intent getDownloadFromWebsiteIntent() {
		return new Intent(Intent.ACTION_VIEW, Uri.parse(this.DOWNLOAD_FROM_WEBSITE_LINK));
	}

	public Intent getVisitThreadIntent() {
		return new Intent(Intent.ACTION_VIEW, Uri.parse(this.THREAD_LINK));
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCompatibility() {
		return compatibility;
	}

	public void setCompatibility(String compatibility) {
		this.compatibility = compatibility;
	}

	public String getChangelog() {
		return changelog;
	}

	public void setChangelog(String changelog) {
		this.changelog = changelog;
	}
}
