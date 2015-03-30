package com.desno365.mods.Tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.desno365.mods.Activities.MainActivity;
import com.desno365.mods.R;

public class FragmentTab2 extends Fragment {

    private Boolean displayingAllChangelog = false;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragmenttab2, container, false);

        TextView textPortal = (TextView) rootView.findViewById(R.id.latest_version_portal_is);
        textPortal.setText(MainActivity.portalModVersion);

        final TextView textChangelogPortal = (TextView) rootView.findViewById(R.id.portal_changelog);
        textChangelogPortal.setText(android.text.Html.fromHtml(MainActivity.portalModChangelog));
        textChangelogPortal.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        textChangelogPortal.setMaxLines(15);

        final TextView textShowHide = (TextView) rootView.findViewById(R.id.changelog_show_hide_tab2);
        textShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!displayingAllChangelog) {
                    textChangelogPortal.setMaxLines(99999);
                    displayingAllChangelog = true;
                    textShowHide.setText(getResources().getString(R.string.hide_changelog));
                } else {
                    textChangelogPortal.setMaxLines(15);
                    displayingAllChangelog = false;
                    textShowHide.setText(getResources().getString(R.string.show_changelog));
                }
            }
        });
        textShowHide.setText(getResources().getString(R.string.show_changelog));
        ViewTreeObserver vto = textShowHide.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(textChangelogPortal.getLineCount() < 15) {
                    textShowHide.setVisibility(View.GONE);
                } else {
                    textShowHide.setVisibility(View.VISIBLE);
                }
            }
        });

        return rootView;
	}

}

