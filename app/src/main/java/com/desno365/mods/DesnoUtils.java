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

import com.desno365.mods.Activities.MainActivity;
import com.desno365.mods.Mods.DesnoGuns;
import com.desno365.mods.Mods.Jukebox;
import com.desno365.mods.Mods.Laser;
import com.desno365.mods.Mods.Mod;
import com.desno365.mods.Mods.Portal;
import com.desno365.mods.Mods.Turrets;
import com.desno365.mods.Mods.Unreal;

import org.apache.http.HttpEntity;
import org.apache.http.entity.BufferedHttpEntity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class DesnoUtils {

    private static final String TAG = "DesnoMods-DesnoUtils";

    private static final String errorString = "Error";

    private static final String notInitializedStringError = "r000";

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

    public static void generalNotification(Context context, String title, String content, int id) {

        Intent notificationIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // notification
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context);
        noti.setSmallIcon(R.drawable.ic_notification_main);
        noti.setContentTitle(title);
        noti.setContentText(content);
        noti.setContentIntent(resultPendingIntent);
        noti.setAutoCancel(true);
        noti.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
        noti.setColor(context.getResources().getColor(R.color.minecraft_dirt_dark));

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, noti.build());

    }

    public static void notificationForNewVersion(Context context, Mod mod) {

        // content
        String contentText = context.getString(R.string.notification_new_version_content1) + " " + mod.getName(context) + " " + context.getString(R.string.notification_new_version_content2);


        // main click of the notification = launches MainActivity
        Intent mainClickIntent = new Intent(context, MainActivity.class);
        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(mainClickIntent);
        PendingIntent mainClickPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        // download button of notification = go to the website where the download is available
        Intent downloadClickIntent = mod.getDownloadFromWebsiteIntent();
        // Because clicking the notification launches a new ("special") activity,
        // there's no need to create an artificial back stack.
        PendingIntent downloadClickPendingIntent = PendingIntent.getActivity(context, 0, downloadClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        // thread button of notification = go to the minecraftforum.net thread
        Intent threadClickIntent = mod.getVisitThreadIntent();
        // Because clicking the notification launches a new ("special") activity,
        // there's no need to create an artificial back stack.
        PendingIntent threadClickPendingIntent = PendingIntent.getActivity(context, 0, threadClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // notification
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context);
        noti.setSmallIcon(R.drawable.ic_notification_main);
        noti.setContentTitle(context.getString(R.string.notification_new_version_title));
        noti.setContentText(contentText);
        noti.setContentIntent(mainClickPendingIntent);
        noti.setAutoCancel(true);
        noti.setStyle(new NotificationCompat.BigTextStyle().bigText(contentText));
        noti.setColor(context.getResources().getColor(R.color.minecraft_dirt_dark));
        noti.addAction(R.drawable.ic_notification_download, context.getString(R.string.notification_download), downloadClickPendingIntent);
        noti.addAction(R.drawable.ic_notification_thread, context.getString(R.string.notification_thread), threadClickPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mod.NOTIFICATION_ID_NEW_VERSION, noti.build());

    }

    public static void notifyForNewUpdates(Context context, String latestGunsVersion, String latestPortalVersion, String latestLaserVersion, String latestTurretsVersion, String latestJukeboxVersion, String latestUnrealVersion) {

        if(checkIfNewVersion(context, latestGunsVersion, "known_guns_version")) {
            DesnoUtils.notificationForNewVersion(context, new DesnoGuns());
        }

        if(checkIfNewVersion(context, latestPortalVersion, "known_portal_version")) {
            DesnoUtils.notificationForNewVersion(context, new Portal());
        }

        if(checkIfNewVersion(context, latestLaserVersion, "known_laser_version")) {
            DesnoUtils.notificationForNewVersion(context, new Laser());
        }

        if(checkIfNewVersion(context, latestTurretsVersion, "known_turrets_version")) {
            DesnoUtils.notificationForNewVersion(context, new Turrets());
        }

        if(checkIfNewVersion(context, latestJukeboxVersion, "known_jukebox_version")) {
            DesnoUtils.notificationForNewVersion(context, new Jukebox());
        }

        if(checkIfNewVersion(context, latestUnrealVersion, "known_unreal_version")) {
            DesnoUtils.notificationForNewVersion(context, new Unreal());
        }

        // debug
        //debugVersions(context, latestGunsVersion, latestPortalVersion, latestLaserVersion, latestTurretsVersion, latestJukeboxVersion, latestUnrealVersion);
    }

    private static boolean checkIfNewVersion(Context context, String latestVersion, String preferenceName) {

        // latestVersion is the version that the app found on internet
        // preferenceName is the string name of the preference of the mod
        // modName is the name of it

        boolean isNewVersion = false;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String knownVersion = sharedPrefs.getString(preferenceName, notInitializedStringError);

        Log.i(TAG, "Checking saved version of " + preferenceName + ", found latest: " + latestVersion + " known: " + knownVersion);

        if(latestVersion.equals("") || latestVersion.isEmpty() || latestVersion.equals("Not Found") || latestVersion.equals(errorString)) {
            Log.e(TAG, "Something went wrong, not displaying notification for " + preferenceName + " (empty String)");
        } else {
            if(latestVersion.length() > 10) {
                Log.e(TAG, "The latest version of " + preferenceName + " shouldn't be so long, probably an internal error happened on the website.");
            } else {

                // if we have arrived here it means that no errors happened, yay!
                if(!(knownVersion.equals(latestVersion))) {
                    if(!(knownVersion.equals(notInitializedStringError))) {
                        Log.i(TAG, "Different version for " + preferenceName + ", displaying notification");
                        isNewVersion = true;
                    } else {
                        Log.i(TAG, "First time the app access the known " + preferenceName + " version.");
                    }

                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString(preferenceName, latestVersion);
                    editor.apply();
                }

            }
        }

        return isNewVersion;
    }

    private static void debugVersions(Context context, String latestGunsVersion, String latestPortalVersion, String latestLaserVersion, String latestTurretsVersion, String latestJukeboxVersion, String latestUnrealVersion) {
        DesnoUtils.notificationForNewVersion(context, new DesnoGuns());

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        String knownGunsVersion = sharedPrefs.getString("known_guns_version", notInitializedStringError);
        String knownPortalVersion = sharedPrefs.getString("known_portal_version", notInitializedStringError);
        String knownLaserVersion = sharedPrefs.getString("known_laser_version", notInitializedStringError);
        String knownTurretsVersion = sharedPrefs.getString("known_turrets_version", notInitializedStringError);
        String knownJukeboxVersion = sharedPrefs.getString("known_jukebox_version", notInitializedStringError);
        String knownUnrealVersion = sharedPrefs.getString("known_unreal_version", notInitializedStringError);

        Log.d(TAG, "Log:" + " g: " + latestGunsVersion + knownGunsVersion + " p: " + latestPortalVersion + knownPortalVersion + " l: " + latestLaserVersion + knownLaserVersion + " t: " + latestTurretsVersion + knownTurretsVersion + " j: " + latestJukeboxVersion + knownJukeboxVersion + " u: " + latestUnrealVersion + knownUnrealVersion);

        DesnoUtils.generalNotification(context, "Log", "Log:" + " g: " + latestGunsVersion + knownGunsVersion + " p: " + latestPortalVersion + knownPortalVersion + " l: " + latestLaserVersion + knownLaserVersion + " t: " + latestTurretsVersion + knownTurretsVersion + " j: " + latestJukeboxVersion + knownJukeboxVersion + " u: " + latestUnrealVersion + knownUnrealVersion, NotificationsId.ID_DEBUG_VERSIONS);
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
