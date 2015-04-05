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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.desno365.mods.CardNews;
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
	private LinearLayout cardsContainer;

	private boolean isRefreshing = false;
	private boolean newsCorrectlyDownloaded = false;
    private int numberOfNews;
    private String[] newsTitles;
    private String[] newsContents;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Activity started (onCreate)");
		DesnoUtils.setSavedLanguage(this);
		super.onCreate(savedInstanceState);

		activity = this;

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

		cardsContainer = (LinearLayout) findViewById(R.id.cards_container);

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

	private void createCards(final int cardsNumber, final String[] titles, final String[] contents) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				for (int i = 0; i < cardsNumber; i++) {
					CardNews card = new CardNews(activity.getApplicationContext(), titles[i], contents[i]);
					card.getPARENT().setAnimation(getIntroSet(1000, 0));

					cardsContainer.addView(card.getPARENT());
					card.getPARENT().animate();
				}
			}
		});
	}

	private AnimationSet getIntroSet(int duration, int startOffset) {
		AlphaAnimation animation1 = new AlphaAnimation(0, 1);

		TranslateAnimation animation2 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_PARENT, -1,
				Animation.RELATIVE_TO_SELF, 0);

		final AnimationSet set = new AnimationSet(false);
		set.addAnimation(animation1);
		set.addAnimation(animation2);
		set.setDuration(duration);
		set.setStartOffset(startOffset);

		return set;
	}

	private synchronized AnimationSet getOutroSet(int duration, int startOffset) {
		AlphaAnimation animation1 = new AlphaAnimation(1, 0);

		TranslateAnimation animation2 = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0,
				Animation.RELATIVE_TO_PARENT, 0,
				Animation.RELATIVE_TO_PARENT, 0,
				Animation.RELATIVE_TO_PARENT, 1);

		final AnimationSet set = new AnimationSet(false);
		set.addAnimation(animation1);
		set.addAnimation(animation2);
		set.setDuration(duration);
		set.setStartOffset(startOffset);

		return set;
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

			int count = cardsContainer.getChildCount();
			if(count == 0) {
				RetrieveNewsContent downloadTask = new RetrieveNewsContent();
				downloadTask.execute((Void) null);
			} else {
				final View lastChild = cardsContainer.getChildAt(count - 1);
				for (int i = 0; i < count; i++) {
					final View v = cardsContainer.getChildAt(i);
					AnimationSet set = getOutroSet(500, (count - 1 - i) * 200);
					set.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							cardsContainer.post(new Runnable() {
								@Override
								public void run() {
									cardsContainer.removeView(v);
									if (v == lastChild) {
										RetrieveNewsContent downloadTask = new RetrieveNewsContent();
										downloadTask.execute((Void) null);
									}
								}
							});
						}

						@Override
						public void onAnimationRepeat(Animation animation) {

						}
					});
					v.startAnimation(set);
				}
			}
		}
	}

	public class RetrieveNewsContent extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			try {
				if(DesnoUtils.isNetworkAvailable(getApplicationContext())) {
					String downloadedNews = DesnoUtils.getTextFromUrl(Keys.KEY_NEWS);

                    String[] cardsText = downloadedNews.split("<endcontent>");
                    numberOfNews = cardsText.length;
                    newsTitles = new String[numberOfNews];
                    newsContents = new String[numberOfNews];
                    for (int i = 0; i < numberOfNews; i++) {
                        String[] titleAndContent = cardsText[i].split("<endtitle>");
                        newsTitles[i] = titleAndContent[0]; // 0 = title
                        newsContents[i] = titleAndContent[1]; // 1 = content
                    }

                    newsCorrectlyDownloaded = newsTitles.length >= numberOfNews && newsContents.length >= numberOfNews;
				} else {
					newsCorrectlyDownloaded = false;
				}
			} catch (Exception err) {
				newsCorrectlyDownloaded = false;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			Log.i(TAG, "onPostExecute now, the AsyncTask for the news finished loading with result " + (newsCorrectlyDownloaded ? "successful" : "not successful"));

			if(newsCorrectlyDownloaded) {
				findViewById(R.id.news_loading_text).setVisibility(View.GONE);
                createCards(numberOfNews, newsTitles, newsContents);
			} else {
				findViewById(R.id.news_loading_text).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.news_loading_text)).setText(getResources().getString(R.string.internet_error));
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(activity.getApplicationContext(), getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
					}
				});
			}

            setRefreshState(false);
            isRefreshing = false;
		}

	}

}