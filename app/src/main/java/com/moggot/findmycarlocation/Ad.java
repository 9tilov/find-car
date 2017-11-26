package com.moggot.findmycarlocation;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

final public class Ad {
    private Context mCtx;

    public Ad(Context ctx) {
        mCtx = ctx;
        MobileAds.initialize(mCtx.getApplicationContext(), mCtx.getString(R.string.app_id));
    }

    public void showBanner(final int id) {
        AdView mAdView = (AdView) ((Activity)mCtx).findViewById(id);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void showInterstitial(int id) {
        final InterstitialAd interstitialAd = new InterstitialAd(mCtx.getApplicationContext());
        interstitialAd.setAdUnitId(mCtx.getString(id));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }
        });
    }
}