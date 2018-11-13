package com.moggot.findmycarlocation.billing;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BillingManager{

    private BillingClient mBillingClient;
    @Nullable
    private AdsEventListener mAdsEventListener;

    private boolean showAds = true;
    private final Context mContext;

    @Inject
    public BillingManager(Context context) {
        mContext = context;
        mBillingClient = BillingClient.newBuilder(context)
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
                        if (responseCode == BillingClient.BillingResponse.OK &&
                                purchases != null) {
                            for (Purchase purchase : purchases) {
                                mBillingClient.consumeAsync(purchase.getPurchaseToken(), new AdsPolicyUpdateListener());
                            }
                        }
                    }
                }).build();
    }

    public void startConnection() {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(int responseCode) {
                if (responseCode == BillingClient.BillingResponse.OK) {
                    mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS, new PurchaseHistoryResponseListener() {
                        @Override
                        public void onPurchaseHistoryResponse(int responseCode, List<Purchase> purchases) {
                            if (responseCode == BillingClient.BillingResponse.OK &&
                                    purchases != null) {
                                for (Purchase purchase : purchases) {
                                    mBillingClient.consumeAsync(purchase.getPurchaseToken(), new AdsPolicyUpdateListener());
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                mBillingClient.startConnection(this);

            }
        });
    }

    public boolean isPremium() {
        return showAds;
    }

    public void setAdsShowListener(AdsEventListener adsEventListener) {
        mAdsEventListener = adsEventListener;
    }

    public void requestSubscription(Activity activity) {
        if (!showAds) {
            return;
        }
        List<String> skuList = new ArrayList<String>() {{
            add("ads_disable_subscription");
        }};

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build();
        mBillingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                if (responseCode == BillingClient.BillingResponse.OK &&
                        skuDetailsList != null) {
                    for (SkuDetails skuDetails : skuDetailsList) {
                        BillingFlowParams.Builder builder = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetails);
                        if (mBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
                                == BillingClient.BillingResponse.OK) {
                            mBillingClient.launchBillingFlow(activity, builder.build());
                        }
                    }
                } else {
                    startConnection();
                }
            }
        });
    }

    private class AdsPolicyUpdateListener implements ConsumeResponseListener {

        @Override
        public void onConsumeResponse(int responseCode, String purchaseToken) {
            if (mAdsEventListener != null) {
                if (responseCode == BillingClient.BillingResponse.OK) {
                    return;
                }
                mAdsEventListener.hideAds();
                showAds = false;
            }
        }
    }

    public void destroy() {
        if (mBillingClient != null && mBillingClient.isReady()) {
            mBillingClient.endConnection();
        }
    }
}
