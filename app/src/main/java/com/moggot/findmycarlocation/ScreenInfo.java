package com.moggot.findmycarlocation;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ScreenInfo extends TrackedActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_info);

        ImageView imageViewReview = (ImageView) findViewById(R.id.ivReview);
        TextView tvAppVersion = (TextView) findViewById(R.id.textViewVersion);
        tvAppVersion.setText(BuildConfig.VERSION_NAME);

        imageViewReview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.moggot.findmycarlocation");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (!MyStartActivity(intent));
                    return;
            }
        });
    }

    private boolean MyStartActivity(Intent aIntent) {
        try
        {
            startActivity(aIntent);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
    }
}
