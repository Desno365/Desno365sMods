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

package com.desno365.mods.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.desno365.mods.AnalyticsApplication;
import com.desno365.mods.DesnoUtils;
import com.desno365.mods.R;
import com.google.android.gms.analytics.Tracker;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.LibsFragment;

public class LibrariesActivity extends BaseActivity {

    private static final String TAG = "LibrariesActivity";

    public static AppCompatActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Activity started (onCreate)");
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_libraries);



        // Start Google Analytics
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();

        // Send screen change
        DesnoUtils.sendScreenChange(mTracker, "LibrariesActivity");

        // Set up the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_libraries); // Attaching the layout to the toolbar object
        toolbar.setNavigationIcon(R.drawable.ic_navigation_arrow_back);
        toolbar.setTitle(R.string.used_libraries_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                DesnoUtils.changeFinishAnimations(activity);
            }
        });

        // create AboutLibraries Fragment
        LibsFragment fragment = new LibsBuilder()
                .withLibraries("photoview", "androidtooltip", "animatedexpandablelistview", "hugo", "android_shapeloadingview", "flatwokenicontheme") // definitions in strings.xml
                .withExcludedLibraries("androideasingfunctions")
                .withAutoDetect(true)
                .withAnimations(true)
                .withLicenseShown(true)
                .withVersionShown(false)
                .withListener(new LibsConfiguration.LibsListener() {
                    @Override
                    public void onIconClicked(View view) {

                    }

                    @Override
                    public boolean onIconLongClicked(View view) {
                        return false;
                    }


                    @Override
                    public boolean onLibraryAuthorClicked(View v, Library library) {
                        return true;
                    }

                    @Override
                    public boolean onLibraryBottomClicked(View v, Library library) {
                        return true;
                    }

                    @Override
                    public boolean onLibraryAuthorLongClicked(View v, Library library) {
                        return true;
                    }

                    @Override
                    public boolean onLibraryBottomLongClicked(View v, Library library) {
                        return true;
                    }


                    @Override
                    public boolean onLibraryContentClicked(View v, Library library) {
                        return false;
                    }

                    @Override
                    public boolean onExtraClicked(View v, Libs.SpecialButton specialButton) {
                        return false;
                    }

                    @Override
                    public boolean onLibraryContentLongClicked(View v, Library library) {
                        return true;
                    }


                })
                .fragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.about_libraries_container, fragment).commit();

    }

}

