package com.desno365.mods.Activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.desno365.mods.DesnoUtils;
import com.desno365.mods.Keys;
import com.desno365.mods.R;

public class AboutActivity extends Activity {

    public static Activity activity;

    public void onCreate(Bundle savedInstanceState) {
        DesnoUtils.setSavedTheme(this);
        DesnoUtils.setSavedLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        activity = this;

        @SuppressLint("AppCompatMethod")
        ActionBar actionBar = this.getActionBar();
        assert actionBar != null;
        actionBar.setTitle(getApplicationContext().getResources().getString(R.string.action_info));

        //set if the user can click the icon
        actionBar.setHomeButtonEnabled(true);

        //when clicking the icon return to the parent activity (specified in AndroidManifest.xml)
        actionBar.setDisplayHomeAsUpEnabled(true);

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

