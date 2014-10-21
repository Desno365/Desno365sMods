package com.desno365.mods;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.ViewTreeObserver;
import android.widget.TextView;

public class FragmentTab5 extends Fragment {

    private Boolean displayingAllChangelog = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragmenttab5, container, false);

        TextView textJukebox = (TextView) rootView.findViewById(R.id.latest_version_jukebox_is);
        textJukebox.setText(MainActivity.jukeboxModVersion);

        final TextView textChangelogJukebox = (TextView) rootView.findViewById(R.id.jukebox_changelog);
        textChangelogJukebox.setText(android.text.Html.fromHtml(MainActivity.jukeboxModChangelog));
        textChangelogJukebox.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        textChangelogJukebox.setMaxLines(15);

        final TextView textShowHide = (TextView) rootView.findViewById(R.id.changelog_show_hide_tab5);
        textShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!displayingAllChangelog) {
                    textChangelogJukebox.setMaxLines(99999);
                    displayingAllChangelog = true;
                    textShowHide.setText(getResources().getString(R.string.hide_changelog));
                } else {
                    textChangelogJukebox.setMaxLines(15);
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
                if(textChangelogJukebox.getLineCount() < 15) {
                    textShowHide.setVisibility(View.GONE);
                } else {
                    textShowHide.setVisibility(View.VISIBLE);
                }
            }
        });

        return rootView;
    }

}

