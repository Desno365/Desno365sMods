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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.desno365.mods.DesnoUtils;
import com.desno365.mods.R;
import com.desno365.mods.Receivers.AlarmReceiver;
import com.desno365.mods.SharedConstants.Keys;


public class SettingsActivity extends PreferenceActivity {

	private static final String TAG = "DesnoMods-SettingsActiv";

	public static Activity activity;

	private static Preference frequencyPreference;

	private static boolean monitorNotificationModsPreference;
	private static boolean monitorNotificationNewsPreference;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Activity started (onCreate)");
		DesnoUtils.setSavedTheme(this);
		DesnoUtils.setSavedLanguage(this);
		super.onCreate(savedInstanceState);

		activity = this;

		setContentView(R.layout.activity_settings);


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

	@Override
	public void onBackPressed() {
		this.finish();
		DesnoUtils.changeFinishAnimations(activity);
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

	public static class PrefsFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.fragmented_preferences);
			activity.setTheme(R.style.PreferenceFragmentTheme);

			// initialize preferences
			SharedPreferences sharedPrefs = getPreferenceScreen().getSharedPreferences();
			Preference notificationModsPreference = findPreference("notification_bool_mods");
			Preference notificationNewsPreference = findPreference("notification_bool_news");
			frequencyPreference = findPreference("sync_frequency");
			Preference languagePreference = findPreference("selected_language");
			Preference helpTranslatingPreference = findPreference("help_translating");
			Preference themePreference = findPreference("selected_theme");
			Preference restoreTipsPreference = findPreference("restore_tips");

			monitorNotificationModsPreference = sharedPrefs.getBoolean(notificationModsPreference.getKey(), true);
			monitorNotificationNewsPreference = sharedPrefs.getBoolean(notificationNewsPreference.getKey(), true);


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
					restartDialogLanguage();
					return true;
				}
			});


			// help translating preference
			helpTranslatingPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_APP_TRANSLATIONS)));
					return false;
				}
			});

			// open popup when theme preference is changed
			themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					restartDialogTheme();
					return true;
				}
			});

			// restore suggestions preference
			restoreTipsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					// restore tooltip
					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
					SharedPreferences.Editor editor = sharedPrefs.edit();
					editor.putBoolean("user_understood_full_resolution_help", false);
					editor.apply();

					Toast.makeText(activity.getApplicationContext(), getString(R.string.restored_toast), Toast.LENGTH_SHORT).show();
					return false;
				}
			});

		}
	}

	private static void restartDialogLanguage() {
		View mView = View.inflate(activity, R.layout.popup_settings_restart_language, null);

		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
		builder.setView(mView);
		builder.setTitle(activity.getResources().getString(R.string.app_name));
		builder.setNeutralButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				MainActivity.activity.finish();
				System.exit(0);
			}
		});
		builder.setCancelable(false);

		android.app.AlertDialog popup = builder.create();
		popup.setCanceledOnTouchOutside(false);
		popup.show();
	}

	private static void restartDialogTheme() {
		View mView = View.inflate(activity, R.layout.popup_settings_restart_theme, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(mView);
		builder.setTitle(activity.getResources().getString(R.string.app_name));
		builder.setNeutralButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				MainActivity.activity.finish();
				System.exit(0);
			}
		});
		builder.setCancelable(false);

		AlertDialog popup = builder.create();
		popup.setCanceledOnTouchOutside(false);
		popup.show();
	}

}
