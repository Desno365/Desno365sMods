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

package com.desno365.mods.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.desno365.mods.DesnoUtils;
import com.desno365.mods.SharedConstants.Keys;


public class AlarmReceiver extends BroadcastReceiver {

	private static final String TAG = "DesnoMods-AlarmReceiver";
	private static Context currentContext;
	private final int ALARM_REQUEST_CODE = 365;
	private String latestNewsVersion = "";
	private String latestGunsVersion = "";
	private String latestPortalVersion = "";
	private String latestLaserVersion = "";
	private String latestTurretsVersion = "";
	private String latestJukeboxVersion = "";
	private String latestUnrealVersion = "";
	private boolean notificationModsBool;
	private boolean notificationNewsBool;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			currentContext = context;
			Log.i(TAG, "Alarm running now: checking updates with AsyncTask");

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(currentContext);
			notificationModsBool = sharedPreferences.getBoolean("notification_bool_mods", true);
			notificationNewsBool = sharedPreferences.getBoolean("notification_bool_news", true);

			if (notificationModsBool || notificationNewsBool) {
				Log.i(TAG, "Alarm: " + ((notificationModsBool) ? "Checking mods updates. " : "NOT checking mods updates. ") + ((notificationNewsBool) ? "Checking news." : "NOT checking news."));

				RetrieveNewsAndModsUpdates downloadTask = new RetrieveNewsAndModsUpdates();
				downloadTask.execute((Void) null);
			} else {
				Log.e(TAG, "Alarm: the alarm shouldn't have been started with preferences for notifications false.");
				Log.i(TAG, "Alarm canceled.");
			}
		} catch (Exception err) {
			Log.e(TAG, "Exception in onReceive() ", err);
		}
	}

	public void setAlarm(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		notificationModsBool = sharedPreferences.getBoolean("notification_bool_mods", true);
		notificationNewsBool = sharedPreferences.getBoolean("notification_bool_news", true);

		if (notificationModsBool || notificationNewsBool) {
			Log.i(TAG, "Alarm set. notification_bool_mods " + notificationModsBool + ", notification_bool_news " + notificationNewsBool);

			String selectedFrequency = sharedPreferences.getString("sync_frequency", "err");
			int frequency;
			try {
				frequency = Integer.parseInt(selectedFrequency);
			} catch (NumberFormatException err) {
				Log.e(TAG, "Exception in setAlarm(), frequency is not a number");
				frequency = 12;
			}
			Log.i(TAG, "Frequency found: " + frequency + " hour(s)");

			long timeFrequency;
			switch (frequency) {
				case 4:
					timeFrequency = AlarmManager.INTERVAL_HOUR * 4;
					break;
				case 12:
					timeFrequency = AlarmManager.INTERVAL_HALF_DAY;
					break;
				case 24:
					timeFrequency = AlarmManager.INTERVAL_DAY;
					break;
				case 72:
					timeFrequency = AlarmManager.INTERVAL_DAY * 3;
					break;
				default:
					timeFrequency = AlarmManager.INTERVAL_HALF_DAY;
					Log.e(TAG, "Error in setAlarm(), frequency is not a known number.");
					break;
			}
			Log.i(TAG, "AlarmManager will be set every " + timeFrequency + " milliseconds.");

			Intent intent = new Intent(context, AlarmReceiver.class);
			PendingIntent myPendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

			// Get the AlarmManager service
			AlarmManager myAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			// start the alarm manager
			myAlarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES, timeFrequency, myPendingIntent);
		} else {
			Log.i(TAG, "Alarm not set: notification_bool preferences for mods and news were false");
		}
	}

	public void cancelAlarm(Context context) {
		Log.i(TAG, "Alarm canceled.");
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent myPendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		// Get the AlarmManager service
		AlarmManager myAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		myAlarmManager.cancel(myPendingIntent);
		myPendingIntent.cancel();
	}

	private class RetrieveNewsAndModsUpdates extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			if (DesnoUtils.isNetworkAvailable(currentContext)) {
				if (notificationNewsBool) {
					latestNewsVersion = DesnoUtils.getTextFromUrl(Keys.KEY_NEWS_COUNT);
				}
				if (notificationModsBool) {
					latestGunsVersion = DesnoUtils.getTextFromUrl(Keys.KEY_DESNOGUNS_VERSION);
					latestPortalVersion = DesnoUtils.getTextFromUrl(Keys.KEY_PORTAL_VERSION);
					latestLaserVersion = DesnoUtils.getTextFromUrl(Keys.KEY_LASER_VERSION);
					latestTurretsVersion = DesnoUtils.getTextFromUrl(Keys.KEY_TURRETS_VERSION);
					latestJukeboxVersion = DesnoUtils.getTextFromUrl(Keys.KEY_JUKEBOX_VERSION);
					latestUnrealVersion = DesnoUtils.getTextFromUrl(Keys.KEY_UNREAL_VERSION);
				}
			} else {
				Log.i(TAG, "Internet connection not found. Expected empty strings");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			Log.i(TAG, "onPostExecute now, the AsyncTask finished");

			if (notificationNewsBool) {
				DesnoUtils.notifyForUnreadNews(currentContext, latestNewsVersion);
			}
			if (notificationModsBool) {
				DesnoUtils.notifyForNewUpdates(currentContext, latestGunsVersion, latestPortalVersion, latestLaserVersion, latestTurretsVersion, latestJukeboxVersion, latestUnrealVersion);
			}

			// debug notification
			/*Random r = new Random();
            int randomInt = r.nextInt(NotificationsId.ID_DEBUG_LAST_NUMBER - NotificationsId.ID_DEBUG_FIRST_NUMBER) + NotificationsId.ID_DEBUG_FIRST_NUMBER;
            Calendar c = Calendar.getInstance();
            int minute = c.get(Calendar.MINUTE);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            DesnoUtils.generalNotification(currentContext, "Updates in background.", "Alarm h" + hour + " m" + minute, randomInt);*/
		}

	}

}
