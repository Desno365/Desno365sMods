/*
 * Copyright 2015 Dennis Motta
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

import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.desno365.mods.AnalyticsApplication;
import com.desno365.mods.DesnoUtils;
import com.desno365.mods.NewsCard;
import com.desno365.mods.NewsSwipeRefreshLayout;
import com.desno365.mods.R;
import com.desno365.mods.SharedConstants.Keys;
import com.desno365.mods.SharedConstants.SharedConstants;
import com.google.android.gms.analytics.Tracker;


public class NewsActivity extends AppCompatActivity {

	private static final String TAG = "DesnoMods-NewsActivity";

	public static AppCompatActivity activity;
	private Toolbar toolbar;
	private Menu optionsMenu;
	private NewsSwipeRefreshLayout swipeLayout;
	private LinearLayout cardsContainer;

	private boolean firstRefresh = true;
	private boolean isRefreshing = false;
	private boolean newsCorrectlyDownloaded = false;
	private int numberOfNews;
	private String[] newsTitles;
	private String[] newsContents;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Activity started (onCreate)");
		DesnoUtils.setSavedTheme(this);
		DesnoUtils.setSavedLanguage(this);
		super.onCreate(savedInstanceState);

		activity = this;

		setContentView(R.layout.activity_news);



		// Starting Google Analytics
		AnalyticsApplication application = (AnalyticsApplication) getApplication();
		Tracker mTracker = application.getDefaultTracker();

		// Send screen change
		DesnoUtils.sendScreenChange(mTracker, "NewsActivity");

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

		// Set up the NewSwipeRefreshLayout
		swipeLayout = (NewsSwipeRefreshLayout) findViewById(R.id.swipe_container_news);
		swipeLayout.setOnRefreshListener(new NewsSwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				startRefreshingNews();
			}
		});
		TypedArray a = getTheme().obtainStyledAttributes(new int[]{R.attr.color_primary, R.attr.color_accent});
		int color1 = a.getColor(0, 0);
		int color2 = a.getColor(1, 0);
		a.recycle();
		swipeLayout.setColorSchemeColors(color1, color2);

		cardsContainer = (LinearLayout) findViewById(R.id.cards_container);

	}

	@Override
	public void onBackPressed() {
		this.finish();
		DesnoUtils.changeFinishAnimations(activity);
	}

	@Override
	public void onResume() {
		super.onResume();

		toolbar.setTitle(R.string.news_title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//save the menu in a variable
		this.optionsMenu = menu;

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_news_activity, menu);

		//refresh content on start
		new android.os.Handler().postDelayed(new Runnable() {
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
					NewsCard card = new NewsCard(activity.getApplicationContext(), activity.getLayoutInflater(), titles[i], contents[i]);
					card.getPARENT().setAnimation(getIntroSet(750 + (25 * cardsNumber), 0));

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
				Animation.RELATIVE_TO_PARENT, 0.5f);

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
				} else {
					refreshItem.setActionView(null);
				}
			}
		}
	}

	private void startRefreshingNews() {
		if (!isRefreshing) {
			isRefreshing = true;
			setRefreshState(true);

			int count = cardsContainer.getChildCount();
			if (count == 0) {
				// no need to animate cards
				RetrieveNewsContent downloadTask = new RetrieveNewsContent();
				downloadTask.execute((Void) null);
			} else {
				// start exit animation
				final View lastChild = cardsContainer.getChildAt(0);
				for (int i = 0; i < count; i++) {
					final View v = cardsContainer.getChildAt(i);
					int invertedVerticalPosition = count - i;
					AnimationSet set;
					if(count <= 15) {
						// not synchronized animation
						// ((1500 / count) + 50) = base offset
						// formula to calculate the total offset of the animations based on the number of cards (x):
						// (((1500 / count) + 50) * x --> 1500 + 50x milliseconds
						set = getOutroSet(500, (invertedVerticalPosition - 1) * ((1500 / count) + 50));
					} else {
						// synchronized animation, all the cards move together
						set = getOutroSet(750 + (25 * count), 0);
					}
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
				if (DesnoUtils.isNetworkAvailable(getApplicationContext())) {

					long startTime = System.currentTimeMillis();

					// START
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
					// END

					long endTime = System.currentTimeMillis();
					long time = (endTime - startTime);

					Log.i(TAG, "Getting news took " + time + " milliseconds");
					if(firstRefresh && time < SharedConstants.SHAPELOADINGVIEW_MIN_TIME_DISPLAYING) {
						firstRefresh = false;
						try {
							// if necessary the asynctask will stop so the user can see at least the first half second of the animation of the ShapeLoadingView
							Thread.sleep((SharedConstants.SHAPELOADINGVIEW_MIN_TIME_DISPLAYING - time) + 100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

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

			if (newsCorrectlyDownloaded) {
				findViewById(R.id.news_load_view).setVisibility(View.GONE);
				findViewById(R.id.news_error_loading_text).setVisibility(View.GONE);
				createCards(numberOfNews, newsTitles, newsContents);
			} else {
				findViewById(R.id.news_load_view).setVisibility(View.GONE);
				findViewById(R.id.news_error_loading_text).setVisibility(View.VISIBLE);
				runOnUiThread(new Runnable() {
					public void run() {
						DesnoUtils.showDefaultSnackbar(activity.findViewById(R.id.scroll_news), R.string.internet_error);
					}
				});
			}

			setRefreshState(false);
			isRefreshing = false;
		}

	}

}
