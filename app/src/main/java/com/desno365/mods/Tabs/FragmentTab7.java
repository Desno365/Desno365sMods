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

public class FragmentTab7 extends Fragment {

    private Boolean displayingAllChangelog = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragmenttab7, container, false);

        TextView textUnreal = (TextView) rootView.findViewById(R.id.latest_version_unreal_is);
        textUnreal.setText(MainActivity.unrealMapVersion);

        final TextView textChangelogUnreal = (TextView) rootView.findViewById(R.id.unreal_changelog);
        textChangelogUnreal.setText(android.text.Html.fromHtml(MainActivity.unrealMapChangelog));
        textChangelogUnreal.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        textChangelogUnreal.setMaxLines(15);

        final TextView textShowHide = (TextView) rootView.findViewById(R.id.changelog_show_hide_tab7);
        textShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!displayingAllChangelog) {
                    textChangelogUnreal.setMaxLines(99999);
                    displayingAllChangelog = true;
                    textShowHide.setText(getResources().getString(R.string.hide_changelog));
                } else {
                    textChangelogUnreal.setMaxLines(15);
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
                if(textChangelogUnreal.getLineCount() < 15) {
                    textShowHide.setVisibility(View.GONE);
                } else {
                    textShowHide.setVisibility(View.VISIBLE);
                }
            }
        });

        return rootView;
    }

}
