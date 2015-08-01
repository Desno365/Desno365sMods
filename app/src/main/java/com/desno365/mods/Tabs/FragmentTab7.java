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

package com.desno365.mods.Tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.desno365.mods.Activities.MainActivity;
import com.desno365.mods.DesnoUtils;
import com.desno365.mods.R;
import com.desno365.mods.SharedConstants.SharedConstants;


public class FragmentTab7 extends Fragment {

	private boolean displayingAllChangelog = false;

	private int changelogHiddenHeight;

	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragmenttab7, container, false); // xml tab

		TextView textVersion = (TextView) rootView.findViewById(R.id.latest_version_unreal_is); // id TextView version
		textVersion.setText(MainActivity.unrealMapVersion); // MainActivity variable that holds the latest version


		TextView textCompatibility = (TextView) rootView.findViewById(R.id.unreal_compatibility); // id TextView compatibility
		textCompatibility.setText(MainActivity.unrealMapCompatibility); // MainActivity variable that holds the versions compatibility


		final TextView textChangelog = (TextView) rootView.findViewById(R.id.unreal_changelog); // id TextView changelog
		textChangelog.setText(android.text.Html.fromHtml(MainActivity.unrealMapChangelog)); // MainActivity variable that holds the latest changelog
		textChangelog.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
		textChangelog.setMaxLines(SharedConstants.CHANGELOG_TEXT_MAX_LINES);

		final TextView textShowHide = (TextView) rootView.findViewById(R.id.changelog_show_hide_tab7); // id TextView show/hide changelog
		textShowHide.setText(getResources().getString(R.string.show_changelog));
		textShowHide.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!displayingAllChangelog) {

					// get the TextView height that will be used when hiding the changelog
					changelogHiddenHeight = textChangelog.getHeight();

					DesnoUtils.expandTextView(container, textChangelog);

					displayingAllChangelog = true;
					textShowHide.setText(getResources().getString(R.string.hide_changelog));

				} else {

					DesnoUtils.collapseTextView(container, textChangelog, changelogHiddenHeight);

					displayingAllChangelog = false;
					textShowHide.setText(getResources().getString(R.string.show_changelog));
				}
			}
		});

		// make the show/hide button invisible if it is not necessary
		ViewTreeObserver vto = textShowHide.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (textChangelog.getLineCount() <= SharedConstants.CHANGELOG_TEXT_MAX_LINES) {
					textShowHide.setVisibility(View.GONE);
				} else {
					textShowHide.setVisibility(View.VISIBLE);
				}
			}
		});

		return rootView;
	}
}

