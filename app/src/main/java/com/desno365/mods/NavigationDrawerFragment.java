package com.desno365.mods;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.desno365.mods.Activities.MainActivity;


/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    public static DrawerLayout mDrawerLayout;
    public ListView mDrawerListView;
    private View mFragmentContainerView;

    // commented code that change the checked item
    //public int mCurrentSelectedPosition = 0;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(R.layout.navigation_drawer_fragment, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        ArrayAdapter ad = new ArrayAdapter<String>(
                MainActivity.activity.getApplicationContext(),
                R.layout.navigation_drawer_item,
                android.R.id.text1,
                new String[]{
                        getString(R.string.home_title),
                        getString(R.string.mod5_title),
                        getString(R.string.mod1_title),
                        getString(R.string.mod2_title),
                        getString(R.string.mod3_title),
                        getString(R.string.mod4_title),
                        getString(R.string.mod6_title),
                        getString(R.string.action_help),
                        getString(R.string.news_title),
                        getString(R.string.action_settings),
                }) {
            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                View v = super.getView(position, view, viewGroup);

                Typeface font = Typeface.createFromAsset(MainActivity.myMainActivity.get().getAssets(),"fonts/minecraft.ttf");
                TextView tv = (TextView) v;

                tv.setTypeface(font);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    tv.setShadowLayer(1, Math.round(tv.getLineHeight() / 8), Math.round(tv.getLineHeight() / 8), getResources().getColor(R.color.drawerTextShadow));
                else
                    tv.setShadowLayer(0.0001F, Math.round(tv.getLineHeight() / 8), Math.round(tv.getLineHeight() / 8), getResources().getColor(R.color.drawerTextShadow));

                // commented code that changes the checked item
                // custom colors selected items
                /*if(position == mCurrentSelectedPosition)
                    v.setBackgroundColor(getResources().getColor(R.color.minecraft_not_pressed));
                else
                    v.setBackgroundColor(getResources().getColor(R.color.white));*/

                v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                return v;
            }
        };

        mDrawerListView.setAdapter(ad);

        // commented code that change the checked item
        //mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                MainActivity.toolbar,             /* toolbar */
                R.string.app_name,  /* "open drawer" description for accessibility */
                R.string.close_dialog  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void selectItem(int position) {
        // commented code that change the checked item
        //mCurrentSelectedPosition = position;
        /*if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }*/
        if (mDrawerLayout != null && mFragmentContainerView != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}

