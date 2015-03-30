package com.desno365.mods.Mods;

import com.desno365.mods.Keys;
import com.desno365.mods.NotificationsId;
import com.desno365.mods.R;
import com.desno365.mods.Tabs.FragmentTab5;

public class Jukebox extends Mod {

    public Jukebox() {

        super();

        this.ID = 5;
        this.nameId = R.string.mod4_title;

        this.NOTIFICATION_ID_NEW_VERSION = NotificationsId.ID_JUKEBOX_NEW_VERSION;
        this.DOWNLOAD_FROM_WEBSITE_LINK = Keys.KEY_JUKEBOX_DOWNLOAD;
        this.THREAD_LINK = Keys.KEY_JUKEBOX_THREAD;

    }

    public static FragmentTab5 getFragmentTab() {
        return new FragmentTab5();
    }

}