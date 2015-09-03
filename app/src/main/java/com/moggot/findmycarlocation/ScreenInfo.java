package com.moggot.findmycarlocation;

/**
 * Created by dmitry on 28.08.15.
 */

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

        ImageView imageViewReview = (ImageView) findViewById(R.id.ivReview);

        imageViewReview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "imageViewReview");

                Uri uri = Uri
                        .parse("https://play.google.com/store/apps/details?id=com.moggot.findmycarlocation");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
