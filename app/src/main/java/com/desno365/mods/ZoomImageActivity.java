package com.desno365.mods;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ZoomImageActivity extends Activity implements OnShowcaseEventListener {

    private PhotoViewAttacher mAttacher;
    private ShowcaseView mShowcase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeHoloDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);

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
        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (mShowcase.isShown())
                    mShowcase.hide();
            }
        });

        mShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(new ViewTarget(R.id.iv_photo, this))
                .setContentTitle(getString(R.string.zoom_image_showcase_title))
                .setContentText(getString(R.string.zoom_image_showcase_content))
                .singleShot(1)
                .build();

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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
    }

}