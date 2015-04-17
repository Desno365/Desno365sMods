package com.desno365.mods.Tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.desno365.mods.Activities.MainActivity;
import com.desno365.mods.Values.Values;
import com.desno365.mods.R;

public class FragmentTab6 extends Fragment {

    private Boolean displayingAllChangelog = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragmenttab6, container, false);

        TextView textGuns = (TextView) rootView.findViewById(R.id.latest_version_guns_is);
        textGuns.setText(MainActivity.gunsModVersion);

        final TextView textChangelogGuns = (TextView) rootView.findViewById(R.id.guns_changelog);
        textChangelogGuns.setText(android.text.Html.fromHtml(MainActivity.gunsModChangelog));
        textChangelogGuns.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        textChangelogGuns.setMaxLines(Values.CHANGELOG_TEXT_MAX_LINES);

        final TextView textShowHide = (TextView) rootView.findViewById(R.id.changelog_show_hide_tab6);
        textShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!displayingAllChangelog) {
                    textChangelogGuns.setMaxLines(99999);
                    displayingAllChangelog = true;
                    textShowHide.setText(getResources().getString(R.string.hide_changelog));
                } else {
                    textChangelogGuns.setMaxLines(Values.CHANGELOG_TEXT_MAX_LINES);
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
                if(textChangelogGuns.getLineCount() < Values.CHANGELOG_TEXT_MAX_LINES) {
                    textShowHide.setVisibility(View.GONE);
                } else {
                    textShowHide.setVisibility(View.VISIBLE);
                }
            }
        });

        return rootView;
    }

}
