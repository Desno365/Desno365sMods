package com.desno365.mods.Tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.desno365.mods.Activities.MainActivity;
import com.desno365.mods.R;

public class FragmentTab4 extends Fragment {

    private Boolean displayingAllChangelog = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragmenttab4, container, false);

        TextView textTurrets = (TextView) rootView.findViewById(R.id.latest_version_turrets_is);
        textTurrets.setText(MainActivity.turretsModVersion);

        final TextView textChangelogTurrets = (TextView) rootView.findViewById(R.id.turrets_changelog);
        textChangelogTurrets.setText(android.text.Html.fromHtml(MainActivity.turretsModChangelog));
        textChangelogTurrets.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        textChangelogTurrets.setMaxLines(15);

        final TextView textShowHide = (TextView) rootView.findViewById(R.id.changelog_show_hide_tab4);
        textShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!displayingAllChangelog) {
                    textChangelogTurrets.setMaxLines(99999);
                    displayingAllChangelog = true;
                    textShowHide.setText(getResources().getString(R.string.hide_changelog));
                } else {
                    textChangelogTurrets.setMaxLines(15);
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
                if (textChangelogTurrets.getLineCount() < 15) {
                    textShowHide.setVisibility(View.GONE);
                } else {
                    textShowHide.setVisibility(View.VISIBLE);
                }
            }
        });

        return rootView;
    }

}
