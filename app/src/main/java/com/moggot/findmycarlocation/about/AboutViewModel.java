package com.moggot.findmycarlocation.about;

import android.arch.lifecycle.MutableLiveData;

import com.moggot.findmycarlocation.billing.AdsEventListener;
import com.moggot.findmycarlocation.billing.BillingManager;
import com.moggot.findmycarlocation.common.BaseViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AboutViewModel extends BaseViewModel {

    private final MutableLiveData<BillingManager> billing = new MutableLiveData<>();

    private BillingManager mBillingManager;

    @Inject
    AboutViewModel(BillingManager billingManager) {
        mBillingManager = billingManager;
        billing.setValue(billingManager);
    }

    public void setBillingListener(AdsEventListener adsEventListener) {
        mBillingManager.setAdsShowListener(adsEventListener);
    }

    public boolean isPremium() {
        return mBillingManager.isPremium();
    }

    public MutableLiveData<BillingManager> getBilling() {
        return billing;
    }

    @Override
    public void onCleared() {
        super.onCleared();
        mBillingManager.destroy();
    }
}
