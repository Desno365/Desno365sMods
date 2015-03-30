package com.desno365.mods.Mods;

import com.desno365.mods.Keys;
import com.desno365.mods.NotificationsId;
import com.desno365.mods.R;
import com.desno365.mods.Tabs.FragmentTab3;

public class Laser extends Mod {

    public Laser() {

        super();

        this.ID = 3;
        this.nameId = R.string.mod2_title;

        this.NOTIFICATION_ID_NEW_VERSION = NotificationsId.ID_LASER_NEW_VERSION;
        this.DOWNLOAD_FROM_WEBSITE_LINK = Keys.KEY_LASER_DOWNLOAD;
        this.THREAD_LINK = Keys.KEY_LASER_THREAD;

    }

    public static FragmentTab3 getFragmentTab() {
        return new FragmentTab3();
    }

}
