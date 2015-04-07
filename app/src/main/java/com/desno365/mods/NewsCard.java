package com.desno365.mods;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desno365.mods.Activities.NewsActivity;


public class NewsCard {
	private Context CONTEXT;
	private String TITLE;
	private String CONTENT;
	private View PARENT;

	public NewsCard(Context c, String title, String content) {
		TITLE = title;
		CONTENT = content;
        CONTEXT = c;
		//PARENT = ((LayoutInflater) CONTEXT.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.card_layout, null);
        PARENT = NewsActivity.activity.getLayoutInflater().inflate(R.layout.card_layout, null); // this fixes the "Calling startActivity() from outside of an Activity context requires the FLAG_ACTIVITY_NEW_TASK flag" error when clicking links
		((TextView) PARENT.findViewById(R.id.card_title)).setText(android.text.Html.fromHtml(TITLE));
		TextView textViewContent = (TextView) PARENT.findViewById(R.id.card_content_text);
        textViewContent.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
		textViewContent.setText(android.text.Html.fromHtml(CONTENT));
	}

	public String getTITLE() {
		return TITLE;
	}

	public View getPARENT() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.setMargins(12, 12, 12, 12);
		PARENT.setLayoutParams(params);
		return PARENT;
	}

}
