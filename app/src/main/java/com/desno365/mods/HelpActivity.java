package com.desno365.mods;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;

import it.sephiroth.android.library.tooltip.TooltipManager;

public class HelpActivity extends Activity {

    private TooltipManager mTooltip;

    public void onCreate(Bundle savedInstanceState) {
        DesnoUtils.setSavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        @SuppressLint("AppCompatMethod")
        ActionBar actionBar = this.getActionBar();
        assert actionBar != null;
        actionBar.setTitle(getApplicationContext().getResources().getString(R.string.action_help));

        //set if the user can click the icon
        actionBar.setHomeButtonEnabled(true);

        //when clicking the icon return to the parent activity (specified in AndroidManifest.xml)
        actionBar.setDisplayHomeAsUpEnabled(true);

        // tooltip at start
        mTooltip = TooltipManager.getInstance(this);
        new android.os.Handler().postDelayed(new Runnable(){
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
                                .show();
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onViewClick(View v) {
        switch (v.getId()) {

            //minecraft image and text
            case R.id.minecraft_image:
            case R.id.minecraft_text:
                try {
                    //play store installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mojang.minecraftpe")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    //play store not installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.mojang.minecraftpe")));
                }
                break;

            //blocklauncher image and text
            case R.id.blocklauncher_image:
            case R.id.blocklauncher_text:
                try {
                    //play store installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=net.zhuoweizhang.mcpelauncher")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    //play store not installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=net.zhuoweizhang.mcpelauncher")));
                }
                break;

            //file manager image and text
            case R.id.file_manager_image:
            case R.id.file_manager_text:
                try {
                    //play store installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=file%20explorer")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    //play store not installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=file%20explorer")));
                }
                break;
        }
    }

    public void onImageClick(View v) {
        Intent i = new Intent(this, ZoomImageActivity.class);
        i.putExtra("viewId", v.getId());
        startActivity(i);
    }

}
