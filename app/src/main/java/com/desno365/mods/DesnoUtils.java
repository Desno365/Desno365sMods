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

package com.desno365.mods;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.desno365.mods.Activities.MainActivity;
import com.desno365.mods.Activities.NewsActivity;
import com.desno365.mods.Mods.DesnoGuns;
import com.desno365.mods.Mods.Jukebox;
import com.desno365.mods.Mods.Laser;
import com.desno365.mods.Mods.Mod;
import com.desno365.mods.Mods.Portal;
import com.desno365.mods.Mods.Turrets;
import com.desno365.mods.Mods.Unreal;
import com.desno365.mods.SharedConstants.DefaultSettingsValues;
import com.desno365.mods.SharedConstants.NotificationsId;
import com.desno365.mods.SharedConstants.SharedConstants;
import com.desno365.mods.SharedVariables.SharedVariables;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import hugo.weaving.DebugLog;


public class DesnoUtils {

	private static final String TAG = "DesnoUtils";

	private static final String ERROR_STRING = "Error";
	private static final String NOT_INITIALIZED_ERROR_STRING = "r000";


	/* ######### APP UTILS ######### */
	public static void setSavedLanguage(Context context) {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		String[] language = sharedPrefs.getString("selected_language", "not_changed").split("-r");

		if (!language[0].equals("default") && !language[0].equals("not_changed")) {
			Locale locale;
			if(language.length == 1)
				locale = new Locale(language[0]);
			else
				locale = new Locale(language[0], language[1]);
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
		}

		// this will be removed when languages become more accurate
		// languages that I'm sure are accurate are not affected
		if (language[0].equals("not_changed") && !Locale.getDefault().getCountry().equals("IT")) {
			Locale locale = new Locale("en");
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
		}
	}

	public static void setSavedTheme(Context context) {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		String theme = sharedPrefs.getString("selected_theme", "0");
		try {
			int themeNumber = Integer.parseInt(theme);
			switch (themeNumber) {
				case 0:
					context.setTheme(R.style.AppTheme_Brown);
					break;
				case 1:
					context.setTheme(R.style.AppTheme_Green);
					break;
				default:
					context.setTheme(R.style.AppTheme_Brown);
					break;
			}
		} catch(NumberFormatException e) {
			Log.e(TAG, "NumberFormatExcpetion in setSavedTheme() with " + theme, e);
			context.setTheme(R.style.AppTheme_Brown);
		}

	}

	private static boolean checkIfNewVersion(Context context, String latestVersion, String preferenceName) {

		// latestVersion is the version that the app found on internet
		// preferenceName is the string name of the preference of the mod/content

		boolean isNewVersion = false;

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		String knownVersion = sharedPrefs.getString(preferenceName, NOT_INITIALIZED_ERROR_STRING);

		Log.i(TAG, "Checking saved version of " + preferenceName + ", found latest: " + latestVersion + " known: " + knownVersion);

		if (latestVersion.equals("") || latestVersion.isEmpty() || latestVersion.equals("Not Found") || latestVersion.equals(ERROR_STRING)) {
			Log.e(TAG, "Something went wrong in checkIfNewVersion() for " + preferenceName + " (empty String)");
		} else {
			if (latestVersion.length() > 10) {
				Log.e(TAG, "The latest version of " + preferenceName + " shouldn't be so long, probably an internal error happened on the website.");
			} else {

				// if we have arrived here it means that no errors happened, yay!
				if (!(knownVersion.equals(latestVersion))) {
					if (!(knownVersion.equals(NOT_INITIALIZED_ERROR_STRING))) {
						Log.i(TAG, "Different version for " + preferenceName + ". Maybe a notification should appear.");
						isNewVersion = true;
					} else {
						Log.i(TAG, "First time the app access the saved " + preferenceName + " version.");
					}

					SharedPreferences.Editor editor = sharedPrefs.edit();
					editor.putString(preferenceName, latestVersion);
					editor.apply();
				}

			}
		}

		return isNewVersion;
	}

