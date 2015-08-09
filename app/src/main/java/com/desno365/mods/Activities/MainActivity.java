/*
 * Copyright 2015 Dennis Motta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.desno365.mods.Activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.desno365.mods.AnalyticsApplication;
import com.desno365.mods.DesnoUtils;
import com.desno365.mods.MainNavigationDrawerFragment;
import com.desno365.mods.MainSwipeRefreshLayout;
import com.desno365.mods.Mods.DesnoGuns;
import com.desno365.mods.Mods.Jukebox;
import com.desno365.mods.Mods.Laser;
import com.desno365.mods.Mods.Mod;
import com.desno365.mods.Mods.Portal;
import com.desno365.mods.Mods.Turrets;
import com.desno365.mods.Mods.Unreal;
import com.desno365.mods.R;
import com.desno365.mods.Receivers.AlarmReceiver;
import com.desno365.mods.SharedConstants.Keys;
import com.desno365.mods.Tabs.FragmentTab1;
import com.google.android.gms.analytics.Tracker;

import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity implements MainNavigationDrawerFragment.NavigationDrawerCallbacks {

	public static final String TAG = "MainActivity";

	public static AppCompatActivity activity;

	public static String newsString;

	public static Mod MOD_GUNS;
	public static Mod MOD_PORTAL;
	public static Mod MOD_LASER;
	public static Mod MOD_TURRETS;
	public static Mod MOD_JUKEBOX;
	public static Mod MAP_UNREAL;
	
	// UI elements
	public static Toolbar toolbar;
	public static ViewPager mViewPager;
	private MainNavigationDrawerFragment mNavigationDrawerFragment = new MainNavigationDrawerFragment();
	private MainSwipeRefreshLayout swipeLayout;
	private AppSectionsPagerAdapter mAppSectionsPagerAdapter;

	// ads after the click of a button
	private boolean displayAdAtResume = false;

	// analytics tracker
	private Tracker mTracker;


	@SuppressLint("CommitPrefEdits")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "APP LAUNCHED!");
		DesnoUtils.setSavedTheme(this);
		DesnoUtils.setSavedLanguage(this);
		super.onCreate(savedInstanceState);

		activity = this;

		newsString = getResources().getString(R.string.loading);

		MOD_GUNS = new DesnoGuns(this);
		MOD_PORTAL = new Portal(this);
		MOD_LASER = new Laser(this);
		MOD_TURRETS = new Turrets(this);
		MOD_JUKEBOX = new Jukebox(this);
		MAP_UNREAL = new Unreal(this);

		// set content of the activity
		setContentView(R.layout.activity_main);



		// Starting Google Analytics
		AnalyticsApplication application = (AnalyticsApplication) getApplication();
		mTracker = application.getDefaultTracker();

		// Send screen change
		DesnoUtils.sendScreenChange(mTracker, "MainActivity");

		// Set up the ToolBar.
		toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
		setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with setSupportActionBar() call

		// Set up the SwipeRefreshLayout
		swipeLayout = (MainSwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(new MainSwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				startRefreshingAndChecking();
			}
		});
		TypedArray a = getTheme().obtainStyledAttributes(new int[]{R.attr.color_primary, R.attr.color_accent});
		int color1 = a.getColor(0, 0);
		int color2 = a.getColor(1, 0);
		a.recycle();
		swipeLayout.setColorSchemeColors(color1, color2);

		// Create the adapter that will return a fragment for each section of the app.
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager and attaching the adapter
		mViewPager = (ViewPager) findViewById(R.id.fragment_container);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		mViewPager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				swipeLayout.setEnabled(false);
				switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						swipeLayout.setEnabled(true);
						break;
				}
				return false;
			}
		});

		// Bind the tabs to the ViewPager
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setViewPager(mViewPager);
		tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When swiping between different app sections

				// change toolbar title
				if (position == 0)
					toolbar.setTitle(getResources().getString(R.string.app_name));
				else
					toolbar.setTitle(mAppSectionsPagerAdapter.getPageTitle(position));

				// close drawer
				if (mNavigationDrawerFragment.isDrawerOpen())
					MainNavigationDrawerFragment.mDrawerLayout.closeDrawer(findViewById(R.id.navigation_drawer));

				// analytics
				DesnoUtils.sendScreenChange(mTracker, mAppSectionsPagerAdapter.getPageTitle(position).toString());
			}
		});

		// Set up the drawer.
		mNavigationDrawerFragment = (MainNavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

		// actions to do at the first launch of the app
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (sharedPrefs.getBoolean("is_first_launch", true)) {
			// first launch, the app has never been launched before
			SharedPreferences.Editor editor = sharedPrefs.edit();
			editor.putBoolean("refresh_on_start", true);
			editor.commit();
			editor.putBoolean("is_first_launch", false);
			editor.putBoolean("notification_bool_mods", true);
			editor.putBoolean("notification_bool_news", true);
			editor.putString("sync_frequency", "4");
			editor.putString("selected_language", "not_changed");
			editor.putString("selected_theme", "0");
			editor.putString("selected_animations", "0");
			editor.putBoolean("anonymous_statistics", true);
			editor.putBoolean("user_understood_full_resolution_help", false);
			editor.apply();
			Log.i(TAG, "First launch");
		}

		// Load alarmManager for notifications
		AlarmReceiver aR = new AlarmReceiver();
		aR.cancelAlarm(getApplicationContext());
		aR.setAlarm(getApplicationContext());

		// Load the IntersitialAd with the InterstitialAdStatic custom class
		// Now we don't need to load the ad again, here or in other activities
		new DesnoUtils.InterstitialAdStatic(this);
	}

	@Override
	public boolean onPrepareOptionsPanel(View view, Menu menu) {
		//add icons near menu items
		if (menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (NoSuchMethodException e) {
					Log.e(TAG, "onMenuOpened", e);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return super.onPrepareOptionsPanel(view, menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main_activity, menu);

		//refresh content on start
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (sharedPrefs.getBoolean("refresh_on_start", true)) {
			new android.os.Handler().postDelayed(new Runnable() {
				public void run() {
					runOnUiThread(new Runnable() {
						public void run() {
							try {
								startRefreshingAndChecking();
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
				if (mNavigationDrawerFragment.isDrawerOpen())
					MainNavigationDrawerFragment.mDrawerLayout.closeDrawer(findViewById(R.id.navigation_drawer));
				else
					MainNavigationDrawerFragment.mDrawerLayout.openDrawer(findViewById(R.id.navigation_drawer));
				return true;

			case R.id.action_info:
				startActivity(new Intent(this, AboutActivity.class));
				return true;

			case R.id.action_help:
				startActivity(new Intent(this, HelpActivity.class));
				return true;

			case R.id.action_news:
				startActivity(new Intent(this, NewsActivity.class));
				return true;

			case R.id.action_share:
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_body));
				startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
				DesnoUtils.sendAction(mTracker, "Share");
				return true;

			case R.id.action_feedback:
				Intent intent = new Intent(Intent.ACTION_SENDTO);
				intent.setData(Uri.parse("mailto:")); // only email apps should handle this
				intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ "desno365@gmail.com" });
				intent.putExtra(Intent.EXTRA_SUBJECT, "Desno365's Mods feedback");
				if (intent.resolveActivity(getPackageManager()) != null) {
					startActivity(intent);
				}
				DesnoUtils.sendAction(mTracker, "Feedback");
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
				DesnoUtils.sendAction(mTracker, "Rate-app");
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
	public void onBackPressed() {
		if (mNavigationDrawerFragment.isDrawerOpen())
			MainNavigationDrawerFragment.mDrawerLayout.closeDrawer(findViewById(R.id.navigation_drawer));
		else
			super.onBackPressed();
	}

	@Override
	public void onResume() {
		super.onResume();

		// ads
		if (displayAdAtResume) {
			DesnoUtils.showAd();
			displayAdAtResume = false;
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(int groupPosition, int childPosition) {
		if (groupPosition == 1) {
			if (mAppSectionsPagerAdapter != null) {
				mViewPager.setCurrentItem(childPosition + 1); // + 1 because the home Fragment of the ViewPager isn't in the mods group
			} else {
				// update the main content by replacing fragments
				Fragment myFragment;
				switch (childPosition + 1) {
					case DesnoGuns.viewPagerPosition:
						myFragment = DesnoGuns.getFragmentTab();
						break;
					case Portal.viewPagerPosition:
						myFragment = Portal.getFragmentTab();
						break;
					case Laser.viewPagerPosition:
						myFragment = Laser.getFragmentTab();
						break;
					case Turrets.viewPagerPosition:
						myFragment = Turrets.getFragmentTab();
						break;
					case Jukebox.viewPagerPosition:
						myFragment = Jukebox.getFragmentTab();
						break;
					case Unreal.viewPagerPosition:
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
		}
	}

	@Override
	public void onNavigationDrawerGroupSelected(int position) {
		switch (position) {
			case 0:
				if (mAppSectionsPagerAdapter != null) {
					mViewPager.setCurrentItem(position);
				} else {
					FragmentManager fragmentManager = getSupportFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.fragment_container, new FragmentTab1())
							.commit();
				}
				break;
			case 1:
				// already caught by the NavigationDrawer class because it's a container and not a clickable group
				break;
			case 2:
				new android.os.Handler().postDelayed(new Runnable() {
					public void run() {
						startActivity(new Intent(getApplicationContext(), AboutActivity.class));
					}
				}, 200);
				break;
			case 3:
				new android.os.Handler().postDelayed(new Runnable() {
					public void run() {
						startActivity(new Intent(getApplicationContext(), HelpActivity.class));
					}
				}, 200);
				break;
			case 4:
				new android.os.Handler().postDelayed(new Runnable() {
					public void run() {
						startActivity(new Intent(getApplicationContext(), NewsActivity.class));
					}
				}, 200);
				break;
			case 5:
				new android.os.Handler().postDelayed(new Runnable() {
					public void run() {
						startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
					}
				}, 200);
				break;
		}
	}

	@Override
	public void startActivity(Intent intent) {
		try {
			super.startActivity(intent);
			DesnoUtils.changeStartAnimations(activity);
		} catch (ActivityNotFoundException e1) {
			Log.e(TAG, "Start activity failed for the first time.", e1);

			try {
				super.startActivity(intent);
				DesnoUtils.changeStartAnimations(activity);
			} catch (ActivityNotFoundException e2) {
				Log.e(TAG, "Start activity failed for the second and last time.", e2);
				Toast.makeText(activity.getApplicationContext(), "Error: can't start the Activity, please try again and make sure you have a Internet browser installed.", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void refreshTextViews() {
		activity.runOnUiThread(new Runnable() {
			public void run() {

				try {
					TextView textUpdatesGuns = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_guns_is);
					textUpdatesGuns.setText(MOD_GUNS.version);

					TextView textCompatibilityGuns = (TextView) getWindow().getDecorView().findViewById(R.id.guns_compatibility);
					textCompatibilityGuns.setText(MOD_GUNS.compatibility);

					TextView textChangelogGuns = (TextView) getWindow().getDecorView().findViewById(R.id.guns_changelog);
					textChangelogGuns.setText(android.text.Html.fromHtml(MOD_GUNS.changelog));
				} catch (NullPointerException e) {
					Log.e(TAG, "NullPointerException in refreshTextViews for DesnoGuns");
				}

				try {
					TextView textUpdatesPortal = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_portal_is);
					textUpdatesPortal.setText(MOD_PORTAL.version);

					TextView textCompatibilityPortal = (TextView) getWindow().getDecorView().findViewById(R.id.portal_compatibility);
					textCompatibilityPortal.setText(android.text.Html.fromHtml(MOD_PORTAL.compatibility));

					TextView textChangelogPortal = (TextView) getWindow().getDecorView().findViewById(R.id.portal_changelog);
					textChangelogPortal.setText(android.text.Html.fromHtml(MOD_PORTAL.changelog));
				} catch (NullPointerException e) {
					Log.e(TAG, "NullPointerException in refreshTextViews for Portal");
				}

				try {
					TextView textUpdatesLaser = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_laser_is);
					textUpdatesLaser.setText(MOD_LASER.version);

					TextView textCompatibilityLaser = (TextView) getWindow().getDecorView().findViewById(R.id.laser_compatibility);
					textCompatibilityLaser.setText(android.text.Html.fromHtml(MOD_LASER.compatibility));

					TextView textChangelogLaser = (TextView) getWindow().getDecorView().findViewById(R.id.laser_changelog);
					textChangelogLaser.setText(android.text.Html.fromHtml(MOD_LASER.changelog));
				} catch (NullPointerException e) {
					Log.e(TAG, "NullPointerException in refreshTextViews for Laser");
				}

				try {
					TextView textUpdatesTurrets = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_turrets_is);
					textUpdatesTurrets.setText(MOD_TURRETS.version);

					TextView textCompatibilityTurrets = (TextView) getWindow().getDecorView().findViewById(R.id.turrets_compatibility);
					textCompatibilityTurrets.setText(android.text.Html.fromHtml(MOD_TURRETS.compatibility));

					TextView textChangelogTurrets = (TextView) getWindow().getDecorView().findViewById(R.id.turrets_changelog);
					textChangelogTurrets.setText(android.text.Html.fromHtml(MOD_TURRETS.changelog));
				} catch (NullPointerException e) {
					Log.e(TAG, "NullPointerException in refreshTextViews for Turrets");
				}

				try {
					TextView textUpdatesJukebox = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_jukebox_is);
					textUpdatesJukebox.setText(MOD_JUKEBOX.version);

					TextView textCompatibilityJukebox = (TextView) getWindow().getDecorView().findViewById(R.id.jukebox_compatibility);
					textCompatibilityJukebox.setText(android.text.Html.fromHtml(MOD_JUKEBOX.compatibility));

					TextView textChangelogJukebox = (TextView) getWindow().getDecorView().findViewById(R.id.jukebox_changelog);
					textChangelogJukebox.setText(android.text.Html.fromHtml(MOD_JUKEBOX.changelog));
				} catch (NullPointerException e) {
					Log.e(TAG, "NullPointerException in refreshTextViews for Jukebox");
				}

				try {
					TextView textUpdatesUnreal = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_unreal_is);
					textUpdatesUnreal.setText(MAP_UNREAL.version);

					TextView textCompatibilityUnreal = (TextView) getWindow().getDecorView().findViewById(R.id.unreal_compatibility);
					textCompatibilityUnreal.setText(android.text.Html.fromHtml(MAP_UNREAL.compatibility));

					TextView textChangelogUnreal = (TextView) getWindow().getDecorView().findViewById(R.id.unreal_changelog);
					textChangelogUnreal.setText(android.text.Html.fromHtml(MAP_UNREAL.changelog));
				} catch (NullPointerException e) {
					Log.e(TAG, "NullPointerException in refreshTextViews for Unreal");
				}
			}
		});
	}

	public void onViewClick(View v) {
		switch (v.getId()) {
			// minecraftforum.net thread buttons
			case R.id.minecraft_thread_guns_button:
				startActivity(MOD_GUNS.getVisitThreadIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Minecraft-Thread-Guns");
				break;
			case R.id.minecraft_thread_portal_button:
				startActivity(MOD_PORTAL.getVisitThreadIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Minecraft-Thread-Portal");
				break;
			case R.id.minecraft_thread_laser_button:
				startActivity(MOD_LASER.getVisitThreadIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Minecraft-Thread-Laser");
				break;
			case R.id.minecraft_thread_turrets_button:
				startActivity(MOD_TURRETS.getVisitThreadIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Minecraft-Thread-Turrets");
				break;
			case R.id.minecraft_thread_jukebox_button:
				startActivity(MOD_JUKEBOX.getVisitThreadIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Minecraft-Thread-Jukebox");
				break;
			case R.id.minecraft_thread_unreal_button:
				startActivity(MAP_UNREAL.getVisitThreadIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Minecraft-Thread-Unreal");
				break;

			// download from website buttons
			case R.id.download_guns_button:
				startActivity(MOD_GUNS.getDownloadFromWebsiteIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Download-Guns");
				break;
			case R.id.download_portal_button:
				startActivity(MOD_PORTAL.getDownloadFromWebsiteIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Download-Portal");
				break;
			case R.id.download_laser_button:
				startActivity(MOD_LASER.getDownloadFromWebsiteIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Download-Laser");
				break;
			case R.id.download_turrets_button:
				startActivity(MOD_TURRETS.getDownloadFromWebsiteIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Download-Turrets");
				break;
			case R.id.download_jukebox_button:
				startActivity(MOD_JUKEBOX.getDownloadFromWebsiteIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Download-Jukebox");
				break;
			case R.id.download_unreal_button:
				startActivity(MAP_UNREAL.getDownloadFromWebsiteIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Download-Unreal");
				break;

			// installation video tutorial button
			case R.id.installation_video_guns_button:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_DESNOGUNS_VIDEO_TUTORIAL)));
				DesnoUtils.sendAction(mTracker, "Video-Guns");
				break;

			// start news activity
			case R.id.button_news:
				startActivity(new Intent(this, NewsActivity.class));
				break;

			// start help activity
			case R.id.button_help:
				startActivity(new Intent(this, HelpActivity.class));
				break;
		}
	}

	// test alarm onClick
	/**
	 * public void testAlarm(View v) {
	 * AlarmReceiver aR = new AlarmReceiver();
	 * aR.onReceive(getApplicationContext(), null);
	 * }
	 */

	public void setRefreshState(final boolean refreshing) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (swipeLayout != null) {
					swipeLayout.setRefreshing(refreshing);
				}
			}
		});
	}

	private void startRefreshingAndChecking() {
		RetrieveNewsAndModsUpdates downloadTask = new RetrieveNewsAndModsUpdates();
		downloadTask.execute((Void) null);
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
				case DesnoGuns.viewPagerPosition:
					return DesnoGuns.getFragmentTab();
				case Portal.viewPagerPosition:
					return Portal.getFragmentTab();
				case Laser.viewPagerPosition:
					return Laser.getFragmentTab();
				case Turrets.viewPagerPosition:
					return Turrets.getFragmentTab();
				case Jukebox.viewPagerPosition:
					return Jukebox.getFragmentTab();
				case Unreal.viewPagerPosition:
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
					return activity.getString(R.string.home_title);
				case 1:
					return MOD_GUNS.getName(activity);
				case 2:
					return MOD_PORTAL.getName(activity);
				case 3:
					return MOD_LASER.getName(activity);
				case 4:
					return MOD_TURRETS.getName(activity);
				case 5:
					return MOD_JUKEBOX.getName(activity);
				case 6:
					return MAP_UNREAL.getName(activity);
				default:
					return "Missing title";
			}
		}
	}

	public class RetrieveNewsAndModsUpdates extends AsyncTask<Void, String, Void> {

		private String latestNewsVersion = "";
		private String latestGunsVersion = "";
		private String latestPortalVersion = "";
		private String latestLaserVersion = "";
		private String latestTurretsVersion = "";
		private String latestJukeboxVersion = "";
		private String latestUnrealVersion = "";

		@Override
		protected Void doInBackground(Void... params) {

			setRefreshState(true);

			if (DesnoUtils.isNetworkAvailable(getApplicationContext())) {
				latestNewsVersion = DesnoUtils.getTextFromUrl(Keys.KEY_NEWS_COUNT);
				latestGunsVersion = DesnoUtils.getTextFromUrl(Keys.KEY_DESNOGUNS_VERSION);
				latestPortalVersion = DesnoUtils.getTextFromUrl(Keys.KEY_PORTAL_VERSION);
				latestLaserVersion = DesnoUtils.getTextFromUrl(Keys.KEY_LASER_VERSION);
				latestTurretsVersion = DesnoUtils.getTextFromUrl(Keys.KEY_TURRETS_VERSION);
				latestJukeboxVersion = DesnoUtils.getTextFromUrl(Keys.KEY_JUKEBOX_VERSION);
				latestUnrealVersion = DesnoUtils.getTextFromUrl(Keys.KEY_UNREAL_VERSION);

				MOD_GUNS.compatibility = getResources().getString(R.string.mod_compatibility_content) + " " + DesnoUtils.getTextFromUrl(Keys.KEY_DESNOGUNS_COMPATIBILITY);
				MOD_PORTAL.compatibility = getResources().getString(R.string.mod_compatibility_content) + " " + DesnoUtils.getTextFromUrl(Keys.KEY_PORTAL_COMPATIBILITY);
				MOD_LASER.compatibility = getResources().getString(R.string.mod_compatibility_content) + " " + DesnoUtils.getTextFromUrl(Keys.KEY_LASER_COMPATIBILITY);
				MOD_TURRETS.compatibility = getResources().getString(R.string.mod_compatibility_content) + " " + DesnoUtils.getTextFromUrl(Keys.KEY_TURRETS_COMPATIBILITY);
				MOD_JUKEBOX.compatibility = getResources().getString(R.string.mod_compatibility_content) + " " + DesnoUtils.getTextFromUrl(Keys.KEY_JUKEBOX_COMPATIBILITY);
				MAP_UNREAL.compatibility = getResources().getString(R.string.mod_compatibility_content) + " " + DesnoUtils.getTextFromUrl(Keys.KEY_UNREAL_COMPATIBILITY);

				MOD_GUNS.changelog = DesnoUtils.getTextFromUrl(Keys.KEY_DESNOGUNS_CHANGELOG);
				MOD_PORTAL.changelog = DesnoUtils.getTextFromUrl(Keys.KEY_PORTAL_CHANGELOG);
				MOD_LASER.changelog = DesnoUtils.getTextFromUrl(Keys.KEY_LASER_CHANGELOG);
				MOD_TURRETS.changelog = DesnoUtils.getTextFromUrl(Keys.KEY_TURRETS_CHANGELOG);
				MOD_JUKEBOX.changelog = DesnoUtils.getTextFromUrl(Keys.KEY_JUKEBOX_CHANGELOG);
				MAP_UNREAL.changelog = DesnoUtils.getTextFromUrl(Keys.KEY_UNREAL_CHANGELOG);
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						DesnoUtils.showDefaultSnackbar(activity.findViewById(R.id.coordinator_main), R.string.internet_error, 2000);
					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			Log.i(TAG, "onPostExecute now, the AsyncTask finished");

			DesnoUtils.notifyForUnreadNews(getApplicationContext(), latestNewsVersion);
			DesnoUtils.notifyForNewUpdates(getApplicationContext(), latestGunsVersion, latestPortalVersion, latestLaserVersion, latestTurretsVersion, latestJukeboxVersion, latestUnrealVersion);

			MOD_GUNS.version = getResources().getString(R.string.latest_version_is) + " " + latestGunsVersion;
			MOD_PORTAL.version = getResources().getString(R.string.latest_version_is) + " " + latestPortalVersion;
			MOD_LASER.version = getResources().getString(R.string.latest_version_is) + " " + latestLaserVersion;
			MOD_TURRETS.version = getResources().getString(R.string.latest_version_is) + " " + latestTurretsVersion;
			MOD_JUKEBOX.version = getResources().getString(R.string.latest_version_is) + " " + latestJukeboxVersion;
			MAP_UNREAL.version = getResources().getString(R.string.latest_version_is) + " " + latestUnrealVersion;
			refreshTextViews();
			setRefreshState(false);
		}

	}

}
