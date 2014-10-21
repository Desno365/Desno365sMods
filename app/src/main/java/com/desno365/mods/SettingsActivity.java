package com.desno365.mods;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_settings);

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
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            //this prevent to re-create the MainActivity
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
