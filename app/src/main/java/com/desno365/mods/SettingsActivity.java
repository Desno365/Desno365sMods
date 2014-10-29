package com.desno365.mods;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {

    public static Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        DesnoUtils.setSavedTheme(this);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_settings);

        activity = this;

        SharedPreferences sharedPrefs = getPreferenceScreen().getSharedPreferences();
        final Preference frequencyPreference = findPreference("sync_frequency");

        if(sharedPrefs.getBoolean("notification_bool", true))
            frequencyPreference.setEnabled(true);
        else
            frequencyPreference.setEnabled(false);

        sharedPrefs.registerOnSharedPreferenceChangeListener(new android.content.SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("notification_bool")) {
                    if (sharedPreferences.getBoolean("notification_bool", true))
                        frequencyPreference.setEnabled(true);
                    else
                        frequencyPreference.setEnabled(false);
                    //load alarmManager for notifications
                    AlarmReceiver aR = new AlarmReceiver();
                    aR.cancelAlarm(getApplicationContext());
                    aR.setAlarm(getApplicationContext());
                }

                if (key.equals("sync_frequency")) {
                    //load alarmManager for notifications
                    AlarmReceiver aR = new AlarmReceiver();
                    aR.cancelAlarm(getApplicationContext());
                    aR.setAlarm(getApplicationContext());
                }

                if (key.equals("selected_theme")) {
                    restartDialog();
                }
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

        Preference myPref = findPreference("restore_tips");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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

    private void restartDialog() {
        View mView = View.inflate(this, R.layout.restart_popup_settings, null);

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
