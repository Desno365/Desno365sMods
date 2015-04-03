package com.desno365.mods;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.desno365.mods.Activities.MainActivity;
import com.desno365.mods.Mods.DesnoGuns;
import com.desno365.mods.Mods.Jukebox;
import com.desno365.mods.Mods.Laser;
import com.desno365.mods.Mods.Portal;
import com.desno365.mods.Mods.Turrets;
import com.desno365.mods.Mods.Unreal;

public class SwipeLayout extends SwipeRefreshLayout {

    public SwipeLayout(Context context) {
        super(context);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp()
    {
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
    }
}
