package com.moggot.findmycarlocation.billing;

import android.app.Activity;
import android.support.annotation.Nullable;

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
public class BillingManager {

    public static final int BILLING_MANAGER_NOT_INITIALIZED = -1;
    private final Activity mActivity;
    private BillingClient mBillingClient;
    @Nullable
    private BillingReadyListener mBillingReadyListener;
    private int mBillingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED;
    private boolean showAds = true;

    @Inject
    public BillingManager(Activity activity) {
        mActivity = activity;
        mBillingClient = BillingClient.newBuilder(activity)
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

    public int getBillingClientResponseCode() {
        return mBillingClientResponseCode;
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
                            if (mBillingReadyListener != null) {
                                mBillingReadyListener.billingReady();
                            }
                        }
                    });
                }
                mBillingClientResponseCode = responseCode;
            }

            @Override
            public void onBillingServiceDisconnected() {
                mBillingClient.startConnection(this);

            }
        });
    }

    public boolean isPremium() {
        return !showAds;
    }

    public void setAdsShowListener(BillingReadyListener billingReadyListener) {
        mBillingReadyListener = billingReadyListener;
    }

    public void requestSubscription() {
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
                            mBillingClient.launchBillingFlow(mActivity, builder.build());
                        }
                    }
                } else {
                    startConnection();
                }
            }
        });
    }

    public void destroy() {
        if (mBillingClient != null && mBillingClient.isReady()) {
            mBillingClient.endConnection();
        }
    }

    private class AdsPolicyUpdateListener implements ConsumeResponseListener {

        @Override
        public void onConsumeResponse(int responseCode, String purchaseToken) {
            if (responseCode == BillingClient.BillingResponse.OK) {
                return;
            }
            showAds = false;
        }
    }
}
