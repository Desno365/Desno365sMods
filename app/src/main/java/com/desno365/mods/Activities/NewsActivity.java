package com.desno365.mods.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.desno365.mods.DesnoUtils;
import com.desno365.mods.R;
import com.desno365.mods.SwipeLayout;

public class NewsActivity extends Activity {

    private static final String TAG = "DesnoMods-NewsActivity";

    public static Activity activity;
    private Menu optionsMenu;
    private SwipeRefreshLayout swipeLayout;

    public void onCreate(Bundle savedInstanceState) {
        DesnoUtils.setSavedLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        activity = this;

        // Set up the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_news); // Attaching the layout to the toolbar object
        toolbar.setTitle(R.string.news_title);
        toolbar.setNavigationIcon(R.drawable.ic_navigation_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                DesnoUtils.changeFinishAnimations(activity);
            }
        });

        // Set up the SwipeRefreshLayout
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_news);
        swipeLayout.setColorSchemeResources(R.color.minecraft_dirt_light, R.color.minecraft_dirt_green);
        swipeLayout.setOnRefreshListener(new SwipeLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // download news
            }
        });

    }

    @Override
    public void onBackPressed() {
        this.finish();
        DesnoUtils.changeFinishAnimations(activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //save the menu in a variable
        this.optionsMenu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_activity, menu);

        //refresh content on start
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPrefs.getBoolean("refresh_on_start", true)) {
            new android.os.Handler().postDelayed(new Runnable(){
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                // download news
                            } catch (Exception err) {
                                Log.e(TAG, "Exception in runOnUiThread() in onCreate() ", err);
                            }
                        }
                    });
                }
            }, 1000);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            // this prevent to re-create the MainActivity
            case android.R.id.home:
                this.finish();
                DesnoUtils.changeFinishAnimations(activity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setRefreshState(final boolean refreshing) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (swipeLayout != null) {
                    swipeLayout.setRefreshing(refreshing);
                }
            }
        });

        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu.findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                        }
                    });
                }else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

}
