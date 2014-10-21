package com.desno365.mods;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "DesnoMods-AlarmReceiver";
    private static Context currentContext;

    private String latestPortalVersion = "";
    private String latestLaserVersion = "";
    private String latestTurretsVersion = "";
    private String latestJukeboxVersion = "";

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
                latestPortalVersion = DesnoUtils.getTextFromUrl("https://raw.githubusercontent.com/Desno365/MCPE-scripts/master/portalMOD-version");
                latestLaserVersion = DesnoUtils.getTextFromUrl("https://raw.githubusercontent.com/Desno365/MCPE-scripts/master/laserMOD-version");
                latestTurretsVersion = DesnoUtils.getTextFromUrl("https://raw.githubusercontent.com/Desno365/MCPE-scripts/master/turretsMOD-version");
                latestJukeboxVersion = DesnoUtils.getTextFromUrl("https://raw.githubusercontent.com/Desno365/MCPE-scripts/master/jukeboxMOD-version");
            } else {
                Log.i(TAG, "Internet connection not found. Expected empty strings");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.i(TAG, "onPostExecute now, the AsyncTask finished");

            DesnoUtils.readWriteVersionsAndNotify(currentContext, latestPortalVersion, latestLaserVersion, latestTurretsVersion, latestJukeboxVersion);

            /*SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(currentContext);

            String knownPortalVersion = sharedPrefs.getString("known_portal_version", "r000");
            String knownLaserVersion = sharedPrefs.getString("known_laser_version", "r000");
            String knownTurretsVersion = sharedPrefs.getString("known_turrets_version", "r000");
            String knownJukeboxVersion = sharedPrefs.getString("known_jukebox_version", "r000");

            Log.d(TAG, "Log: p: " + latestPortalVersion + knownPortalVersion + " l: " + latestLaserVersion + knownLaserVersion + " t: " + latestTurretsVersion + knownTurretsVersion + " j: " + latestJukeboxVersion + knownJukeboxVersion);

            if(latestPortalVersion.equals("") || latestPortalVersion.isEmpty() || latestPortalVersion.equals("Not Found")) {
                Log.e(TAG, "Something went wrong, not displaying notification for Portal (empty String)");
            } else {
                if(!(knownPortalVersion.equals(latestPortalVersion))) {
                    Log.i(TAG, "Different Portal version, displaying notification");

                    MainActivity.notification(currentContext, currentContext.getString(R.string.notification_new_version_content1) + " " + currentContext.getString(R.string.mod1_title) + " " + currentContext.getString(R.string.notification_new_version_content2), 365 + 100 + 1);

                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString("known_portal_version", latestPortalVersion);
                    editor.apply();
                }
            }
            if(latestLaserVersion.equals("") || latestLaserVersion.isEmpty() || latestLaserVersion.equals("Not Found")) {
                Log.e(TAG, "Something went wrong, not displaying notification for Laser (empty String)");
            } else {
                if(!(knownLaserVersion.equals(latestLaserVersion))) {
                    Log.i(TAG, "Different Laser version, displaying notification");

                    MainActivity.notification(currentContext, currentContext.getString(R.string.notification_new_version_content1) + " " + currentContext.getString(R.string.mod2_title) + " " + currentContext.getString(R.string.notification_new_version_content2), 365 + 100 + 2);

                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString("known_laser_version", latestLaserVersion);
                    editor.apply();
                }
            }
            if(latestTurretsVersion.equals("") || latestTurretsVersion.isEmpty() || latestTurretsVersion.equals("Not Found")) {
                Log.e(TAG, "Something went wrong, not displaying notification for Turrets (empty String)");
            } else {
                if(!(knownTurretsVersion.equals(latestTurretsVersion))) {
                    Log.i(TAG, "Different Turrets version, displaying notification");

                    MainActivity.notification(currentContext, currentContext.getString(R.string.notification_new_version_content1) + " " + currentContext.getString(R.string.mod3_title) + " " + currentContext.getString(R.string.notification_new_version_content2), 365 + 100 + 3);

                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString("known_turrets_version", latestTurretsVersion);
                    editor.apply();
                }
            }
            if(latestJukeboxVersion.equals("") || latestJukeboxVersion.isEmpty() || latestJukeboxVersion.equals("Not Found")) {
                Log.e(TAG, "Something went wrong, not displaying notification for Jukebox (empty String)");
            } else {
                if(!(knownJukeboxVersion.equals(latestJukeboxVersion))) {
                    Log.i(TAG, "Different Jukebox version, displaying notification");

                    MainActivity.notification(currentContext, currentContext.getString(R.string.notification_new_version_content1) + " " + currentContext.getString(R.string.mod4_title) + " " + currentContext.getString(R.string.notification_new_version_content2), 365 + 100 + 4);

                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString("known_jukebox_version", latestJukeboxVersion);
                    editor.apply();
                }
            }*/

            /* DEBUG:

            Random r = new Random();
            int randomInt = r.nextInt(50 - 1) + 1;
            Calendar c = Calendar.getInstance();
            int minute = c.get(Calendar.MINUTE);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            DesnoUtils.notification(currentContext, "Alarm h" + hour + " m" + minute, randomInt);
            DesnoUtils.notification(currentContext, "Log: p: " + latestPortalVersion + knownPortalVersion + " l: " + latestLaserVersion + knownLaserVersion + " t: " + latestTurretsVersion + knownTurretsVersion + " j: " + latestJukeboxVersion + knownJukeboxVersion, 51);*/

        }

    }

}
