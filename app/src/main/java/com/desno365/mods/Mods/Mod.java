package com.desno365.mods.Mods;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Mod {

    public int ID;

    public int nameId;

    public int NOTIFICATION_ID_NEW_VERSION;
    public String DOWNLOAD_FROM_WEBSITE_LINK;
    public String THREAD_LINK;

    public String getName(Context context) {
        return context.getString(this.nameId);
    }

    public Intent getDownloadFromWebsiteIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(this.DOWNLOAD_FROM_WEBSITE_LINK));
    }

    public Intent getVisitThreadIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(this.THREAD_LINK));
    }

}
