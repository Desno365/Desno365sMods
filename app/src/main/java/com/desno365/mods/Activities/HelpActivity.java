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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.desno365.mods.DesnoUtils;
import com.desno365.mods.R;
import com.desno365.mods.SharedConstants.Keys;

import it.sephiroth.android.library.tooltip.TooltipManager;


public class HelpActivity extends Activity {

	private static final String TAG = "DesnoMods-HelpActivity";

	public static Activity activity;

	private TooltipManager mTooltip;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Activity started (onCreate)");
		DesnoUtils.setSavedLanguage(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		activity = this;

		// Set up the action bar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_help); // Attaching the layout to the toolbar object
		toolbar.setTitle(R.string.action_help);
		toolbar.setNavigationIcon(R.drawable.ic_navigation_arrow_back);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.finish();
				DesnoUtils.changeFinishAnimations(activity);
			}
		});

		// tooltip at start (only the first time)
		mTooltip = TooltipManager.getInstance(this);
		new android.os.Handler().postDelayed(new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DisplayMetrics metrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(metrics);
						mTooltip.create(1)
								.anchor(findViewById(R.id.help_image_prepare1), TooltipManager.Gravity.TOP)
								.closePolicy(TooltipManager.ClosePolicy.TouchOutside, 10000)
								.text(getResources().getString(R.string.click_image_to_view))
								.maxWidth((metrics.widthPixels) / 10 * 9)
								.withStyleId(R.style.ToolTipStyle)
								.withCustomView(R.layout.tooltip_textview, false)
								.show();

						// don't show the tooltip if the user has already learned to view the full resolution image
						if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("user_understood_full_resolution_help", false)) {
							mTooltip.hide(1);
							mTooltip.remove(1);
						}
					}
				});
			}
		}, 200);

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

			//minecraft image and text
			case R.id.minecraft_image:
			case R.id.minecraft_text:
				try {
					//play store installed
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_INSTALLED + Keys.KEY_PACKAGE_MINECRAFT)));
				} catch (android.content.ActivityNotFoundException anfe) {
					//play store not installed
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_NOT_INSTALLED + Keys.KEY_PACKAGE_MINECRAFT)));
				}
				break;

			//blocklauncher image and text
			case R.id.blocklauncher_image:
			case R.id.blocklauncher_text:
				try {
					//play store installed
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_INSTALLED + Keys.KEY_PACKAGE_BLOCKLAUNCHER)));
				} catch (android.content.ActivityNotFoundException anfe) {
					//play store not installed
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_NOT_INSTALLED + Keys.KEY_PACKAGE_BLOCKLAUNCHER)));
				}
				break;

			//file manager image and text
			case R.id.file_manager_image:
			case R.id.file_manager_text:
				try {
					//play store installed
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_INSTALLED_FILE_MANAGER)));
				} catch (android.content.ActivityNotFoundException anfe) {
					//play store not installed
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_NOT_INSTALLED_FILE_MANAGER)));
				}
				break;
		}
	}

	public void onImageClick(View v) {

		// starting the zoomImage activity (it has a switch case for the id passed to the intent)
		Intent i = new Intent(this, ZoomImageActivity.class);
		i.putExtra("viewId", v.getId());
		startActivity(i);

		// after the first time opening a full resolution image the user doesn't need the tooltip anymore
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putBoolean("user_understood_full_resolution_help", true);
		editor.apply();

	}

}
