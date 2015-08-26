package com.moggot.findmycar;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ScreenInfo extends TrackedActivity {

	final static String LOG_TAG = "myLogs";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_info);

		Typeface font = Typeface.createFromAsset(getAssets(), "Dashley.ttf");

		TextView textViewAppName = (TextView) findViewById(R.id.textViewAppName);
		textViewAppName.setTypeface(font);
		TextView textViewInfo = (TextView) findViewById(R.id.textViewInfo);
		textViewInfo.setTypeface(font);
	}
}
