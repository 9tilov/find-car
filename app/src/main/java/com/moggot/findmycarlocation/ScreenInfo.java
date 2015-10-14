package com.moggot.findmycarlocation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ScreenInfo extends TrackedActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_info);

        ImageView imageViewReview = (ImageView) findViewById(R.id.ivReview);

        imageViewReview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri
                        .parse("https://play.google.com/store/apps/details?id=com.moggot.findmycarlocation");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
