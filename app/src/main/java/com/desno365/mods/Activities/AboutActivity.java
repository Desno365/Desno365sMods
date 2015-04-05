package com.desno365.mods.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.desno365.mods.DesnoUtils;
import com.desno365.mods.Keys;
import com.desno365.mods.R;

public class AboutActivity extends Activity {

    private static final String TAG = "DesnoMods-AboutActivity";

    public static Activity activity;

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Activity started (onCreate)");
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

            // twitter image and text
            case R.id.twitter_image: case R.id.twitter_text:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_MY_TWITTER)));
                DesnoUtils.changeStartAnimations(activity);
                break;

            // github image and text
            case R.id.github_image: case R.id.github_text:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Keys.KEY_APP_GITHUB)));
                DesnoUtils.changeStartAnimations(activity);
                break;

            // google play image and text
            case R.id.google_play_image:case R.id.google_play_text:
                final String appPackageName = getPackageName();
                try {
                    //play store installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    DesnoUtils.changeStartAnimations(activity);
                } catch (android.content.ActivityNotFoundException anfe) {
                    //play store not installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                    DesnoUtils.changeStartAnimations(activity);
                }
        }
    }
}

