package com.desno365.mods.Mods;

import com.desno365.mods.Keys;
import com.desno365.mods.NotificationsId;
import com.desno365.mods.R;
import com.desno365.mods.Tabs.FragmentTab6;

public class DesnoGuns extends Mod {

    public static final int viewPagerPosition = 1;

    public DesnoGuns() {

        super();

        this.ID = 1;
        this.nameId = R.string.mod5_title;

        this.NOTIFICATION_ID_NEW_VERSION = NotificationsId.ID_DESNOGUNS_NEW_VERSION;
        this.DOWNLOAD_FROM_WEBSITE_LINK = Keys.KEY_DESNOGUNS_DOWNLOAD;
        this.THREAD_LINK = Keys.KEY_DESNOGUNS_THREAD;

    }

    public static FragmentTab6 getFragmentTab() {
        return new FragmentTab6();
    }

}
