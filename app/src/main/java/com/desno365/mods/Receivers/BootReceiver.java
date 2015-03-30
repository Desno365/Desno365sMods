package com.desno365.mods.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "DesnoMods-BootReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.d(TAG, "BOOT_COMPLETED received correctly.");

            //load alarmManager for notifications
            AlarmReceiver aR = new AlarmReceiver();
            aR.cancelAlarm(context);
            aR.setAlarm(context);
        } else {
            Log.e(TAG, "Received unexpected intent " + intent.toString());
        }
    }

}
