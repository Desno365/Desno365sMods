package com.desno365.mods.Activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.desno365.mods.Receivers.AlarmReceiver;
import com.desno365.mods.DesnoUtils;
import com.desno365.mods.Keys;
import com.desno365.mods.R;

public class SettingsActivity extends PreferenceActivity {

    public static Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        DesnoUtils.setSavedTheme(this);
        DesnoUtils.setSavedLanguage(this);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_settings);

        activity = this;

        SharedPreferences sharedPrefs = getPreferenceScreen().getSharedPreferences();
        final Preference notificationPreference = findPreference("notification_bool");
        final Preference frequencyPreference = findPreference("sync_frequency");
        final Preference languagePreference = findPreference("selected_language");
        final Preference themePreference = findPreference("selected_theme");


        // enable or disable alarm if the user want or not notifications
        notificationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (preference.getSharedPreferences().getBoolean("notification_bool", true))
                    frequencyPreference.setEnabled(true);
                else
                    frequencyPreference.setEnabled(false);

                // change alarmManager for notifications
                AlarmReceiver aR = new AlarmReceiver();
                aR.cancelAlarm(getApplicationContext());
                aR.setAlarm(getApplicationContext());

                return true;
            }
        });


        // enable or disable frequency preference at start
        if(sharedPrefs.getBoolean("notification_bool", true))
            frequencyPreference.setEnabled(true);
        else
            frequencyPreference.setEnabled(false);

        // change alarm when frequency preference has been changed
        frequencyPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                // change alarmManager for notifications
                AlarmReceiver aR = new AlarmReceiver();
                aR.cancelAlarm(getApplicationContext());
                aR.setAlarm(getApplicationContext());

                return true;
            }
        });


        // open popup when language preference is changed
        languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                restartDialogLanguage();
                return true;
            }
        });


        // open popup when theme preference is changed
        themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                restartDialogTheme();
                return true;
            }
        });


        @SuppressLint("AppCompatMethod")
        ActionBar actionBar = this.getActionBar();
        assert actionBar != null;
        actionBar.setTitle(getApplicationContext().getResources().getString(R.string.action_settings));

        //set if the user can click the icon
        actionBar.setHomeButtonEnabled(true);

        //when clicking the icon return to the parent activity (specified in AndroidManifest.xml) and display arrow
        actionBar.setDisplayHomeAsUpEnabled(true);

        // help translating preference
        Preference myPref1 = findPreference("help_translating");
        myPref1.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_APP_TRANSLATIONS)));
                DesnoUtils.changeStartAnimations(activity);
                return false;
            }
        });

        // restore suggestions preference
        Preference myPref2 = findPreference("restore_tips");
        myPref2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // restore tooltip
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean("user_understood_full_resolution_help", false);
                editor.apply();

                // restore showcaseview
                SharedPreferences internal = getApplicationContext().getSharedPreferences("showcase_internal", Context.MODE_PRIVATE);
                internal.edit().putBoolean("hasShot" + 1, false).apply();

                Toast.makeText(getApplicationContext(), getString(R.string.restored_toast), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
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

    private void restartDialogTheme() {
        View mView = View.inflate(this, R.layout.restart_popup_theme_settings, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(mView);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setNeutralButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
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

    private void restartDialogLanguage() {
        View mView = View.inflate(this, R.layout.restart_popup_language_settings, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(mView);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setNeutralButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
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

}
