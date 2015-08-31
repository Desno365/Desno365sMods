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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.desno365.mods.AnalyticsApplication;
import com.desno365.mods.DesnoUtils;
import com.desno365.mods.R;
import com.google.android.gms.analytics.Tracker;

import uk.co.senab.photoview.PhotoViewAttacher;


public class ZoomImageActivity extends BaseActivity {

	private static final String TAG = "ZoomImageActivity";

	public static AppCompatActivity activity;

	private PhotoViewAttacher mAttacher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Activity started (onCreate)");
		super.onCreate(savedInstanceState);

		activity = this;

		setContentView(R.layout.activity_zoom_image);



		// Start Google Analytics
		AnalyticsApplication application = (AnalyticsApplication) getApplication();
		Tracker mTracker = application.getDefaultTracker();

		// Send screen change
		DesnoUtils.sendScreenChange(mTracker, "ZoomImageActivity");

		// Set up the action bar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_zoom_image); // Attaching the layout to the toolbar object
		toolbar.setTitle(R.string.zoom_image_showcase_title);
		toolbar.setNavigationIcon(R.drawable.ic_navigation_arrow_back);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.supportFinishAfterTransition();
				DesnoUtils.changeFinishAnimations(activity);
			}
		});


		Drawable mDrawable;
		switch (getIntent().getIntExtra("viewId", 365)) {
			case R.id.help_image_prepare1:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_download_mod_example_fullres);
				break;
			case R.id.help_image_prepare2:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_open_archive_fullres);
				break;
			case R.id.help_image_prepare3:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_extract_fullres);
				break;
			case R.id.help_image_prepare4:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_after_extraction_fullres);
				break;
			case R.id.help_image_script1:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_manage_modpe_scripts_fullres);
				break;
			case R.id.help_image_script2:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_import_fullres);
				break;
			case R.id.help_image_script3:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_script_from_local_storage_fullres);
				break;
			case R.id.help_image_texture_pack1:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_launcher_options_fullres);
				break;
			case R.id.help_image_texture_pack2:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_texture_pack_fullres);
				break;
			case R.id.help_image_texture_pack3:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_select_texture_pack_fullres);
				break;
			default:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.ic_launcher);
				break;
		}

		ImageView mImageView = (ImageView) findViewById(R.id.iv_photo);
		mImageView.setImageDrawable(mDrawable);

		// loading PhotoView library
		mAttacher = new PhotoViewAttacher(mImageView);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Need to call clean-up
		mAttacher.cleanup();
	}

	@Override
	public void onBackPressed() {
		supportFinishAfterTransition();
		DesnoUtils.changeFinishAnimations(activity);
	}

}