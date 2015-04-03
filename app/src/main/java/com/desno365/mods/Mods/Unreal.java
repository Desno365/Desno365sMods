package com.desno365.mods.Mods;

import com.desno365.mods.Keys;
import com.desno365.mods.NotificationsId;
import com.desno365.mods.R;
import com.desno365.mods.Tabs.FragmentTab7;

public class Unreal extends Mod {

    public static final int viewPagerPosition = 6;

    public Unreal() {

        super();

        this.ID = 6;
        this.nameId = R.string.mod6_title;

        this.NOTIFICATION_ID_NEW_VERSION = NotificationsId.ID_UNREAL_NEW_VERSION;
        this.DOWNLOAD_FROM_WEBSITE_LINK = Keys.KEY_UNREAL_DOWNLOAD;
        this.THREAD_LINK = Keys.KEY_UNREAL_THREAD;

    }

    public static FragmentTab7 getFragmentTab() {
        return new FragmentTab7();
    }

}
