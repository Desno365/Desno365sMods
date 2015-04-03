package com.desno365.mods.Mods;

import com.desno365.mods.Keys;
import com.desno365.mods.NotificationsId;
import com.desno365.mods.R;
import com.desno365.mods.Tabs.FragmentTab4;

public class Turrets extends Mod {

    public static final int viewPagerPosition = 4;

    public Turrets() {

        super();

        this.ID = 4;
        this.nameId = R.string.mod3_title;

        this.NOTIFICATION_ID_NEW_VERSION = NotificationsId.ID_TURRETS_NEW_VERSION;
        this.DOWNLOAD_FROM_WEBSITE_LINK = Keys.KEY_TURRETS_DOWNLOAD;
        this.THREAD_LINK = Keys.KEY_TURRETS_THREAD;

    }

    public static FragmentTab4 getFragmentTab() {
        return new FragmentTab4();
    }

}
