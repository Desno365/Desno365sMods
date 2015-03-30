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
import com.desno365.mods.Keys;
import com.desno365.mods.NotificationsId;

import java.util.Calendar;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "DesnoMods-AlarmReceiver";
    private static Context currentContext;

    private String latestGunsVersion = "";
    private String latestPortalVersion = "";
    private String latestLaserVersion = "";
    private String latestTurretsVersion = "";
    private String latestJukeboxVersion = "";
    private String latestUnrealVersion = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            currentContext = context;
            Log.i(TAG, "Alarm running now: checking updates with AsyncTask");

            RetrieveModsUpdatesFromAlarm downloadTask = new RetrieveModsUpdatesFromAlarm();
            downloadTask.execute((Void) null);

        } catch (Exception err) {
            Log.e(TAG, "Exception in onReceive() ", err);
        }
    }

    public void setAlarm(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedPreferences.getBoolean("notification_bool", true)) {
            Log.i(TAG, "Alarm set. notification_bool preference = true");

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
            PendingIntent myPendingIntent = PendingIntent.getBroadcast(context, 365, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            // Get the AlarmManager service
            AlarmManager myAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // start the alarm manager
            myAlarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES, timeFrequency, myPendingIntent);
        } else {
            Log.i(TAG, "Alarm not set: notification_bool preferences = false");
        }
    }

    public void cancelAlarm(Context context) {
        Log.i(TAG, "Alarm canceled.");
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent myPendingIntent = PendingIntent.getBroadcast(context, 365, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Get the AlarmManager service
        AlarmManager myAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        myAlarmManager.cancel(myPendingIntent);
        myPendingIntent.cancel();
    }

    private class RetrieveModsUpdatesFromAlarm extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if(DesnoUtils.isNetworkAvailable(currentContext)) {
                latestGunsVersion = DesnoUtils.getTextFromUrl(Keys.KEY_DESNOGUNS_VERSION);
                latestPortalVersion = DesnoUtils.getTextFromUrl(Keys.KEY_PORTAL_VERSION);
                latestLaserVersion = DesnoUtils.getTextFromUrl(Keys.KEY_LASER_VERSION);
                latestTurretsVersion = DesnoUtils.getTextFromUrl(Keys.KEY_TURRETS_VERSION);
                latestJukeboxVersion = DesnoUtils.getTextFromUrl(Keys.KEY_JUKEBOX_VERSION);
                latestUnrealVersion = DesnoUtils.getTextFromUrl(Keys.KEY_UNREAL_VERSION);
            } else {
                Log.i(TAG, "Internet connection not found. Expected empty strings");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.i(TAG, "onPostExecute now, the AsyncTask finished");
            DesnoUtils.notifyForNewUpdates(currentContext, latestGunsVersion, latestPortalVersion, latestLaserVersion, latestTurretsVersion, latestJukeboxVersion, latestUnrealVersion);

            Random r = new Random();
            int randomInt = r.nextInt(NotificationsId.ID_DEBUG_LAST_NUMBER - NotificationsId.ID_DEBUG_FIRST_NUMBER) + NotificationsId.ID_DEBUG_FIRST_NUMBER;
            Calendar c = Calendar.getInstance();
            int minute = c.get(Calendar.MINUTE);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            DesnoUtils.generalNotification(currentContext, "Updates in background.", "Alarm h" + hour + " m" + minute, randomInt);
        }

    }

}
