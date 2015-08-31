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

package com.desno365.mods.Activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.desno365.mods.DesnoUtils;

@SuppressLint("Registered") // Activity is not registered in the manifest
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        DesnoUtils.setSavedTheme(this);
        DesnoUtils.setSavedLanguage(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        DesnoUtils.changeFinishAnimations(this);
    }

    @Override
    public void startActivity(Intent intent) {
        try {
            super.startActivity(intent);
            DesnoUtils.changeStartAnimations(this);
        } catch (ActivityNotFoundException e1) {
            Log.e(TAG, "Start activity failed for the first time.", e1);

            try {
                super.startActivity(intent);
                DesnoUtils.changeStartAnimations(this);
            } catch (ActivityNotFoundException e2) {
                Log.e(TAG, "Start activity failed for the second and last time.", e2);
                Toast.makeText(this, "Error: can't start the Activity. One common cause of this error may be the absence or the unavailability of an Internet browser.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
