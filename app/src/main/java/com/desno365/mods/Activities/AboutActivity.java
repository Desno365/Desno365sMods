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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.desno365.mods.DesnoUtils;
import com.desno365.mods.R;
import com.desno365.mods.SharedConstants.Keys;

import java.util.Random;


public class AboutActivity extends AppCompatActivity {

	private static final String TAG = "DesnoMods-AboutActivity";

	public static Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Activity started (onCreate)");
		DesnoUtils.setSavedTheme(this);
		DesnoUtils.setSavedLanguage(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		activity = this;

		// Set up the action bar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_about); // Attaching the layout to the toolbar object
		toolbar.setNavigationIcon(R.drawable.ic_navigation_arrow_back);
		toolbar.setTitle(R.string.action_info);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.finish();
				DesnoUtils.changeFinishAnimations(activity);
			}
		});

		// Animate "Made by" TextView
        findViewById(R.id.textview_made_by).setVisibility(View.INVISIBLE);
		new android.os.Handler().postDelayed(new Runnable() {
			public void run() {

				findViewById(R.id.textview_made_by).setVisibility(View.VISIBLE);

				int random = new Random().nextInt(5); // max exclusive: 3, so: 0, 1, 2
				YoYo.AnimationComposer anim;
				switch (random) {
					case 0:
						anim = YoYo.with(Techniques.DropOut);
						break;
					case 2:
						anim = YoYo.with(Techniques.SlideInDown);
                        break;
					default:
						anim = YoYo.with(Techniques.DropOut);
				}
				anim.duration(1500);
				anim.playOn(findViewById(R.id.textview_made_by));

			}
		}, 400);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		DesnoUtils.showAd();
	}

	@Override
	public void onBackPressed() {
		this.finish();
		DesnoUtils.changeFinishAnimations(activity);
	}

	@Override
	public void startActivity(Intent intent) {
		try {
			super.startActivity(intent);
			DesnoUtils.changeStartAnimations(activity);
		} catch (ActivityNotFoundException e1) {
			Log.e(TAG, "Start activity failed for the first time.", e1);

			try {
				super.startActivity(intent);
				DesnoUtils.changeStartAnimations(activity);
			} catch (ActivityNotFoundException e2) {
				Log.e(TAG, "Start activity failed for the second and last time.", e2);
				Toast.makeText(activity.getApplicationContext(), "Error: can't start the Activity, please try again and make sure you have a Internet browser installed.", Toast.LENGTH_LONG).show();
			}
		}
	}

	public void onViewClick(View v) {
		switch (v.getId()) {

			// twitter image and text
			case R.id.twitter_layout:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_MY_TWITTER)));
				break;

			// github image and text
			case R.id.github_layout:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_APP_GITHUB)));
				break;

			// google play image and text
			case R.id.google_play_layout:
				final String appPackageName = getPackageName();
				try {
					//play store installed
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					//play store not installed
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
				}
		}
	}
}