	@Nullable
	public static String getMinecraftVersion(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo("com.mojang.minecraftpe", 0);
			return pInfo.versionName.replace("b", "beta");
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "Minecraft PE not installed");
			return null;
		}
	}
	/* ######### APP UTILS ######### */


	/* ######### VIEWS UTILS ######### */
	public static int convertDpToPixel(int dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int px;
		px = (int) (dp * metrics.density);
		return px;
	}

	@SuppressWarnings("unused")
	public static int convertPixelsToDp(int px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int dp;
		dp = (int) (px / metrics.density);
		return dp;
	}

	public static void setViewHeight(final View view, final int height) {
		ViewGroup.LayoutParams params = view.getLayoutParams();
		params.height = height;
		view.setLayoutParams(params);
	}

	public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
		if (Build.VERSION.SDK_INT < 16) {
			//noinspection deprecation
			v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
		} else {
			v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
		}
	}
	/* ######### VIEWS UTILS ######### */


	/* ######### SNACKBAR ######### */
	public static void showDefaultSnackbar(View parent, int text) {
		showDefaultSnackbar(parent, text, Snackbar.LENGTH_SHORT);
	}

	public static void showDefaultSnackbar(View parent, int text, int duration) {
		Snackbar snack = getDefaultSnackbar(parent, text, duration);

		snack.show();
	}

	public static void showAnimatedDefaultSnackbar(ViewGroup parent, int text) {
		showAnimatedDefaultSnackbar(parent, text, Snackbar.LENGTH_SHORT);
	}

	public static void showAnimatedDefaultSnackbar(final ViewGroup parent, int text, int duration) {
		// the parent ViewGroup, that is the container that is animated when showing the Snackbar, mustn't be a CoordinatorLayout

		final Snackbar snack = getDefaultSnackbar(parent, text, duration);

		// add dismiss animations to the container layout
		snack.setCallback(new Snackbar.Callback() {
			@Override
			public void onDismissed(Snackbar snackbar, int event) {
				super.onDismissed(snackbar, event);

				final AnimatorSet set2 = new AnimatorSet();
				set2.play(Glider.glide(Skill.Linear, 0.005f, ObjectAnimator.ofFloat(parent, "translationY", -snackbar.getView().getHeight(), 0)));
				set2.start();
			}
		});

		// add show animations to the container layout
		snack.getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				DesnoUtils.removeOnGlobalLayoutListener(snack.getView(), this);

				final AnimatorSet set = new AnimatorSet();
				set.play(Glider.glide(Skill.CircEaseOut, 0.005f, ObjectAnimator.ofFloat(parent, "translationY", 0, -snack.getView().getHeight())));
				set.start();
			}
		});

		snack.show();
	}

	private static Snackbar getDefaultSnackbar(View parent, int text, int duration) {
		Snackbar snack = Snackbar.make(parent, text, duration);

		// make text white
		View view = snack.getView();
		@SuppressLint("PrivateResource")
		TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
		tv.setTextColor(Color.WHITE);

		// set background color
		TypedArray a = parent.getContext().getTheme().obtainStyledAttributes(new int[]{ R.attr.color_primary_dark });
		int color1 = a.getColor(0, 0);
		a.recycle();
		view.setBackgroundColor(color1);

		return snack;
	}
	/* ######### SNACKBAR ######### */


	/* ######### NETWORK ######### */
	public static boolean isNetworkAvailable(Context currentContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
	}

	@DebugLog
	public static String getTextFromUrl(String stringUrl) {
		try {

			// Download content
			URL url = new URL(stringUrl);
			URLConnection connection = url.openConnection();

			// Get content
			InputStream myInputStream = connection.getInputStream();

			// Read result
			String loadedText = "";
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(myInputStream));
			String row;
			while ((row = bufferedReader.readLine()) != null) {
				loadedText += row;
			}
			bufferedReader.close();

			return loadedText;

		} catch (Exception err) {
			Log.e(TAG, "Exception in getTextFromUrl() ", err);
			return ERROR_STRING;
		}
	}
	/* ######### NETWORK ######### */


	/* ######### NOTIFICATIONS ######### */
	public static NotificationCompat.Builder defaultNotification(Context context, String title, String content, Intent contentIntent) {

		// The stack builder object will contain an artificial back stack for the started Activity.
		// This ensures that navigating backward from the Activity leads out of your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(contentIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		// notification
		NotificationCompat.Builder noti = new NotificationCompat.Builder(context);
		noti.setSmallIcon(R.drawable.ic_notification_main);
		noti.setContentTitle(title);
		noti.setContentText(content);
		noti.setContentIntent(resultPendingIntent);
		noti.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
		noti.setTicker(content);
		noti.setAutoCancel(true); // notification is automatically canceled when the user clicks it in the panel
		noti.setColor(context.getResources().getColor(R.color.minecraft_brown_dirt_dark));
		noti.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
		noti.setPriority(NotificationCompat.PRIORITY_HIGH); // "A notification's big view appears only when the notification is expanded, which happens when the notification is at the top of the notification drawer"

		return noti;
	}

	public static void generalNotification(Context context, String title, String content, int id) {
		DesnoUtils.generalNotification(context, title, content, id, new Intent(context, MainActivity.class));
	}

	public static void generalNotification(Context context, String title, String content, int id, Intent customIntent) {
		NotificationCompat.Builder noti = defaultNotification(context, title, content, customIntent);

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(id, noti.build());
	}

	public static void notificationForNewVersion(Context context, Mod mod) {
		// content
		String contentText = context.getString(R.string.notification_new_version_content1) + " " + mod.getName(context) + " " + context.getString(R.string.notification_new_version_content2);

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
		NotificationCompat.Builder noti = defaultNotification(context, context.getString(R.string.notification_new_version_title), contentText, new Intent(context, MainActivity.class));
		noti.addAction(R.drawable.ic_notification_download, context.getString(R.string.notification_download), downloadClickPendingIntent);
		noti.addAction(R.drawable.ic_notification_thread, context.getString(R.string.notification_thread), threadClickPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(mod.NOTIFICATION_ID_NEW_VERSION, noti.build());


		// Send event to Analytics
		Tracker mTracker = DesnoUtils.getTracker(context);
		DesnoUtils.sendEvent(mTracker, "Notification-Mod", "New version of the " + mod.getName(context));
	}

	public static void notifyForNewUpdates(Context context, String latestGunsVersion, String latestPortalVersion, String latestLaserVersion, String latestTurretsVersion, String latestJukeboxVersion, String latestUnrealVersion) {

		if (checkIfNewVersion(context, latestGunsVersion, "known_guns_version")) {
			DesnoUtils.notificationForNewVersion(context, new DesnoGuns(context));
		}

		if (checkIfNewVersion(context, latestPortalVersion, "known_portal_version")) {
			DesnoUtils.notificationForNewVersion(context, new Portal(context));
		}

		if (checkIfNewVersion(context, latestLaserVersion, "known_laser_version")) {
			DesnoUtils.notificationForNewVersion(context, new Laser(context));
		}

		if (checkIfNewVersion(context, latestTurretsVersion, "known_turrets_version")) {
			DesnoUtils.notificationForNewVersion(context, new Turrets(context));
		}

		if (checkIfNewVersion(context, latestJukeboxVersion, "known_jukebox_version")) {
			DesnoUtils.notificationForNewVersion(context, new Jukebox(context));
		}

		if (checkIfNewVersion(context, latestUnrealVersion, "known_unreal_version")) {
			DesnoUtils.notificationForNewVersion(context, new Unreal(context));
		}

		// test notification
		//DesnoUtils.notificationForNewVersion(context, MainActivity.MOD_GUNS);

		// debug versions
		//debugVersions(context, latestGunsVersion, latestPortalVersion, latestLaserVersion, latestTurretsVersion, latestJukeboxVersion, latestUnrealVersion);
	}

	public static void notifyForUnreadNews(Context context, String latestNews) {
		if (checkIfNewVersion(context, latestNews, "latest_read_news")) {
			DesnoUtils.generalNotification(context, context.getString(R.string.app_name), context.getString(R.string.notification_unread_news), NotificationsId.ID_UNREAD_NEWS, new Intent(context, NewsActivity.class));

			// Send event to Analytics
			Tracker mTracker = DesnoUtils.getTracker(context);
			DesnoUtils.sendEvent(mTracker, "Notification-News", "Unread News, latest news count: " + latestNews);
		}

		// test notification
		//DesnoUtils.generalNotification(context, context.getString(R.string.app_name), context.getString(R.string.notification_unread_news), NotificationsId.ID_UNREAD_NEWS, new Intent(context, NewsActivity.class));
	}

	@SuppressWarnings("unused")
	private static void debugVersions(Context context, String latestGunsVersion, String latestPortalVersion, String latestLaserVersion, String latestTurretsVersion, String latestJukeboxVersion, String latestUnrealVersion) {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

		String knownGunsVersion = sharedPrefs.getString("known_guns_version", NOT_INITIALIZED_ERROR_STRING);
		String knownPortalVersion = sharedPrefs.getString("known_portal_version", NOT_INITIALIZED_ERROR_STRING);
		String knownLaserVersion = sharedPrefs.getString("known_laser_version", NOT_INITIALIZED_ERROR_STRING);
		String knownTurretsVersion = sharedPrefs.getString("known_turrets_version", NOT_INITIALIZED_ERROR_STRING);
		String knownJukeboxVersion = sharedPrefs.getString("known_jukebox_version", NOT_INITIALIZED_ERROR_STRING);
		String knownUnrealVersion = sharedPrefs.getString("known_unreal_version", NOT_INITIALIZED_ERROR_STRING);

		Log.d(TAG, "Log:" + " g: " + latestGunsVersion + knownGunsVersion + " p: " + latestPortalVersion + knownPortalVersion + " l: " + latestLaserVersion + knownLaserVersion + " t: " + latestTurretsVersion + knownTurretsVersion + " j: " + latestJukeboxVersion + knownJukeboxVersion + " u: " + latestUnrealVersion + knownUnrealVersion);

		DesnoUtils.generalNotification(context, "Log", "Log:" + " g: " + latestGunsVersion + knownGunsVersion + " p: " + latestPortalVersion + knownPortalVersion + " l: " + latestLaserVersion + knownLaserVersion + " t: " + latestTurretsVersion + knownTurretsVersion + " j: " + latestJukeboxVersion + knownJukeboxVersion + " u: " + latestUnrealVersion + knownUnrealVersion, NotificationsId.ID_DEBUG_VERSIONS);
	}
	/* ######### NOTIFICATIONS ######### */


	/* ######### ANIMATIONS ######### */
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

	public static void expandTextView(ViewGroup container, TextView tv) {
		// animation
		if (Build.VERSION.SDK_INT >= 19) {
			TransitionManager.beginDelayedTransition(container, new AutoTransition().setDuration(SharedConstants.CHANGELOG_ANIMATION_DURATION_PER_LINE * tv.getLineCount()));
		}

		// expand the TextView with setMaxLines
		tv.setMaxLines(Integer.MAX_VALUE);
	}

	public static void collapseTextView(ViewGroup container, final TextView tv, int collapsedHeight) {

		if (Build.VERSION.SDK_INT >= 19) {
			int lines = tv.getLineCount();

			// animation
			TransitionManager.beginDelayedTransition(container, new AutoTransition().setDuration(SharedConstants.CHANGELOG_ANIMATION_DURATION_PER_LINE * lines));

			// collapse the view by setting the collapsed height
			DesnoUtils.setViewHeight(tv, collapsedHeight);

			// restore initial state of the TextView when the animation finishes
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					DesnoUtils.setViewHeight(tv, ViewGroup.LayoutParams.WRAP_CONTENT);
					tv.setMaxLines(SharedConstants.CHANGELOG_TEXT_MAX_LINES);
				}
			}, SharedConstants.CHANGELOG_ANIMATION_DURATION_PER_LINE * lines + 100);
		} else {
			// no animation without the new APIs :/
			tv.setMaxLines(SharedConstants.CHANGELOG_TEXT_MAX_LINES);
		}

	}
	/* ######### ANIMATIONS ######### */


	/* ######### ADS ######### */
	private static final long MINIMUM_DELAY_FOR_NEW_AD_MILLIS = 30000;
	public static InterstitialAdStatic interstitialAdStatic;

	public static void showAd() {
		if (interstitialAdStatic != null) {
			interstitialAdStatic.show();
		}
	}

	public static class InterstitialAdStatic {

		private InterstitialAd interstitialAd;

		private long latestShowedTime;

		public InterstitialAdStatic(Context context) {

			interstitialAdStatic = this;

			latestShowedTime = 0;

			// Load InterstitialAd
			interstitialAd = new InterstitialAd(context);
			interstitialAd.setAdUnitId("ca-app-pub-4328789168608769/6477600530");

			// Set an AdListener that loads again the ad when it closes
			interstitialAd.setAdListener(new AdListener() {
				@Override
				public void onAdClosed() {
					interstitialAd.loadAd(new AdRequest.Builder().build());
				}

				@Override
				public void onAdLoaded() {
				}
			});

			// Start loading the ad now
			interstitialAd.loadAd(new AdRequest.Builder().build());
		}

		public void show() {
			// "this" object should be equal to the interstitialAd object
			if (System.currentTimeMillis() >= (latestShowedTime + MINIMUM_DELAY_FOR_NEW_AD_MILLIS)) {
				if (interstitialAd.isLoaded()) {
					interstitialAd.show();
					latestShowedTime = System.currentTimeMillis();
				}
			} else {
				Log.i(TAG, "Ads: already displayed an ad before. The next ad will be available after " + (((latestShowedTime + MINIMUM_DELAY_FOR_NEW_AD_MILLIS) - System.currentTimeMillis()) / 1000) + " seconds");
			}
		}
	}
	/* ######### ADS ######### */


	/* ######### ANALYTICS ######### */
	public static Tracker getTracker(Context context) {
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);

		DesnoUtils.updateStatisticsEnabledBool(context);

		// To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
		return analytics.newTracker("UA-55378092-6");
	}

	public static void sendScreenChange(Tracker tracker, String name) {
		if(SharedVariables.areStatisticsEnabled) {
			tracker.setScreenName("Screen~" + name);
			tracker.send(new HitBuilders.ScreenViewBuilder().build());
		} else {
			Log.i(TAG, "Analytics disabled, screen \"" + name + "\" not sent");
		}
	}

	public static void sendAction(Tracker tracker, String action) {
		if(SharedVariables.areStatisticsEnabled) {
			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("Action")
					.setAction(action)
					.build());
		} else {
			Log.i(TAG, "Analytics disabled, action \"" + action + "\" not sent");
		}
	}

	public static void sendEvent(Tracker tracker, String eventCategory, String event) {
		if(SharedVariables.areStatisticsEnabled) {
			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("Event")
					.setAction(eventCategory)
					.setLabel(event)
					.build());
		} else {
			Log.i(TAG, "Analytics disabled, event category \"" + eventCategory + "\", event \"" + event + "\" not sent");
		}
	}

	public static void updateStatisticsEnabledBool(Context context) {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedVariables.areStatisticsEnabled = sharedPrefs.getBoolean("anonymous_statistics", DefaultSettingsValues.ANONYMOUS_STATISTICS);
	}
	/* ######### ANALYTICS ######### */

}
