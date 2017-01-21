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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.desno365.mods.AnalyticsApplication;
import com.desno365.mods.DesnoUtils;
import com.desno365.mods.R;
import com.desno365.mods.Receivers.AlarmReceiver;
import com.desno365.mods.SharedConstants.DefaultSettingsValues;
import com.desno365.mods.SharedConstants.Keys;
import com.desno365.mods.SharedVariables.SharedVariables;
import com.google.android.gms.analytics.Tracker;


public class SettingsActivity extends BaseActivity {

	private static final String TAG = "SettingsActivity";

	private AppCompatActivity activity;

	// analytics tracker
	private static Tracker mTracker;

	private static Preference frequencyPreference;

	private static boolean monitorNotificationModsPreference;
	private static boolean monitorNotificationNewsPreference;
	private static boolean monitorAnonymousStatisticsPreference;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Activity started (onCreate)");
		super.onCreate(savedInstanceState);

		activity = this;

		setContentView(R.layout.activity_settings);



		// Start Google Analytics
		AnalyticsApplication application = (AnalyticsApplication) getApplication();
		mTracker = application.getDefaultTracker();

		// Send screen change
		DesnoUtils.sendScreenChange(mTracker, "SettingsActivity");

		// Set up the action bar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_settings); // Attaching the layout to the toolbar object
		toolbar.setTitle(R.string.action_settings);
		toolbar.setNavigationIcon(R.drawable.ic_navigation_arrow_back);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.finish();
				DesnoUtils.changeFinishAnimations(activity);
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		DesnoUtils.showAd();

		Log.i(TAG, "onDestroy. Launched the new alarm.");

		// change alarmManager for notifications
		AlarmReceiver aR = new AlarmReceiver();
		aR.cancelAlarm(activity.getApplicationContext());
		aR.setAlarm(activity.getApplicationContext());
	}

	public static class PrefsFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.fragmented_preferences);
			getActivity().setTheme(R.style.PreferenceFragmentTheme);

			// initialize preferences
			SharedPreferences sharedPrefs = getPreferenceScreen().getSharedPreferences();
			Preference notificationModsPreference = findPreference("notification_bool_mods");
			Preference notificationNewsPreference = findPreference("notification_bool_news");
			frequencyPreference = findPreference("sync_frequency");
			Preference languagePreference = findPreference("selected_language");
			Preference helpTranslatingPreference = findPreference("help_translating");
			Preference themePreference = findPreference("selected_theme");
			Preference restoreTipsPreference = findPreference("restore_tips");
			Preference anonymousStatisticsPreference = findPreference("anonymous_statistics");

			monitorNotificationModsPreference = sharedPrefs.getBoolean(notificationModsPreference.getKey(), DefaultSettingsValues.NOTIFICATIONS_MODS);
			monitorNotificationNewsPreference = sharedPrefs.getBoolean(notificationNewsPreference.getKey(), DefaultSettingsValues.NOTIFICATIONS_NEWS);
			monitorAnonymousStatisticsPreference = sharedPrefs.getBoolean(anonymousStatisticsPreference.getKey(), DefaultSettingsValues.ANONYMOUS_STATISTICS);


			notificationModsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object newValue) {

					monitorNotificationModsPreference = !monitorNotificationModsPreference;

					Log.i(TAG, "notification_bool_mods set to " + monitorNotificationModsPreference);

					if (monitorNotificationModsPreference || monitorNotificationNewsPreference)
						frequencyPreference.setEnabled(true);
					else
						frequencyPreference.setEnabled(false);

					return true;
				}
			});

			notificationNewsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object newValue) {

					monitorNotificationNewsPreference = !monitorNotificationNewsPreference;

					Log.i(TAG, "notification_bool_news set to " + monitorNotificationNewsPreference);

					if (monitorNotificationModsPreference || monitorNotificationNewsPreference)
						frequencyPreference.setEnabled(true);
					else
						frequencyPreference.setEnabled(false);

					return true;
				}
			});


			// enable or disable frequency preference at start
			if (monitorNotificationModsPreference || monitorNotificationNewsPreference)
				frequencyPreference.setEnabled(true);
			else
				frequencyPreference.setEnabled(false);


			// open popup when language preference is changed
			languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					restartAppDialog(getActivity(), R.string.restart_text_language_popup);
					DesnoUtils.sendAction(mTracker, "Language-changed");
					return true;
				}
			});


			// help translating preference
			helpTranslatingPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_APP_TRANSLATIONS)));
					DesnoUtils.sendAction(mTracker, "Translate-app-Crowdin");
					return false;
				}
			});

			// open popup when theme preference is changed
			themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					restartAppDialog(getActivity(), R.string.restart_text_popup);
					DesnoUtils.sendAction(mTracker, "Theme-changed");
					return true;
				}
			});

			anonymousStatisticsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					monitorAnonymousStatisticsPreference = !monitorAnonymousStatisticsPreference;

					SharedVariables.areStatisticsEnabled = monitorAnonymousStatisticsPreference;
					return true;
				}
			});

			// restore suggestions preference
			restoreTipsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					// restore tooltip
					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
					SharedPreferences.Editor editor = sharedPrefs.edit();
					editor.putBoolean("user_understood_full_resolution_help", false);
					editor.putBoolean("user_understood_sliding_pages_help", false);
					editor.apply();

					DesnoUtils.showAnimatedDefaultSnackbar((ViewGroup) getActivity().findViewById(R.id.viewgroup_snackbar_container_settings), R.string.restored_toast);
					return false;
				}
			});

		}
	}

	private static void restartAppDialog(Context context, int message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.exit(0);
			}
		});
		builder.setCancelable(false);

		AlertDialog popup = builder.create();
		popup.setCanceledOnTouchOutside(false);
		popup.show();
	}

}
