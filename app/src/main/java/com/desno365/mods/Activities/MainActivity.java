/*
 *
 * Copyright 2017 Dennis Motta
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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
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
import com.desno365.mods.Mods.ModsContainer;
import com.desno365.mods.Mods.Portal;
import com.desno365.mods.Mods.Unreal;
import com.desno365.mods.R;
import com.desno365.mods.Receivers.AlarmReceiver;
import com.desno365.mods.SharedConstants.DefaultSettingsValues;
import com.desno365.mods.SharedConstants.Keys;
import com.desno365.mods.SharedConstants.SharedConstants;
import com.desno365.mods.SharedVariables.SharedVariables;
import com.desno365.mods.Tabs.FragmentTab1;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.lang.reflect.Method;


public class MainActivity extends BaseActivity implements MainNavigationDrawerFragment.NavigationDrawerCallbacks {

	public static final String TAG = "MainActivity";

	private AppCompatActivity activity;

	public static String newsString;

	public static ModsContainer modsContainer;
	
	// UI elements
	private Toolbar toolbar;
	private ViewPager mViewPager;
	private MainNavigationDrawerFragment mNavigationDrawerFragment = new MainNavigationDrawerFragment();
	private MainSwipeRefreshLayout swipeLayout;
	private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	public static int currentPageViewPager = 0; // used in MainSwipeRefreshLayout in canChildScrollUp

	// ads after the click of a button
	private boolean displayAdAtResume = false;

	// banner ad
	private AdView mAdView;

	// analytics tracker
	private Tracker mTracker;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "APP LAUNCHED!");
		super.onCreate(savedInstanceState);

		activity = this;

		newsString = getResources().getString(R.string.loading);

		modsContainer = new ModsContainer(this);

		// set content of the activity
		setContentView(R.layout.activity_main);



		// are statistics (crashlytics and analytics) enabled?
		DesnoUtils.updateStatisticsEnabledBool(this);

		// load Firebase analytics
		if(SharedVariables.areStatisticsEnabled)
			FirebaseAnalytics.getInstance(this);

		// start Google Analytics
		AnalyticsApplication application = (AnalyticsApplication) getApplication();
		mTracker = application.getDefaultTracker();

		// load google ads
		MobileAds.initialize(getApplicationContext(), "ca-app-pub-4328789168608769~3753956938");

		// Load the IntersitialAd, then we just need to call DesnoUtils.showAd() to show the loaded Interstitial
		// Now we don't need to load the ad again, here or in other activities
		DesnoUtils.loadInterstitialAd(this);

		// Load the banner ad
		mAdView = (AdView) findViewById(R.id.ad_view);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);



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
		mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When swiping between different app sections

				// save position in a static variable
				currentPageViewPager = position;

				// change toolbar title and analytics
				if (position == 0) {
					toolbar.setTitle(getResources().getString(R.string.app_name));
					DesnoUtils.sendScreenChange(mTracker, "Home");
				} else {
					toolbar.setTitle(mAppSectionsPagerAdapter.getPageTitle(position));
					DesnoUtils.sendScreenChange(mTracker, mAppSectionsPagerAdapter.getPageTitle(position).toString());
				}

				// close drawer
				if (mNavigationDrawerFragment.isDrawerOpen())
					mNavigationDrawerFragment.mDrawerLayout.closeDrawer(findViewById(R.id.navigation_drawer));
			}
		});

		// Bind the tabs to the ViewPager
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setViewPager(mViewPager);

		// Set up the drawer.
		mNavigationDrawerFragment = (MainNavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

		// actions to do at the first launch of the app or after the app gets an update
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPrefs.edit();

		final int knownVersion = sharedPrefs.getInt("known_version_code", 0);
		int currentVersion;
		try {
			currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "This should never happen, if this really happened one of the following has most likely happened: 1) There is a significant bug in this code. 2) There is a significant bug in Android. 3) The universe has ceased to exist.");
			currentVersion = 0;
		}

		if (sharedPrefs.getBoolean("is_first_launch", true)) {
			// first launch, the app has never been launched before
			editor.putBoolean("is_first_launch", false);
			editor.putInt("known_version_code", currentVersion);

			editor.putBoolean("notification_bool_mods", DefaultSettingsValues.NOTIFICATIONS_MODS);
			editor.putBoolean("notification_bool_news", DefaultSettingsValues.NOTIFICATIONS_NEWS);
			editor.putString("sync_frequency", DefaultSettingsValues.SYNC_FREQUENCY_STRING);
			editor.putString("selected_language", "not_changed");
			editor.putString("selected_theme", "0");
			editor.putString("selected_animations", "0");
			editor.putBoolean("anonymous_statistics", DefaultSettingsValues.ANONYMOUS_STATISTICS);
			editor.putBoolean("user_understood_full_resolution_help", false);
			editor.putBoolean("user_understood_sliding_pages_help", false);
			Log.i(TAG, "First launch");
		} else {
			Log.i(TAG, "APP LAUNCHED, with versionCode " + currentVersion);
			editor.putInt("known_version_code", currentVersion);

			if(knownVersion < currentVersion) {
				Log.i(TAG, "New version of the app: current versionCode " + currentVersion + ", old versionCode: " + knownVersion);

				if(knownVersion < 15) {
					editor.putBoolean("anonymous_statistics", DefaultSettingsValues.ANONYMOUS_STATISTICS);
				}
			}
		}
		editor.apply();

		// Load alarmManager for notifications
		AlarmReceiver aR = new AlarmReceiver();
		aR.cancelAlarm(getApplicationContext());
		aR.setAlarm(getApplicationContext());
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
		if(SharedConstants.DEBUG_REFRESH_CONTENT_ON_START) {
			new android.os.Handler().postDelayed(new Runnable() {
				public void run() {
					runOnUiThread(new Runnable() {
						public void run() {
							startRefreshingAndChecking();
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
					mNavigationDrawerFragment.mDrawerLayout.closeDrawer(findViewById(R.id.navigation_drawer));
				else
					mNavigationDrawerFragment.mDrawerLayout.openDrawer(findViewById(R.id.navigation_drawer));
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
				Toast.makeText(this, getResources().getString(R.string.feedback_toast), Toast.LENGTH_LONG).show();
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
		if (mNavigationDrawerFragment.isDrawerOpen()) {
			mNavigationDrawerFragment.mDrawerLayout.closeDrawer(findViewById(R.id.navigation_drawer));
		} else {
			// not caling super.onBackPressed(): no custom animations at the end of the app
			this.finish();
		}
	}

	@Override
	public void onPause() {
		if (mAdView != null) {
			mAdView.pause();
		}
		super.onPause();
	}


	@Override
	public void onResume() {
		super.onResume();
		// ads
		if (displayAdAtResume) {
			DesnoUtils.showAd();
			displayAdAtResume = false;
		}
		if (mAdView != null) {
			mAdView.resume();
		}

		// send screen change
		DesnoUtils.sendScreenChange(mTracker, TAG);
	}

	@Override
	public void onDestroy() {
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
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

	private void refreshTextViews() {
		activity.runOnUiThread(new Runnable() {
			public void run() {

				try {
					TextView textUpdatesGuns = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_guns_is);
					textUpdatesGuns.setText(modsContainer.desnoGuns.getVersion());

					TextView textCompatibilityGuns = (TextView) getWindow().getDecorView().findViewById(R.id.guns_compatibility);
					textCompatibilityGuns.setText(modsContainer.desnoGuns.getCompatibility());

					TextView textChangelogGuns = (TextView) getWindow().getDecorView().findViewById(R.id.guns_changelog);
					textChangelogGuns.setText(DesnoUtils.fromHtml(modsContainer.desnoGuns.getChangelog()));
				} catch (NullPointerException e) {
					Log.e(TAG, "NullPointerException in refreshTextViews for DesnoGuns");
				}

				try {
					TextView textUpdatesPortal = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_portal_is);
					textUpdatesPortal.setText(modsContainer.portal.getVersion());

					TextView textCompatibilityPortal = (TextView) getWindow().getDecorView().findViewById(R.id.portal_compatibility);
					textCompatibilityPortal.setText(DesnoUtils.fromHtml(modsContainer.portal.getCompatibility()));

					TextView textChangelogPortal = (TextView) getWindow().getDecorView().findViewById(R.id.portal_changelog);
					textChangelogPortal.setText(DesnoUtils.fromHtml(modsContainer.portal.getChangelog()));
				} catch (NullPointerException e) {
					Log.e(TAG, "NullPointerException in refreshTextViews for Portal");
				}

				try {
					TextView textUpdatesLaser = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_laser_is);
					textUpdatesLaser.setText(modsContainer.laser.getVersion());

					TextView textCompatibilityLaser = (TextView) getWindow().getDecorView().findViewById(R.id.laser_compatibility);
					textCompatibilityLaser.setText(DesnoUtils.fromHtml(modsContainer.laser.getCompatibility()));

					TextView textChangelogLaser = (TextView) getWindow().getDecorView().findViewById(R.id.laser_changelog);
					textChangelogLaser.setText(DesnoUtils.fromHtml(modsContainer.laser.getChangelog()));
				} catch (NullPointerException e) {
					Log.e(TAG, "NullPointerException in refreshTextViews for Laser");
				}

				try {
					TextView textUpdatesJukebox = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_jukebox_is);
					textUpdatesJukebox.setText(modsContainer.jukebox.getVersion());

					TextView textCompatibilityJukebox = (TextView) getWindow().getDecorView().findViewById(R.id.jukebox_compatibility);
					textCompatibilityJukebox.setText(DesnoUtils.fromHtml(modsContainer.jukebox.getCompatibility()));

					TextView textChangelogJukebox = (TextView) getWindow().getDecorView().findViewById(R.id.jukebox_changelog);
					textChangelogJukebox.setText(DesnoUtils.fromHtml(modsContainer.jukebox.getChangelog()));
				} catch (NullPointerException e) {
					Log.e(TAG, "NullPointerException in refreshTextViews for Jukebox");
				}

				try {
					TextView textUpdatesUnreal = (TextView) getWindow().getDecorView().findViewById(R.id.latest_version_unreal_is);
					textUpdatesUnreal.setText(modsContainer.unreal.getVersion());

					TextView textCompatibilityUnreal = (TextView) getWindow().getDecorView().findViewById(R.id.unreal_compatibility);
					textCompatibilityUnreal.setText(DesnoUtils.fromHtml(modsContainer.unreal.getCompatibility()));

					TextView textChangelogUnreal = (TextView) getWindow().getDecorView().findViewById(R.id.unreal_changelog);
					textChangelogUnreal.setText(DesnoUtils.fromHtml(modsContainer.unreal.getChangelog()));
				} catch (NullPointerException e) {
					Log.e(TAG, "NullPointerException in refreshTextViews for Unreal");
				}
			}
		});
	}

	public void onViewClick(View v) {
		switch (v.getId()) {
			// download from website buttons
			case R.id.download_guns_button:
				startActivity(modsContainer.desnoGuns.getDownloadFromWebsiteIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Download-Guns");
				break;
			case R.id.download_portal_button:
				startActivity(modsContainer.portal.getDownloadFromWebsiteIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Download-Portal");
				break;
			case R.id.download_laser_button:
				startActivity(modsContainer.laser.getDownloadFromWebsiteIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Download-Laser");
				break;
			case R.id.download_jukebox_button:
				startActivity(modsContainer.jukebox.getDownloadFromWebsiteIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Download-Jukebox");
				break;
			case R.id.download_unreal_button:
				startActivity(modsContainer.unreal.getDownloadFromWebsiteIntent());
				displayAdAtResume = true;
				DesnoUtils.sendAction(mTracker, "Download-Unreal");
				break;

			// installation video tutorial button
			case R.id.installation_video_guns_button:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_DESNOGUNS_VIDEO_TUTORIAL)));
				DesnoUtils.sendAction(mTracker, "Video-Guns");
				break;
			case R.id.installation_video_portal_button:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PORTAL_VIDEO_TUTORIAL)));
				DesnoUtils.sendAction(mTracker, "Video-Portal");
				break;

			// start news activity
			case R.id.button_news:
				startActivity(new Intent(this, NewsActivity.class));
				break;

			// start help activity
			case R.id.button_help:
				startActivity(new Intent(this, HelpActivity.class));
				break;

			case R.id.button_help_unreal_geometry:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_UNREAL_INSTRUCTIONS)));
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
	public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

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
			return 6;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return activity.getString(R.string.home_title);
				case 1:
					return modsContainer.desnoGuns.getName();
				case 2:
					return modsContainer.portal.getName();
				case 3:
					return modsContainer.laser.getName();
				case 4:
					return modsContainer.jukebox.getName();
				case 5:
					return modsContainer.unreal.getName();
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
				latestJukeboxVersion = DesnoUtils.getTextFromUrl(Keys.KEY_JUKEBOX_VERSION);
				latestUnrealVersion = DesnoUtils.getTextFromUrl(Keys.KEY_UNREAL_VERSION);

				modsContainer.desnoGuns.setCompatibility(getResources().getString(R.string.mod_compatibility_content) + " " + DesnoUtils.getTextFromUrl(Keys.KEY_DESNOGUNS_COMPATIBILITY));
				modsContainer.portal.setCompatibility(getResources().getString(R.string.mod_compatibility_content) + " " + DesnoUtils.getTextFromUrl(Keys.KEY_PORTAL_COMPATIBILITY));
				modsContainer.laser.setCompatibility(getResources().getString(R.string.mod_compatibility_content) + " " + DesnoUtils.getTextFromUrl(Keys.KEY_LASER_COMPATIBILITY));
				modsContainer.jukebox.setCompatibility(getResources().getString(R.string.mod_compatibility_content) + " " + DesnoUtils.getTextFromUrl(Keys.KEY_JUKEBOX_COMPATIBILITY));
				modsContainer.unreal.setCompatibility(getResources().getString(R.string.mod_compatibility_content) + " " + DesnoUtils.getTextFromUrl(Keys.KEY_UNREAL_COMPATIBILITY));

				modsContainer.desnoGuns.setChangelog(DesnoUtils.getTextFromUrl(Keys.KEY_DESNOGUNS_CHANGELOG));
				modsContainer.portal.setChangelog(DesnoUtils.getTextFromUrl(Keys.KEY_PORTAL_CHANGELOG));
				modsContainer.laser.setChangelog(DesnoUtils.getTextFromUrl(Keys.KEY_LASER_CHANGELOG));
				modsContainer.jukebox.setChangelog(DesnoUtils.getTextFromUrl(Keys.KEY_JUKEBOX_CHANGELOG));
				modsContainer.unreal.setChangelog(DesnoUtils.getTextFromUrl(Keys.KEY_UNREAL_CHANGELOG));
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						Snackbar snack = DesnoUtils.getDefaultSnackbar(activity.findViewById(R.id.coordinator_main), R.string.internet_error, Snackbar.LENGTH_LONG);
						snack.setAction(R.string.retry, new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								startRefreshingAndChecking();
							}
						});
						snack.show();
					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			Log.i(TAG, "onPostExecute now, the AsyncTask finished");

			DesnoUtils.notifyForUnreadNews(getApplicationContext(), latestNewsVersion);
			DesnoUtils.notifyForNewUpdates(getApplicationContext(), latestGunsVersion, latestPortalVersion, latestLaserVersion, latestJukeboxVersion, latestUnrealVersion);

			modsContainer.desnoGuns.setVersion(getResources().getString(R.string.latest_version_is) + " " + latestGunsVersion);
			modsContainer.portal.setVersion(getResources().getString(R.string.latest_version_is) + " " + latestPortalVersion);
			modsContainer.laser.setVersion(getResources().getString(R.string.latest_version_is) + " " + latestLaserVersion);
			modsContainer.jukebox.setVersion(getResources().getString(R.string.latest_version_is) + " " + latestJukeboxVersion);
			modsContainer.unreal.setVersion(getResources().getString(R.string.latest_version_is) + " " + latestUnrealVersion);
			refreshTextViews();
			setRefreshState(false);
		}

	}

}
