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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.desno365.mods.AnalyticsApplication;
import com.desno365.mods.DesnoUtils;
import com.desno365.mods.R;
import com.desno365.mods.SharedConstants.Keys;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.Tracker;

import it.sephiroth.android.library.tooltip.Tooltip;


public class HelpActivity extends BaseActivity {

	private static final String TAG = "HelpActivity";

	private AppCompatActivity activity;

	// analytics tracker
	private Tracker mTracker;

	// banner ad
	private AdView mAdView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Activity started (onCreate)");
		super.onCreate(savedInstanceState);

		activity = this;

		setContentView(R.layout.activity_help);



		// Start Google Analytics
		AnalyticsApplication application = (AnalyticsApplication) getApplication();
		mTracker = application.getDefaultTracker();

		// Send screen change
		DesnoUtils.sendScreenChange(mTracker, "HelpActivity");

		// Set up the action bar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_help); // Attaching the layout to the toolbar object
		toolbar.setTitle(R.string.action_help);
		toolbar.setNavigationIcon(R.drawable.ic_navigation_arrow_back);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.finish();
				DesnoUtils.changeFinishAnimations(activity);
			}
		});

		// Create the adapter that will return a fragment for each section of the app.
		AppSectionsPagerAdapter mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager and attaching the adapter
		ViewPager mViewPager = (ViewPager) findViewById(R.id.fragment_container_help);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);

		// Bind the tabs to the ViewPager
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs_help);
		tabs.setViewPager(mViewPager);
		tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When swiping between different app sections

				// tooltip on image
				if(position == 1) {
					new android.os.Handler().postDelayed(new Runnable() {
						public void run() {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (!PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("user_understood_full_resolution_help", false)) {
										DisplayMetrics metrics = new DisplayMetrics();
										getWindowManager().getDefaultDisplay().getMetrics(metrics);

										Tooltip.make(activity,
												new Tooltip.Builder(1) // 1 = id
														.anchor(findViewById(R.id.imageview_help_download_app), Tooltip.Gravity.TOP)
														.closePolicy(Tooltip.ClosePolicy.TOUCH_OUTSIDE_NO_CONSUME, 20000)
														.activateDelay(50)
														.showDelay(200)
														.text(getResources().getString(R.string.click_image_to_view))
														.maxWidth((metrics.widthPixels) / 10 * 9)
														.withArrow(true)
														.withOverlay(true)
														.withStyleId(R.style.ToolTipStyle)
														.build()
										).show();
									}
								}
							});
						}
					}, 200);
				}

				if(position == 1 || position == 2) {
					// after the first time switching to another page the user doesn't need the tooltip anymore
					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
					SharedPreferences.Editor editor = sharedPrefs.edit();
					editor.putBoolean("user_understood_sliding_pages_help", true);
					editor.apply();
				}
			}
		});

		new android.os.Handler().postDelayed(new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("user_understood_sliding_pages_help", false)) {
							DisplayMetrics metrics = new DisplayMetrics();
							getWindowManager().getDefaultDisplay().getMetrics(metrics);

							Tooltip.make(activity,
									new Tooltip.Builder(1) // 1 = id
											.anchor(findViewById(R.id.tabs_help), Tooltip.Gravity.BOTTOM)
											.closePolicy(Tooltip.ClosePolicy.TOUCH_OUTSIDE_NO_CONSUME, 20000)
											.activateDelay(50)
											.showDelay(200)
											.text(getResources().getString(R.string.help_tip_sliding_pages))
											.maxWidth((metrics.widthPixels) / 10 * 9)
											.withArrow(true)
											.withOverlay(true)
											.withStyleId(R.style.ToolTipStyle)
											.build()
							).show();
						}
					}
				});
			}
		}, 200);

		// Load the banner ad
		mAdView = (AdView) findViewById(R.id.ad_view_help);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

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
		if (mAdView != null) {
			mAdView.resume();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mAdView != null) {
			mAdView.destroy();
		}
		DesnoUtils.showAd();
	}

	public void onViewClick(View v) {
		switch (v.getId()) {

			//minecraft image and text
			case R.id.minecraft_app_layout: {
				try {
					//play store installed
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_INSTALLED + Keys.KEY_PACKAGE_MINECRAFT)));
				} catch (android.content.ActivityNotFoundException anfe) {
					//play store not installed
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_NOT_INSTALLED + Keys.KEY_PACKAGE_MINECRAFT)));
				}
				DesnoUtils.sendAction(mTracker, "Minecraft-app");
				break;
			}

			//blocklauncher image and text
			case R.id.blocklauncher_app_layout: {
				try {
					//play store installed
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_INSTALLED + Keys.KEY_PACKAGE_BLOCKLAUNCHER)));
				} catch (android.content.ActivityNotFoundException anfe) {
					//play store not installed
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_NOT_INSTALLED + Keys.KEY_PACKAGE_BLOCKLAUNCHER)));
				}
				DesnoUtils.sendAction(mTracker, "Blocklauncher-app");
				break;
			}
		}
	}

	public void onImageClick(View v) {

		// after the first time opening a full resolution image the user doesn't need the tooltip anymore
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putBoolean("user_understood_full_resolution_help", true);
		editor.apply();

		// starting the zoomImage activity (it has a switch case for the id passed to the intent)
		Intent i = new Intent(this, ZoomImageActivity.class);
		i.putExtra("viewId", v.getId());

		if (Build.VERSION.SDK_INT >= 21) {
			String transitionName = "transitionZoom";
			ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, v, transitionName);
			startActivity(i, transitionActivityOptions.toBundle());
		} else {
			startActivity(i);
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
	 * sections of the app.
	 */
	private class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int i) {
			switch (i) {
				case 0:
					return new FragmentTabHelp1();
				case 1:
					return new FragmentTabHelp2();
				case 2:
					return new FragmentTabHelp3();
				default:
					return null;
			}
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return activity.getString(R.string.action_help);
				case 1:
					return activity.getString(R.string.help_title_download);
				case 2:
					return activity.getString(R.string.help_title_installation);
				default:
					return "Missing title";
			}
		}
	}

	public static class FragmentTabHelp1 extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_help1, container, false);
		}
	}

	public static class FragmentTabHelp2 extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_help2, container, false);
		}
	}

	public static class FragmentTabHelp3 extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_help3, container, false);
		}
	}


}
