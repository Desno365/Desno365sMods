package com.desno365.mods.Activities;

import android.app.Activity;
import android.content.Context;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.desno365.mods.DesnoUtils;
import com.desno365.mods.Keys;
import com.desno365.mods.R;
import com.desno365.mods.Receivers.AlarmReceiver;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = "DesnoMods-SettingsActiv";

    public static Activity activity;

    private static Preference frequencyPreference;

    private static boolean monitorNotificationPrefrence;

	@Override
	public void onCreate(Bundle savedInstanceState) {
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

        Log.i(TAG, "onDestroy. Launched the new alarm.");

        // change alarmManager for notifications
        AlarmReceiver aR = new AlarmReceiver();
        aR.cancelAlarm(activity.getApplicationContext());
        aR.setAlarm(activity.getApplicationContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            //this prevent to re-create the MainActivity
            case android.R.id.home:
                this.finish();
                DesnoUtils.changeFinishAnimations(activity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        DesnoUtils.changeFinishAnimations(activity);
    }

    private static void restartDialogLanguage() {
        View mView = View.inflate(activity, R.layout.restart_popup_language_settings, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(mView);
        builder.setTitle(activity.getResources().getString(R.string.app_name));
        builder.setNeutralButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.myMainActivity.get().finish();
                System.exit(0);
            }
        });
        builder.setCancelable(false);

        android.app.AlertDialog popup = builder.create();
        popup.setCanceledOnTouchOutside(false);
        popup.show();
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragmented_preferences);

            // initialize preferences
            SharedPreferences sharedPrefs = getPreferenceScreen().getSharedPreferences();
            Preference notificationPreference = findPreference("notification_bool");
            frequencyPreference = findPreference("sync_frequency");
            Preference languagePreference = findPreference("selected_language");
            Preference helpTranslatingPreference = findPreference("help_translating");
            Preference restoreTipsPreference = findPreference("restore_tips");

            monitorNotificationPrefrence = sharedPrefs.getBoolean(notificationPreference.getKey(), true);


            // enable or disable alarm if the user want or not notifications
            notificationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    monitorNotificationPrefrence = !monitorNotificationPrefrence;

                    Log.i(TAG, "notification_bool set to " + monitorNotificationPrefrence);

                    if (monitorNotificationPrefrence)
                        frequencyPreference.setEnabled(true);
                    else
                        frequencyPreference.setEnabled(false);

                    return true;
                }
            });


            // enable or disable frequency preference at start
            if(monitorNotificationPrefrence)
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
                    DesnoUtils.changeStartAnimations(activity);
                    return false;
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

                    // restore showcaseview
                    SharedPreferences internal = activity.getApplicationContext().getSharedPreferences("showcase_internal", Context.MODE_PRIVATE);
                    internal.edit().putBoolean("hasShot" + 1, false).apply();

                    Toast.makeText(activity.getApplicationContext(), getString(R.string.restored_toast), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

        }
    }

}
