/*
 *
 * Copyright 2017 Dennis Motta
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

package com.desno365.mods;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.desno365.mods.Activities.MainActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class MainNavigationDrawerFragment extends Fragment {

	public static DrawerLayout mDrawerLayout;
	public AnimatedExpandableListView mDrawerListView;
	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;
	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;
	private View mFragmentContainerView;

	private List<Item> items;

	// commented code that change the checked item
	//public int mCurrentSelectedPosition = 0;

	public MainNavigationDrawerFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mDrawerListView = (AnimatedExpandableListView) inflater.inflate(R.layout.navigation_drawer_fragment, container, false);


		items = new ArrayList<Item>();

		// Populate our list with groups and it's children
		ChildItem home = new ChildItem();
		home.title = getString(R.string.home_title);
		home.hasIcon = true;
		home.iconId = R.drawable.ic_action_home;
		items.add(home);


		GroupItem mods = new GroupItem();
		mods.title = getString(R.string.mods);
		mods.hasIcon = true;
		mods.iconId = R.drawable.ic_action_extension;

		ChildItem child1 = new ChildItem();
		child1.title = getString(R.string.mod5_title);
		mods.children.add(child1);

		ChildItem child2 = new ChildItem();
		child2.title = getString(R.string.mod1_title);
		mods.children.add(child2);

		ChildItem child3 = new ChildItem();
		child3.title = getString(R.string.mod2_title);
		mods.children.add(child3);

		ChildItem child4 = new ChildItem();
		child4.title = getString(R.string.mod4_title);
		mods.children.add(child4);

		ChildItem child5 = new ChildItem();
		child5.title = getString(R.string.mod6_title);
		mods.children.add(child5);

		items.add(mods);


		ChildItem about = new ChildItem();
		about.title = getString(R.string.action_info);
		about.hasIcon = true;
		about.iconId = R.drawable.ic_action_info;
		items.add(about);


		ChildItem help = new ChildItem();
		help.title = getString(R.string.action_help);
		help.hasIcon = true;
		help.iconId = R.drawable.ic_action_help;
		items.add(help);


		ChildItem news = new ChildItem();
		news.title = getString(R.string.news_title);
		news.hasIcon = true;
		news.iconId = R.drawable.ic_action_news;
		items.add(news);


		ChildItem settings = new ChildItem();
		settings.title = getString(R.string.action_settings);
		settings.hasIcon = true;
		settings.iconId = R.drawable.ic_action_settings;
		items.add(settings);


		NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(getActivity().getApplicationContext());
		adapter.setData(items);

		mDrawerListView.setAdapter(adapter);

		// In order to show animations, we need to use a custom click handler
		// for our ExpandableListView.
		mDrawerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				if (isGroupExpandable(groupPosition)) {
					GroupItem group = (GroupItem) items.get(groupPosition);

					// We call collapseGroupWithAnimation(int) and
					// expandGroupWithAnimation(int) to animate group
					// expansion/collapse.
					if (mDrawerListView.isGroupExpanded(groupPosition)) {
						mDrawerListView.collapseGroupWithAnimation(groupPosition);
						group.groupIndicatorView.setImageResource(R.drawable.ic_arrow_down);
					} else {
						mDrawerListView.expandGroupWithAnimation(groupPosition);
						group.groupIndicatorView.setImageResource(R.drawable.ic_arrow_up);
					}
				} else {
					selectGroup(groupPosition);
				}
				return true;
			}
		});

		mDrawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				selectItem(groupPosition, childPosition);
				return false;
			}
		});


		// commented code that change the checked item
		//mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
		return mDrawerListView;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 * @param fragmentId   The android:id of this fragment in its activity's layout.
	 * @param drawerLayout The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(
				getActivity(),                    /* host Activity */
				mDrawerLayout,                    /* DrawerLayout object */
				MainActivity.toolbar,             /* toolbar */
				R.string.app_name,  /* "open drawer" description for accessibility */
				R.string.close_dialog  /* "close drawer" description for accessibility */
		) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}
		};

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	public void selectItem(int groupPosition, int childPosition) {
		// commented code that change the checked item
		//mCurrentSelectedPosition = position;
		/*if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}*/
		if (mDrawerLayout != null && mFragmentContainerView != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(groupPosition, childPosition);
		}
	}

	public void selectGroup(int position) {
		if (mDrawerLayout != null && mFragmentContainerView != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerGroupSelected(position);
		}
	}

	public boolean isGroupExpandable(int position) {
		return !(items.get(position) instanceof ChildItem);
	}

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public interface NavigationDrawerCallbacks {
		/**
		 * Called when an item or a group in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int groupPosition, int childPosition);

		void onNavigationDrawerGroupSelected(int position);
	}

	private static class Item {
		String title;
		boolean hasIcon;
		int iconId;
	}

	private static class GroupItem extends Item {
		List<ChildItem> children = new ArrayList<ChildItem>();
		ImageView groupIndicatorView;
	}

	private static class ChildItem extends Item {
		String hint; // maybe in future I may want to add an hint under the title
	}

	private static class ChildHolder {
		TextView title;
	}

	private static class GroupHolder {
		TextView title;
	}


	/**
	 * Adapter for our list of {@link GroupItem}s.
	 */
	private class NavigationDrawerAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
		private LayoutInflater inflater;

		private List<Item> items;

		public NavigationDrawerAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void setData(List<Item> items) {
			this.items = items;
		}

		@Override
		public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			ChildHolder holder;
			ChildItem item = getChild(groupPosition, childPosition);
			if (convertView == null) {
				holder = new ChildHolder();
				convertView = inflater.inflate(R.layout.navigation_drawer_item, parent, false);
				TextView itemTextView = (TextView) convertView.findViewById(R.id.navigation_drawer_item_title);

				// apply custom font with shadow
				Typeface font = Typeface.createFromAsset(MainActivity.activity.getAssets(), "fonts/minecraft.ttf");
				itemTextView.setTypeface(font);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
					itemTextView.setShadowLayer(1, Math.round(itemTextView.getLineHeight() / 8), Math.round(itemTextView.getLineHeight() / 8), ContextCompat.getColor(getContext(), R.color.drawer_text_shadow_color));
				else
					itemTextView.setShadowLayer(0.0001F, Math.round(itemTextView.getLineHeight() / 8), Math.round(itemTextView.getLineHeight() / 8), ContextCompat.getColor(getContext(), R.color.drawer_text_shadow_color));

				holder.title = itemTextView;
				convertView.setTag(holder);
			} else {
				holder = (ChildHolder) convertView.getTag();
			}

			holder.title.setText(item.title);

			return convertView;
		}

		@Override
		public int getRealChildrenCount(int groupPosition) {

			Item group = getGroup(groupPosition);
			if (isGroupExpandable(groupPosition)) {
				return ((GroupItem) group).children.size();
			} else {
				return 0;
			}

		}

		@Override
		public int getGroupCount() {
			return items.size();
		}

		@Override
		public Item getGroup(int groupPosition) {
			return items.get(groupPosition);
		}

		@Override
		public ChildItem getChild(int groupPosition, int childPosition) {
			Item group = getGroup(groupPosition);
			if (isGroupExpandable(groupPosition)) {
				// is a group
				return ((GroupItem) group).children.get(childPosition);
			} else {
				// is a not expandable group
				return ((ChildItem) group);
			}

		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			GroupHolder holder;
			Item item = getGroup(groupPosition);
			if (convertView == null) {
				holder = new GroupHolder();
				convertView = inflater.inflate(R.layout.navigation_drawer_group, parent, false);
				TextView groupTextView = (TextView) convertView.findViewById(R.id.navigation_drawer_group_title);

				// apply custom font with shadow
				Typeface font = Typeface.createFromAsset(MainActivity.activity.getAssets(), "fonts/minecraft.ttf");
				groupTextView.setTypeface(font);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
					groupTextView.setShadowLayer(1, Math.round(groupTextView.getLineHeight() / 8), Math.round(groupTextView.getLineHeight() / 8), ContextCompat.getColor(getContext(), R.color.drawer_text_shadow_color));
				else
					groupTextView.setShadowLayer(0.0001F, Math.round(groupTextView.getLineHeight() / 8), Math.round(groupTextView.getLineHeight() / 8), ContextCompat.getColor(getContext(), R.color.drawer_text_shadow_color));

				holder.title = groupTextView;
				convertView.setTag(holder);
			} else {
				holder = (GroupHolder) convertView.getTag();
			}

			holder.title.setText(item.title);

			// group indicator image
			View groupIndicatorImageView = convertView.findViewById(R.id.image_group_indicator);
			if (isGroupExpandable(groupPosition)) {
				GroupItem group = (GroupItem) item;
				group.groupIndicatorView = (ImageView) groupIndicatorImageView;
			} else {
				groupIndicatorImageView.setVisibility(View.GONE);
			}

			// group image
			ImageView groupImageView = (ImageView) convertView.findViewById(R.id.image_group);
			if (item.hasIcon) {
				groupImageView.setImageResource(item.iconId);
			}

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return true;
		}

	}

}

