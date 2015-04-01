package com.desno365.mods.Activities;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.desno365.mods.DesnoUtils;
import com.desno365.mods.R;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ZoomImageActivity extends Activity {

    public static Activity activity;

    private PhotoViewAttacher mAttacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        DesnoUtils.setSavedLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);

        activity = this;


        // Set up the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_zoom_image); // Attaching the layout to the toolbar object
        toolbar.setTitle(R.string.zoom_image_showcase_title);
        toolbar.setNavigationIcon(R.drawable.ic_navigation_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                DesnoUtils.changeFinishAnimations(activity);
            }
        });


        Drawable mDrawable;
        switch (getIntent().getIntExtra("viewId", 365)) {
            case R.id.help_image_prepare1:
                mDrawable = getResources().getDrawable(R.drawable.help_download_mod_example_fullres);
                break;
            case R.id.help_image_prepare2:
                mDrawable = getResources().getDrawable(R.drawable.help_open_archive_fullres);
                break;
            case R.id.help_image_prepare3:
                mDrawable = getResources().getDrawable(R.drawable.help_extract_fullres);
                break;
            case R.id.help_image_prepare4:
                mDrawable = getResources().getDrawable(R.drawable.help_after_extraction_fullres);
                break;
            case R.id.help_image_script1:
                mDrawable = getResources().getDrawable(R.drawable.help_manage_modpe_scripts_fullres);
                break;
            case R.id.help_image_script2:
                mDrawable = getResources().getDrawable(R.drawable.help_import_fullres);
                break;
            case R.id.help_image_script3:
                mDrawable = getResources().getDrawable(R.drawable.help_script_from_local_storage_fullres);
                break;
            case R.id.help_image_texture_pack1:
                mDrawable = getResources().getDrawable(R.drawable.help_launcher_options_fullres);
                break;
            case R.id.help_image_texture_pack2:
                mDrawable = getResources().getDrawable(R.drawable.help_texture_pack_fullres);
                break;
            case R.id.help_image_texture_pack3:
                mDrawable = getResources().getDrawable(R.drawable.help_select_texture_pack_fullres);
                break;
            default:
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
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

}