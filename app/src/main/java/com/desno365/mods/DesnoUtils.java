package com.desno365.mods;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.entity.BufferedHttpEntity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DesnoUtils {

    private static final String TAG = "DesnoMods-DesnoUtil";

    public static boolean isNetworkAvailable(Context currentContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    public static String getTextFromUrl(String url) {
        try {
            AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Mozilla/5.0");
            HttpEntity myHttpEntity = httpClient.execute(new org.apache.http.client.methods.HttpGet(url)).getEntity();
            BufferedHttpEntity myBufferedEntity = new BufferedHttpEntity(myHttpEntity);
            InputStream myInputStream = myBufferedEntity.getContent();

            String loadedText = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(myInputStream));
            String row;
            while ((row = bufferedReader.readLine()) != null) {
                loadedText += row;
            }
            bufferedReader.close();
            httpClient.close();

            return loadedText;

        } catch (Exception err) {
            Log.e(TAG, "Exception in getTextFromUrl() ", err);
            return "Error";
        }
    }

    public static void notification(Context currentContext, String content, int id) {

        Intent notificationIntent = new Intent(currentContext, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(currentContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(currentContext)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(currentContext.getString(R.string.notification_new_version_title))
                        .setContentText(content)
                        .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) currentContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());
    }

    public static void readWriteVersionsAndNotify(Context currentContext, String latestPortalVersion, String latestLaserVersion, String latestTurretsVersion, String latestJukeboxVersion) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(currentContext);

        String notInitializedStringError = "r000";

        String knownPortalVersion = sharedPrefs.getString("known_portal_version", notInitializedStringError);
        String knownLaserVersion = sharedPrefs.getString("known_laser_version", notInitializedStringError);
        String knownTurretsVersion = sharedPrefs.getString("known_turrets_version", notInitializedStringError);
        String knownJukeboxVersion = sharedPrefs.getString("known_jukebox_version", notInitializedStringError);

        Log.d(TAG, "Log: p: " + latestPortalVersion + knownPortalVersion + " l: " + latestLaserVersion + knownLaserVersion + " t: " + latestTurretsVersion + knownTurretsVersion + " j: " + latestJukeboxVersion + knownJukeboxVersion);

        if(latestPortalVersion.equals("") || latestPortalVersion.isEmpty() || latestPortalVersion.equals("Not Found")) {
            Log.e(TAG, "Something went wrong, not displaying notification for Portal (empty String)");
        } else {
            if(!(knownPortalVersion.equals(latestPortalVersion))) {
                if(!(knownPortalVersion.equals(notInitializedStringError))) {
                    Log.i(TAG, "Different Portal version, displaying notification");

                    DesnoUtils.notification(currentContext, currentContext.getString(R.string.notification_new_version_content1) + " " + currentContext.getString(R.string.mod1_title) + " " + currentContext.getString(R.string.notification_new_version_content2), 365 + 100 + 1);
                } else
                    Log.i(TAG, "First time the app access the known portal version.");

                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("known_portal_version", latestPortalVersion);
                editor.apply();
            }
        }
        if(latestLaserVersion.equals("") || latestLaserVersion.isEmpty() || latestLaserVersion.equals("Not Found")) {
            Log.e(TAG, "Something went wrong, not displaying notification for Laser (empty String)");
        } else {
            if(!(knownLaserVersion.equals(latestLaserVersion))) {
                if(!(knownLaserVersion.equals(notInitializedStringError))) {
                    Log.i(TAG, "Different Laser version, displaying notification");

                    DesnoUtils.notification(currentContext, currentContext.getString(R.string.notification_new_version_content1) + " " + currentContext.getString(R.string.mod2_title) + " " + currentContext.getString(R.string.notification_new_version_content2), 365 + 100 + 2);
                } else
                    Log.i(TAG, "First time the app access the known laser version.");

                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("known_laser_version", latestLaserVersion);
                editor.apply();
            }
        }
        if(latestTurretsVersion.equals("") || latestTurretsVersion.isEmpty() || latestTurretsVersion.equals("Not Found")) {
            Log.e(TAG, "Something went wrong, not displaying notification for Turrets (empty String)");
        } else {
            if(!(knownTurretsVersion.equals(latestTurretsVersion))) {
                if(!(knownTurretsVersion.equals(notInitializedStringError))) {
                    Log.i(TAG, "Different Turrets version, displaying notification");

                    DesnoUtils.notification(currentContext, currentContext.getString(R.string.notification_new_version_content1) + " " + currentContext.getString(R.string.mod3_title) + " " + currentContext.getString(R.string.notification_new_version_content2), 365 + 100 + 3);
                } else
                    Log.i(TAG, "First time the app access the known turrets version.");

                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("known_turrets_version", latestTurretsVersion);
                editor.apply();
            }
        }
        if(latestJukeboxVersion.equals("") || latestJukeboxVersion.isEmpty() || latestJukeboxVersion.equals("Not Found")) {
            Log.e(TAG, "Something went wrong, not displaying notification for Jukebox (empty String)");
        } else {
            if(!(knownJukeboxVersion.equals(latestJukeboxVersion))) {
                if(!(knownJukeboxVersion.equals(notInitializedStringError))) {
                    Log.i(TAG, "Different Jukebox version, displaying notification");

                    DesnoUtils.notification(currentContext, currentContext.getString(R.string.notification_new_version_content1) + " " + currentContext.getString(R.string.mod4_title) + " " + currentContext.getString(R.string.notification_new_version_content2), 365 + 100 + 4);
                } else
                    Log.i(TAG, "First time the app access the known jukebox version.");

                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("known_jukebox_version", latestJukeboxVersion);
                editor.apply();
            }
        }

        //notification for debug
        //DesnoUtils.notification(currentContext, "Log: p: " + latestPortalVersion + knownPortalVersion + " l: " + latestLaserVersion + knownLaserVersion + " t: " + latestTurretsVersion + knownTurretsVersion + " j: " + latestJukeboxVersion + knownJukeboxVersion, 51);
    }

}
