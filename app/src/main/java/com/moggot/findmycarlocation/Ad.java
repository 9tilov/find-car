package com.moggot.findmycarlocation;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by moggot on 25.06.16.
 */
final public class Ad {
    private Activity mActivity;

    public Ad(Activity activity) {
        mActivity = activity;
        MobileAds.initialize(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.app_id));
    }

    public void ShowBanner(int id) {
        AdView mAdView = (AdView) mActivity.findViewById(id);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void ShowInterstitial(int id) {
        final InterstitialAd interstitialAd = new InterstitialAd(mActivity.getApplicationContext());
        interstitialAd.setAdUnitId(mActivity.getResources().getString(id));
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
