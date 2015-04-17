package com.desno365.mods.Mods;

import com.desno365.mods.Values.Keys;
import com.desno365.mods.Values.NotificationsId;
import com.desno365.mods.R;
import com.desno365.mods.Tabs.FragmentTab2;

public class Portal extends Mod {

    public static final int viewPagerPosition = 2;

    public Portal() {

        super();

        this.ID = 2;
        this.nameId = R.string.mod1_title;

        this.NOTIFICATION_ID_NEW_VERSION = NotificationsId.ID_PORTAL_NEW_VERSION;
        this.DOWNLOAD_FROM_WEBSITE_LINK = Keys.KEY_PORTAL_DOWNLOAD;
        this.THREAD_LINK = Keys.KEY_PORTAL_THREAD;

    }

    public static FragmentTab2 getFragmentTab() {
        return new FragmentTab2();
    }

}