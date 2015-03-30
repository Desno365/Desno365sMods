package com.desno365.mods.Activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.desno365.mods.DesnoUtils;
import com.desno365.mods.Keys;
import com.desno365.mods.Mods.DesnoGuns;
import com.desno365.mods.Mods.Jukebox;
import com.desno365.mods.Mods.Laser;
import com.desno365.mods.Mods.Portal;
import com.desno365.mods.Mods.Turrets;
import com.desno365.mods.Mods.Unreal;
import com.desno365.mods.NavigationDrawerFragment;
import com.desno365.mods.R;
import com.desno365.mods.Receivers.AlarmReceiver;
import com.desno365.mods.Tabs.FragmentTab1;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

public class MainActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static WeakReference<MainActivity> myMainActivity = null;
    public static Activity activity;
    private static final String TAG = "DesnoMods-MainActivity";

    public static String newsString = "Use the refresh button to download the news.";
    public static String gunsModVersion = "Latest version: unknown, please use the refresh button.";
    public static String gunsModChangelog = "Use the refresh button to download the changelog.";
    public static String portalModVersion = "Latest version: unknown, please use the refresh button.";
    public static String portalModChangelog = "Use the refresh button to download the changelog.";
    public static String laserModVersion = "Latest version: unknown, please use the refresh button.";
    public static String laserModChangelog = "Use the refresh button to download the changelog.";
    public static String turretsModVersion = "Latest version: unknown, please use the refresh button.";
    public static String turretsModChangelog = "Use the refresh button to download the changelog.";
    public static String jukeboxModVersion = "Latest version: unknown, please use the refresh button.";
    public static String jukeboxModChangelog = "Use the refresh button to download the changelog.";
    public static String unrealMapVersion = "Latest version: unknown, please use the refresh button.";
    public static String unrealMapChangelog = "Use the refresh button to download the changelog.";

    private Menu optionsMenu;
    private NavigationDrawerFragment mNavigationDrawerFragment = new NavigationDrawerFragment();

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    //The {@link ViewPager} that will display the three primary sections of the app, one at a time.
    ViewPager mViewPager;

    @SuppressLint("CommitPrefEdits")
    public void onCreate(Bundle savedInstanceState) {
        DesnoUtils.setSavedTheme(this);
        DesnoUtils.setSavedLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "App launched.");

        activity = this;
        myMainActivity = new WeakReference<MainActivity>(this);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        @SuppressLint("AppCompatMethod")
        final ActionBar actionBar = getActionBar();
        assert actionBar != null;

        //set if the user can click the icon
        actionBar.setHomeButtonEnabled(true);

        // Show Actionbar Icon
        actionBar.setDisplayShowHomeEnabled(true);

        // Show Actionbar Title
        actionBar.setDisplayShowTitleEnabled(true);

        // Create Actionbar Tabs
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager and attaching the adapter
        mViewPager = (ViewPager) findViewById(R.id.fragment_container);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);
        tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections
                mNavigationDrawerFragment.selectItem(position);
                if(position == 0)
                    actionBar.setTitle(getResources().getString(R.string.app_name));
                else
                    actionBar.setTitle(mAppSectionsPagerAdapter.getPageTitle(position));
            }
        });

        // Set up the drawer.
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        //action to do at the first launch of the app
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPrefs.getBoolean("is_first_launch", true))
        {
            //first launch, the app has never been launched before
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("refresh_on_start", true);
            editor.commit();
            editor.putBoolean("is_first_launch", false);
            editor.putBoolean("notification_bool", true);
            editor.putString("sync_frequency", "4");
            editor.putString("selected_theme", "0");
            editor.putString("selected_language", "not_changed");
            editor.putString("selected_animations", "0");
            editor.putBoolean("user_understood_full_resolution_help", false);
            editor.apply();
            Log.i(TAG, "First launch");
        }

        //load alarmManager for notifications
        AlarmReceiver aR = new AlarmReceiver();
        aR.cancelAlarm(getApplicationContext());
        aR.setAlarm(getApplicationContext());

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if(position <= 6) {
            if (mAppSectionsPagerAdapter != null) {
                mViewPager.setCurrentItem(position);
            } else {
                // update the main content by replacing fragments
                Fragment myFragment;
                switch (position) {
                    case 0:
                        myFragment = new FragmentTab1();
                        break;
                    case 1:
                        myFragment = DesnoGuns.getFragmentTab();
                        break;
                    case 2:
                        myFragment = Portal.getFragmentTab();
                        break;
                    case 3:
                        myFragment = Laser.getFragmentTab();
                        break;
                    case 4:
                        myFragment = Turrets.getFragmentTab();
                        break;
                    case 5:
                        myFragment = Jukebox.getFragmentTab();
                        break;
                    case 6:
                        myFragment = Unreal.getFragmentTab();
                        break;
                    default:
                        myFragment = new FragmentTab1();
                        break;
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, myFragment)
                        .commit();
            }
        } else {

            if(position == 7) {
                new android.os.Handler().postDelayed(new Runnable(){
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                        DesnoUtils.changeStartAnimations(activity);
                    }
                }, 200);
            }

            if(position == 8) {
                new android.os.Handler().postDelayed(new Runnable(){
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        DesnoUtils.changeStartAnimations(activity);
                    }
                }, 200);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //save the menu in a variable
        this.optionsMenu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        //refresh content on start
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPrefs.getBoolean("refresh_on_start", true)) {
            new android.os.Handler().postDelayed(new Runnable(){
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                RetrieveModsUpdates downloadTask = new RetrieveModsUpdates();
                                downloadTask.execute((Void) null);
                            } catch (Exception err) {
                                Log.e(TAG, "Exception in runOnUiThread() in onCreate() ", err);
                            }
                        }
                    });
                }
            }, 1000);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case android.R.id.home:
                if(mNavigationDrawerFragment.isDrawerOpen())
                    NavigationDrawerFragment.mDrawerLayout.closeDrawer(findViewById(R.id.navigation_drawer));
                else
                    NavigationDrawerFragment.mDrawerLayout.openDrawer(findViewById(R.id.navigation_drawer));
                return true;

            case R.id.action_refresh:
                RetrieveModsUpdates downloadTask = new RetrieveModsUpdates();
                downloadTask.execute((Void) null);
                return true;

            case R.id.action_info:
                startActivity(new Intent(this, AboutActivity.class));
                DesnoUtils.changeStartAnimations(activity);
                return true;

            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                DesnoUtils.changeStartAnimations(activity);
                return true;

            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_body));
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                DesnoUtils.changeStartAnimations(activity);
                return true;

            case R.id.action_feedback:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_APP_THREAD)));
                DesnoUtils.changeStartAnimations(activity);
                Toast.makeText(getApplicationContext(), getString(R.string.feedback_toast), Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_rate:
                final String appPackageName = getPackageName();
                try {
                    //play store installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    DesnoUtils.changeStartAnimations(activity);
                } catch (android.content.ActivityNotFoundException anfe) {
                    //play store not installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                    DesnoUtils.changeStartAnimations(activity);
                }
                return true;

            case R.id.action_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                DesnoUtils.changeStartAnimations(activity);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        //add icons near menu items
        if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    Log.e(TAG, "onMenuOpened", e);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    // refresh TextViews after the content has been refreshed
    public void refreshTabPages() {
        myMainActivity.get().runOnUiThread(new Runnable() {
            public void run() {

                try {
                    TextView newsText = (TextView) getWindow().getDecorView().findViewById(R.id.news_container);
                    newsText.setText(android.text.Html.fromHtml(newsString));
                } catch (Exception err) {
                    Log.e(TAG, "Exception in refreshTabPages() in news ", err);
                }

                try {
                    TextView textUpdatesGuns = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_guns_is);
                    textUpdatesGuns.setText(gunsModVersion);

                    TextView textChangelogGuns = (TextView) getWindow().getDecorView().findViewById(R.id.guns_changelog);
                    textChangelogGuns.setText(android.text.Html.fromHtml(gunsModChangelog));
                } catch (Exception err) {
                    Log.e(TAG, "Exception in refreshTabPages() in portal ", err);
                }

                try {
                    TextView textUpdatesPortal = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_portal_is);
                    textUpdatesPortal.setText(portalModVersion);

                    TextView textChangelogPortal = (TextView) getWindow().getDecorView().findViewById(R.id.portal_changelog);
                    textChangelogPortal.setText(android.text.Html.fromHtml(portalModChangelog));
                } catch (Exception err) {
                    Log.e(TAG, "Exception in refreshTabPages() in portal ", err);
                }

                try {
                    TextView textUpdatesLaser = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_laser_is);
                    textUpdatesLaser.setText(laserModVersion);

                    TextView textChangelogLaser = (TextView) getWindow().getDecorView().findViewById(R.id.laser_changelog);
                    textChangelogLaser.setText(android.text.Html.fromHtml(laserModChangelog));
                } catch (Exception err) {
                    Log.e(TAG, "Exception in refreshTabPages() in laser ", err);
                }

                try {
                    TextView textUpdatesTurrets = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_turrets_is);
                    textUpdatesTurrets.setText(turretsModVersion);

                    TextView textChangelogTurrets = (TextView) getWindow().getDecorView().findViewById(R.id.turrets_changelog);
                    textChangelogTurrets.setText(android.text.Html.fromHtml(turretsModChangelog));
                } catch (Exception err) {
                    Log.e(TAG, "Exception in refreshTabPages() in turrets ", err);
                }

                try {
                    TextView textUpdatesJukebox = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_jukebox_is);
                    textUpdatesJukebox.setText(jukeboxModVersion);

                    TextView textChangelogJukebox = (TextView) getWindow().getDecorView().findViewById(R.id.jukebox_changelog);
                    textChangelogJukebox.setText(android.text.Html.fromHtml(jukeboxModChangelog));
                } catch (Exception err) {
                    Log.e(TAG, "Exception in refreshTabPages() in jukebox ", err);
                }

                try {
                    TextView textUpdatesUnreal = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_unreal_is);
                    textUpdatesUnreal.setText(unrealMapVersion);

                    TextView textChangelogUnreal = (TextView) getWindow().getDecorView().findViewById(R.id.unreal_changelog);
                    textChangelogUnreal.setText(android.text.Html.fromHtml(unrealMapChangelog));
                } catch (Exception err) {
                    Log.e(TAG, "Exception in refreshTabPages() in unreal ", err);
                }
            }
        });
    }

    public void onViewClick(View v) {
        switch(v.getId()) {
            // minecraftforum.net thread buttons
            case R.id.minecraft_thread_guns_button:
                startActivity(new DesnoGuns().getVisitThreadIntent());
                DesnoUtils.changeStartAnimations(activity);
                break;
            case R.id.minecraft_thread_portal_button:
                startActivity(new Portal().getVisitThreadIntent());
                DesnoUtils.changeStartAnimations(activity);
                break;
            case R.id.minecraft_thread_laser_button:
                startActivity(new Laser().getVisitThreadIntent());
                DesnoUtils.changeStartAnimations(activity);
                break;
            case R.id.minecraft_thread_turrets_button:
                startActivity(new Turrets().getVisitThreadIntent());
                DesnoUtils.changeStartAnimations(activity);
                break;
            case R.id.minecraft_thread_jukebox_button:
                startActivity(new Jukebox().getVisitThreadIntent());
                DesnoUtils.changeStartAnimations(activity);
                break;
            case R.id.minecraft_thread_unreal_button:
                startActivity(new Unreal().getVisitThreadIntent());
                DesnoUtils.changeStartAnimations(activity);
                break;

            // download from website buttons
            case R.id.download_guns_button:
                startActivity(new DesnoGuns().getDownloadFromWebsiteIntent());
                DesnoUtils.changeStartAnimations(activity);
                break;
            case R.id.download_portal_button:
                startActivity(new Portal().getDownloadFromWebsiteIntent());
                DesnoUtils.changeStartAnimations(activity);
                break;
            case R.id.download_laser_button:
                startActivity(new Laser().getDownloadFromWebsiteIntent());
                DesnoUtils.changeStartAnimations(activity);
                break;
            case R.id.download_turrets_button:
                startActivity(new Turrets().getDownloadFromWebsiteIntent());
                DesnoUtils.changeStartAnimations(activity);
                break;
            case R.id.download_jukebox_button:
                startActivity(new Jukebox().getDownloadFromWebsiteIntent());
                DesnoUtils.changeStartAnimations(activity);
                break;
            case R.id.download_unreal_button:
                startActivity(new Unreal().getDownloadFromWebsiteIntent());
                DesnoUtils.changeStartAnimations(activity);
                break;

            // installation video tutorial button
            case R.id.installation_video_guns_button:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_DESNOGUNS_VIDEO_TUTORIAL)));
                DesnoUtils.changeStartAnimations(activity);
                break;

            // start help activity
            case R.id.start_help_activity:
                startActivity(new Intent(this, HelpActivity.class));
                DesnoUtils.changeStartAnimations(activity);
                break;
        }
    }

    /** test alarm onClick
     *
    public void testAlarm(View v) {
        AlarmReceiver aR = new AlarmReceiver();
        aR.onReceive(getApplicationContext(), null);
    }*/

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu.findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                        }
                    });
                }else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    public class RetrieveModsUpdates extends AsyncTask<Void, String, Void> {

        private String latestGunsVersion = "";
        private String latestPortalVersion = "";
        private String latestLaserVersion = "";
        private String latestTurretsVersion = "";
        private String latestJukeboxVersion = "";
        private String latestUnrealVersion = "";

        @Override
        protected Void doInBackground(Void... params) {

            setRefreshActionButtonState(true);

            if(DesnoUtils.isNetworkAvailable(getApplicationContext())) {
                latestGunsVersion = DesnoUtils.getTextFromUrl(Keys.KEY_DESNOGUNS_VERSION);
                latestPortalVersion = DesnoUtils.getTextFromUrl(Keys.KEY_PORTAL_VERSION);
                latestLaserVersion = DesnoUtils.getTextFromUrl(Keys.KEY_LASER_VERSION);
                latestTurretsVersion = DesnoUtils.getTextFromUrl(Keys.KEY_TURRETS_VERSION);
                latestJukeboxVersion = DesnoUtils.getTextFromUrl(Keys.KEY_JUKEBOX_VERSION);
                latestUnrealVersion = DesnoUtils.getTextFromUrl(Keys.KEY_UNREAL_VERSION);

                newsString = DesnoUtils.getTextFromUrl(Keys.KEY_NEWS);
                gunsModChangelog = DesnoUtils.getTextFromUrl(Keys.KEY_DESNOGUNS_CHANGELOG);
                portalModChangelog = DesnoUtils.getTextFromUrl(Keys.KEY_PORTAL_CHANGELOG);
                laserModChangelog = DesnoUtils.getTextFromUrl(Keys.KEY_LASER_CHANGELOG);
                turretsModChangelog = DesnoUtils.getTextFromUrl(Keys.KEY_TURRETS_CHANGELOG);
                jukeboxModChangelog = DesnoUtils.getTextFromUrl(Keys.KEY_JUKEBOX_CHANGELOG);
                unrealMapChangelog = DesnoUtils.getTextFromUrl(Keys.KEY_UNREAL_CHANGELOG);
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(myMainActivity.get().getApplicationContext(), getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.i(TAG, "onPostExecute now, the AsyncTask finished");

            DesnoUtils.notifyForNewUpdates(getApplicationContext(), latestGunsVersion, latestPortalVersion, latestLaserVersion, latestTurretsVersion, latestJukeboxVersion, latestUnrealVersion);

            gunsModVersion = getResources().getString(R.string.latest_version_is) + " " + latestGunsVersion;
            portalModVersion = getResources().getString(R.string.latest_version_is) + " " + latestPortalVersion;
            laserModVersion = getResources().getString(R.string.latest_version_is) + " " + latestLaserVersion;
            turretsModVersion = getResources().getString(R.string.latest_version_is) + " " + latestTurretsVersion;
            jukeboxModVersion = getResources().getString(R.string.latest_version_is) + " " + latestJukeboxVersion;
            unrealMapVersion = getResources().getString(R.string.latest_version_is) + " " + latestUnrealVersion;
            refreshTabPages();
            setRefreshActionButtonState(false);
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new FragmentTab1();
                case 1:
                    return DesnoGuns.getFragmentTab();
                case 2:
                    return Portal.getFragmentTab();
                case 3:
                    return Laser.getFragmentTab();
                case 4:
                    return Turrets.getFragmentTab();
                case 5:
                    return Jukebox.getFragmentTab();
                case 6:
                    return Unreal.getFragmentTab();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return myMainActivity.get().getString(R.string.home_title);
                case 1:
                    return new DesnoGuns().getName(myMainActivity.get());
                case 2:
                    return new Portal().getName(myMainActivity.get());
                case 3:
                    return new Laser().getName(myMainActivity.get());
                case 4:
                    return new Turrets().getName(myMainActivity.get());
                case 5:
                    return new Jukebox().getName(myMainActivity.get());
                case 6:
                    return new Unreal().getName(myMainActivity.get());
                default:
                    return "Missing title";
            }
        }
    }

}
