/*
 *
 * Copyright 2017 Dennis Motta
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

	private AppCompatActivity activity;

	private PhotoViewAttacher mAttacher;

	// analytics tracker
	private Tracker mTracker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Activity started (onCreate)");
		super.onCreate(savedInstanceState);

		activity = this;

		setContentView(R.layout.activity_zoom_image);



		// Start Google Analytics
		AnalyticsApplication application = (AnalyticsApplication) getApplication();
		mTracker = application.getDefaultTracker();

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
			case R.id.imageview_help_download_app:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_download_button_app_fullres);
				break;
			case R.id.imageview_help_download_website:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_download_website_fullres);
				break;
			case R.id.imageview_help_installation_manage_modpe:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_installation_manage_modpe_scripts_fullres);
				break;
			case R.id.imageview_help_installation_import:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_installation_import_fullres);
				break;
			case R.id.imageview_help_installation_import_local_storage:
				//noinspection deprecation
				mDrawable = getResources().getDrawable(R.drawable.img_help_installation_import_from_local_storage_fullres);
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
	public void onResume() {
		super.onResume();
		// send screen change
		DesnoUtils.sendScreenChange(mTracker, TAG);
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