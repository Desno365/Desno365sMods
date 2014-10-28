package com.desno365.mods;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

public class FragmentTab1 extends Fragment {

    private Boolean displayingAllNews = false;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragmenttab1, container, false);

        final TextView textNews = (TextView) rootView.findViewById(R.id.news_container);
        textNews.setText(android.text.Html.fromHtml(MainActivity.newsString));
        textNews.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        textNews.setMaxLines(15);

        final TextView textShowHide = (TextView) rootView.findViewById(R.id.news_show_hide);
        textShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!displayingAllNews) {
                    textNews.setMaxLines(99999);
                    displayingAllNews = true;
                    textShowHide.setText(getResources().getString(R.string.hide_news));
                } else {
                    textNews.setMaxLines(15);
                    displayingAllNews = false;
                    textShowHide.setText(getResources().getString(R.string.show_news));
                }
            }
        });
        textShowHide.setText(getResources().getString(R.string.show_news));
        ViewTreeObserver vto = textShowHide.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(textNews.getLineCount() < 15) {
                    textShowHide.setVisibility(View.GONE);
                } else {
                    textShowHide.setVisibility(View.VISIBLE);
                }
            }
        });

		return rootView;
	}

}

