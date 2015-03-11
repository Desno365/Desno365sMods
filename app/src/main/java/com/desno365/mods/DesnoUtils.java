package com.desno365.mods;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import java.util.Locale;

public class DesnoUtils {

    private static final String TAG = "DesnoMods-DesnoUtils";

    private static final String errorString = "Error";

    public static void setSavedTheme(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = sharedPrefs.getString("selected_theme", "0");
        int themeNumber = Integer.parseInt(theme);
        switch (themeNumber) {
            case 0:
                break;
            case 1:
                context.setTheme(R.style.AppThemeDarkActionBar);
                break;
            case 2:
                context.setTheme(R.style.AppThemeHoloDark);
                break;
        }
    }

    public static void setSavedLanguage(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String language = sharedPrefs.getString("selected_language", "not_changed");

        if(!language.equals("default") && !language.equals("not_changed")) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }

        // this will be removed when languages become more accurate
        // languages that I'm sure are accurate are not affected
        if(language.equals("not_changed") && !Locale.getDefault().getCountry().equals("IT")) {
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }

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
            return errorString;
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

    public static void readWriteVersionsAndNotify(Context currentContext, String latestGunsVersion, String latestPortalVersion, String latestLaserVersion, String latestTurretsVersion, String latestJukeboxVersion) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(currentContext);

        String notInitializedStringError = "r000";

        String knownGunsVersion = sharedPrefs.getString("known_guns_version", notInitializedStringError);
        String knownPortalVersion = sharedPrefs.getString("known_portal_version", notInitializedStringError);
        String knownLaserVersion = sharedPrefs.getString("known_laser_version", notInitializedStringError);
        String knownTurretsVersion = sharedPrefs.getString("known_turrets_version", notInitializedStringError);
        String knownJukeboxVersion = sharedPrefs.getString("known_jukebox_version", notInitializedStringError);

        Log.d(TAG, "Log:" + " g: " + latestGunsVersion + knownGunsVersion + " p: " + latestPortalVersion + knownPortalVersion + " l: " + latestLaserVersion + knownLaserVersion + " t: " + latestTurretsVersion + knownTurretsVersion + " j: " + latestJukeboxVersion + knownJukeboxVersion);

        if(latestGunsVersion.equals("") || latestGunsVersion.isEmpty() || latestGunsVersion.equals("Not Found") || latestGunsVersion.equals(errorString)) {
            Log.e(TAG, "Something went wrong, not displaying notification for Guns (empty String)");
        } else {
            if(!(knownGunsVersion.equals(latestGunsVersion))) {
                if(!(knownGunsVersion.equals(notInitializedStringError))) {
                    Log.i(TAG, "Different Guns version, displaying notification");

                    DesnoUtils.notification(currentContext, currentContext.getString(R.string.notification_new_version_content1) + " " + currentContext.getString(R.string.mod5_title) + " " + currentContext.getString(R.string.notification_new_version_content2), 365 + 100 + 5);
                } else
                    Log.i(TAG, "First time the app access the known guns version.");

                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("known_guns_version", latestGunsVersion);
                editor.apply();
            }
        }
        if(latestPortalVersion.equals("") || latestPortalVersion.isEmpty() || latestPortalVersion.equals("Not Found") || latestPortalVersion.equals(errorString)) {
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
        if(latestLaserVersion.equals("") || latestLaserVersion.isEmpty() || latestLaserVersion.equals("Not Found") || latestLaserVersion.equals(errorString)) {
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
        if(latestTurretsVersion.equals("") || latestTurretsVersion.isEmpty() || latestTurretsVersion.equals("Not Found") || latestTurretsVersion.equals(errorString)) {
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
        if(latestJukeboxVersion.equals("") || latestJukeboxVersion.isEmpty() || latestJukeboxVersion.equals("Not Found") || latestJukeboxVersion.equals(errorString)) {
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
        //DesnoUtils.notification(currentContext, "Log:" + " g: " + latestGunsVersion + knownGunsVersion + " p: " + latestPortalVersion + knownPortalVersion + " l: " + latestLaserVersion + knownLaserVersion + " t: " + latestTurretsVersion + knownTurretsVersion + " j: " + latestJukeboxVersion + knownJukeboxVersion, 51);
    }

    public static void changeStartAnimations(Activity activity) {
        Context currentContext = activity.getApplicationContext();
        switch (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(currentContext).getString("selected_animations", "0"))) {
            case 0:
                break;
            case 1:
                overrideStartActivityAnimation(activity, R.anim.abc_slide_in_bottom);
                break;
            case 2:
                overrideStartActivityAnimation(activity, R.anim.scale_from_corner);
                break;
            case 3:
                overrideStartActivityAnimation(activity, R.anim.shrink_and_rotate_enter);
                break;
        }
    }

    public static void changeFinishAnimations(Activity activity) {
        Context currentContext = activity.getApplicationContext();
        switch (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(currentContext).getString("selected_animations", "0"))) {
            case 0:
                break;
            case 1:
                overrideFinishActivityAnimation(activity, R.anim.push_down_out);
                break;
            case 2:
                overrideFinishActivityAnimation(activity, R.anim.scale_towards_corner);
                break;
            case 3:
                overrideFinishActivityAnimation(activity, R.anim.shrink_and_rotate_exit);
                break;
        }
    }

    private static void overrideStartActivityAnimation(Activity activity, int resId) {

        activity.overridePendingTransition(resId, R.anim.hold);
    }

    private static void overrideFinishActivityAnimation(Activity activity, int resId) {
        activity.overridePendingTransition(R.anim.hold, resId);
    }

}
