package com.desno365.mods.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.desno365.mods.DesnoUtils;
import com.desno365.mods.Keys;
import com.desno365.mods.R;

import it.sephiroth.android.library.tooltip.TooltipManager;

public class HelpActivity extends Activity {

    private static final String TAG = "DesnoMods-HelpActivity";

    public static Activity activity;

    private TooltipManager mTooltip;

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
                                .withStyleId(R.style.ToolTipTheme)
                                .show();

                        // don't show the tooltip if the user has already learned to view the full resolution image
                        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("user_understood_full_resolution_help", false)) {
                            mTooltip.hide(1);
                            mTooltip.remove(1);
                        }
                    }
                });
            }
        }, 200);

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

    public void onViewClick(View v) {
        switch (v.getId()) {

            //minecraft image and text
            case R.id.minecraft_image:
            case R.id.minecraft_text:
                try {
                    //play store installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_INSTALLED + Keys.KEY_PACKAGE_MINECRAFT)));
                    DesnoUtils.changeStartAnimations(activity);
                } catch (android.content.ActivityNotFoundException anfe) {
                    //play store not installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_NOT_INSTALLED + Keys.KEY_PACKAGE_MINECRAFT)));
                    DesnoUtils.changeStartAnimations(activity);
                }
                break;

            //blocklauncher image and text
            case R.id.blocklauncher_image:
            case R.id.blocklauncher_text:
                try {
                    //play store installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_INSTALLED + Keys.KEY_PACKAGE_BLOCKLAUNCHER)));
                    DesnoUtils.changeStartAnimations(activity);
                } catch (android.content.ActivityNotFoundException anfe) {
                    //play store not installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_NOT_INSTALLED + Keys.KEY_PACKAGE_BLOCKLAUNCHER)));
                    DesnoUtils.changeStartAnimations(activity);
                }
                break;

            //file manager image and text
            case R.id.file_manager_image:
            case R.id.file_manager_text:
                try {
                    //play store installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_INSTALLED_FILE_MANAGER)));
                    DesnoUtils.changeStartAnimations(activity);
                } catch (android.content.ActivityNotFoundException anfe) {
                    //play store not installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_PLAY_STORE_NOT_INSTALLED_FILE_MANAGER)));
                    DesnoUtils.changeStartAnimations(activity);
                }
                break;
        }
    }

    public void onImageClick(View v) {

        // starting the zoomImage activity (it has a switch case for the id passed to the intent)
        Intent i = new Intent(this, ZoomImageActivity.class);
        i.putExtra("viewId", v.getId());
        startActivity(i);
        DesnoUtils.changeStartAnimations(activity);

        // after the first time opening a full resolution image the user doesn't need the tooltip anymore
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean("user_understood_full_resolution_help", true);
        editor.apply();

    }

}
