package com.desno365.mods;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    //The {@link ViewPager} that will display the three primary sections of the app, one at a time.
    ViewPager mViewPager;

    public static WeakReference<MainActivity> myMainActivity = null;
    private static final String TAG = "DesnoMods-MainActivity";

    public static String newsString = "Use the refresh button to download the news.";
    public static String portalModVersion = "Latest version: unknown, please use the refresh button.";
    public static String portalModChangelog = "Use the refresh button to download the changelog.";
    public static String laserModVersion = "Latest version: unknown, please use the refresh button.";
    public static String laserModChangelog = "Use the refresh button to download the changelog.";
    public static String turretsModVersion = "Latest version: unknown, please use the refresh button.";
    public static String turretsModChangelog = "Use the refresh button to download the changelog.";
    public static String jukeboxModVersion = "Latest version: unknown, please use the refresh button.";
    public static String jukeboxModChangelog = "Use the refresh button to download the changelog.";

    private Menu optionsMenu;

    @SuppressLint("CommitPrefEdits")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "App launched.");

        myMainActivity = new WeakReference<MainActivity>(this);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        @SuppressLint("AppCompatMethod")
        final ActionBar actionBar = getActionBar();
        assert actionBar != null;

        // Show Actionbar Icon
        actionBar.setDisplayShowHomeEnabled(true);

        // Show Actionbar Title
        actionBar.setDisplayShowTitleEnabled(true);

        // Create Actionbar Tabs
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.fragment_container);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
                if(position == 0)
                    actionBar.setTitle(getResources().getString(R.string.app_name));
                else
                    actionBar.setTitle(mAppSectionsPagerAdapter.getPageTitle(position));
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

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
            editor.putString("sync_frequency", "12");
            editor.apply();
            Log.i(TAG, "First launch");
            infoAlertDialog();
        }

        //load alarmManager for notifications
        AlarmReceiver aR = new AlarmReceiver();
        aR.cancelAlarm(getApplicationContext());
        aR.setAlarm(getApplicationContext());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
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

            case R.id.action_refresh:
                RetrieveModsUpdates downloadTask = new RetrieveModsUpdates();
                downloadTask.execute((Void) null);
                return true;

            case R.id.action_info:
                infoAlertDialog();
                return true;

            case R.id.action_help:
                Intent intentHelp = new Intent(this,HelpActivity.class);
                startActivity(intentHelp);
                return true;

            case R.id.action_share:
                // TODO: different text, depends on what page the user is watching
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_body));
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                return true;

            case R.id.action_rate:
                final String appPackageName = getPackageName();
                try {
                    //play store installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    //play store not installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;

            case R.id.action_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
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

    //alert dialog displayed when pressing the info button in action bar
    private void infoAlertDialog() {

        View mLoginConfirmView = View.inflate(this, R.layout.actionbar_informations, null);

        android.app.AlertDialog.Builder popup = new android.app.AlertDialog.Builder(this);
        popup.setView(mLoginConfirmView);
        popup.setTitle(getResources().getString(R.string.action_info));

        popup.setPositiveButton(getResources().getString(R.string.take_me_play_store), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = getPackageName();
                try {
                    //play store installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    //play store not installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        popup.setNegativeButton(getResources().getString(R.string.close_dialog), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });


        popup.show();
    }

    //refresh TextViews after the content has been refreshed
    public void refreshTabPages() {
        try {
            TextView textNews = (TextView) getWindow().getDecorView().findViewById(R.id.news_container);
            textNews.setText(android.text.Html.fromHtml(newsString));
        } catch (Exception err) {
            Log.e(TAG, "Exception in refreshTabPages() in news ", err);
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
    }

    public void onViewClick(View v) {
        switch(v.getId()) {
            //minecraftforum.net thread buttons
            case R.id.minecraft_thread_portal_button:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.minecraftforum.net/forums/minecraft-pocket-edition/mcpe-mods-tools/2097326-mod-beta-portal-2-mod-portal-gun-r008-by-desno365")));
                break;
            case R.id.minecraft_thread_laser_button:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.minecraftforum.net/forums/minecraft-pocket-edition/mcpe-mods-tools/2179257-mod-beta-laser-mod-laser-weapons-r003-by-desno365")));
                break;
            case R.id.minecraft_thread_turrets_button:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.minecraftforum.net/forums/minecraft-pocket-edition/mcpe-mods-tools/2201372-mod-beta-turrets-mod-kill-mobs-automatically-r001")));
                break;
            case R.id.minecraft_thread_jukebox_button:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.minecraftforum.net/forums/minecraft-pocket-edition/mcpe-mods-tools/2173829-mod-jukebox-mod-pc-porting-r002-by-desno365")));
                break;

            //download from website buttons
            case R.id.download_portal_button:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://desno365.github.io/minecraft/portal2-mod/")));
                break;
            case R.id.download_laser_button:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://desno365.github.io/minecraft/laser-mod")));
                break;
            case R.id.download_turrets_button:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://desno365.github.io/minecraft/turrets-mod")));
                break;
            case R.id.download_jukebox_button:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://desno365.github.io/minecraft/jukebox-mod")));
                break;

            //twitter image and text
            case R.id.twitter_image:case R.id.twitter_text:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/desno365")));
                break;

            //github image and text
            case R.id.github_image:case R.id.github_text:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Desno365/Desno365sMods")));
                break;

            //start help activity
            case R.id.start_help_activity:
                Intent intentHelp = new Intent(this,HelpActivity.class);
                startActivity(intentHelp);
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

        private String latestPortalVersion = "";
        private String latestLaserVersion = "";
        private String latestTurretsVersion = "";
        private String latestJukeboxVersion = "";

        @Override
        protected Void doInBackground(Void... params) {

            setRefreshActionButtonState(true);

            if(DesnoUtils.isNetworkAvailable(getApplicationContext())) {
                latestPortalVersion = DesnoUtils.getTextFromUrl("https://raw.githubusercontent.com/Desno365/MCPE-scripts/master/portalMOD-version");
                latestLaserVersion = DesnoUtils.getTextFromUrl("https://raw.githubusercontent.com/Desno365/MCPE-scripts/master/laserMOD-version");
                latestTurretsVersion = DesnoUtils.getTextFromUrl("https://raw.githubusercontent.com/Desno365/MCPE-scripts/master/turretsMOD-version");
                latestJukeboxVersion = DesnoUtils.getTextFromUrl("https://raw.githubusercontent.com/Desno365/MCPE-scripts/master/jukeboxMOD-version");

                newsString = DesnoUtils.getTextFromUrl("https://raw.githubusercontent.com/Desno365/Desno365.github.io/master/minecraft/desno365-mods-app/app-news.html");
                portalModChangelog = DesnoUtils.getTextFromUrl("https://raw.githubusercontent.com/Desno365/Desno365.github.io/master/minecraft/portal2-mod/full-changelog/changelog.html");
                laserModChangelog = DesnoUtils.getTextFromUrl("https://raw.githubusercontent.com/Desno365/Desno365.github.io/master/minecraft/laser-mod/full-changelog/changelog.html");
                turretsModChangelog = DesnoUtils.getTextFromUrl("https://raw.githubusercontent.com/Desno365/Desno365.github.io/master/minecraft/turrets-mod/full-changelog/changelog.html");
                jukeboxModChangelog = DesnoUtils.getTextFromUrl("https://raw.githubusercontent.com/Desno365/Desno365.github.io/master/minecraft/jukebox-mod/full-changelog/changelog.html");
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

            DesnoUtils.readWriteVersionsAndNotify(getApplicationContext(), latestPortalVersion, latestLaserVersion, latestTurretsVersion, latestJukeboxVersion);

            portalModVersion = getResources().getString(R.string.latest_version_is) + " " + latestPortalVersion;
            laserModVersion = getResources().getString(R.string.latest_version_is) + " " + latestLaserVersion;
            turretsModVersion = getResources().getString(R.string.latest_version_is) + " " + latestTurretsVersion;
            jukeboxModVersion = getResources().getString(R.string.latest_version_is) + " " + latestJukeboxVersion;
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
                    return new FragmentTab2();
                case 2:
                    return new FragmentTab3();
                case 3:
                    return new FragmentTab4();
                case 4:
                    return new FragmentTab5();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return myMainActivity.get().getString(R.string.home_title);
                case 1:
                    return myMainActivity.get().getString(R.string.mod1_title);
                case 2:
                    return myMainActivity.get().getString(R.string.mod2_title);
                case 3:
                    return myMainActivity.get().getString(R.string.mod3_title);
                case 4:
                    return myMainActivity.get().getString(R.string.mod4_title);
                default:
                    return "Missing title";
            }
        }
    }

}
