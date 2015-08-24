package com.moggot.findmycar;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class ScreenInfo extends Activity {
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
