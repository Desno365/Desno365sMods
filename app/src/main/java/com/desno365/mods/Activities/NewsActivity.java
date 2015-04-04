package com.desno365.mods.Activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.desno365.mods.DesnoUtils;
import com.desno365.mods.Keys;
import com.desno365.mods.R;
import com.desno365.mods.SwipeLayout;

public class NewsActivity extends ActionBarActivity {

    private static final String TAG = "DesnoMods-NewsActivity";

    public static Activity activity;
    private Toolbar toolbar;
    private Menu optionsMenu;
    private SwipeRefreshLayout swipeLayout;

    public static String newsContent;

    private boolean isRefreshing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        DesnoUtils.setSavedLanguage(this);
        super.onCreate(savedInstanceState);

        activity = this;

        newsContent = getResources().getString(R.string.loading);

        // set content of the activity
        setContentView(R.layout.activity_news);


        // Set up the action bar.
        toolbar = (Toolbar) findViewById(R.id.tool_bar_news); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with setSupportActionBar() call
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
                startRefreshingNews();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        toolbar.setTitle(R.string.news_title);
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
        new android.os.Handler().postDelayed(new Runnable(){
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            startRefreshingNews();
                        } catch (Exception err) {
                            Log.e(TAG, "Exception in runOnUiThread() in onCreateOptionsMenu() ", err);
                        }
                    }
                });
            }
        }, 500);

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

            case R.id.action_news_refresh:
                startRefreshingNews();
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
            final MenuItem refreshItem = optionsMenu.findItem(R.id.action_news_refresh);
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

    private void startRefreshingNews() {
        if(!isRefreshing) {
            isRefreshing = true;
            setRefreshState(true);
            RetrieveNewsContent downloadTask = new RetrieveNewsContent();
            downloadTask.execute((Void) null);
        }
    }

    // refresh TextViews after the content has been refreshed
    private void refreshTextViews() {
        activity.runOnUiThread(new Runnable() {
            public void run() {

                try {
                    TextView newsText = (TextView) getWindow().getDecorView().findViewById(R.id.news_container);
                    newsText.setText(android.text.Html.fromHtml(newsContent));
                } catch (Exception err) {
                    Log.e(TAG, "Exception in refreshTextViews() in news ", err);
                }

            }
        });
    }

    public class RetrieveNewsContent extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if(DesnoUtils.isNetworkAvailable(getApplicationContext())) {
                newsContent = DesnoUtils.getTextFromUrl(Keys.KEY_NEWS);
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity.getApplicationContext(), getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.i(TAG, "onPostExecute now, the AsyncTask for the news finished loading.");
            refreshTextViews();
            setRefreshState(false);
            isRefreshing = false;
        }

    }

}
