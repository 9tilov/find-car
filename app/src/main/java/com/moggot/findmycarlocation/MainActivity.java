package com.moggot.findmycarlocation;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moggot.findmycarlocation.about.AboutFragment;
import com.moggot.findmycarlocation.base.BaseFragment;
import com.moggot.findmycarlocation.billing.BillingManager;
import com.moggot.findmycarlocation.billing.BillingReadyListener;
import com.moggot.findmycarlocation.common.LocationActivity;
import com.moggot.findmycarlocation.home.HomeFragment;
import com.moggot.findmycarlocation.map.GoogleMapFragment;

import static com.moggot.findmycarlocation.billing.BillingManager.BILLING_MANAGER_NOT_INITIALIZED;

public class MainActivity extends LocationActivity {

    private BottomNavigationView bottomNavigationView;

    private BillingManager mBillingManager;

    private int navigationId;
    private AdsCallback mAdsCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        mBillingManager = new BillingManager(this);
        mBillingManager.setAdsShowListener(new PurchaseEnableListener());
        mBillingManager.startConnection();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> showFragment(item.getItemId()));

        if (savedInstanceState != null) {
            bottomNavigationView.setSelectedItemId(navigationId);
        } else {
            showFragment(bottomNavigationView.getSelectedItemId());
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> showFragment(item.getItemId()));
    }

    private boolean showFragment(@IdRes int itemId) {
        switch (itemId) {
            case R.id.navigation_map:
                loadFragment(GoogleMapFragment.Companion.newInstance());
                return true;
            case R.id.navigation_about:
                loadFragment(AboutFragment.newInstance());
                return true;
            default:
                loadFragment(HomeFragment.Companion.newInstance());
                return true;
        }
    }

    private void loadFragment(BaseFragment fragment) {
        Fragment cachedFragment = getSupportFragmentManager().findFragmentByTag(fragment.getFragmentTag());
        if (cachedFragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment, fragment.getFragmentTag())
                    .commit();
        }
    }

    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    public boolean isPremiumPurchased() {
        return mBillingManager.isPremium();
    }

    @Override
    public void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(navigationId);
    }

    public void setCallback(AdsCallback adsCallback) {
        mAdsCallback = adsCallback;
    }

    public void switchToMap() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_map);
    }

    public void switchToHome() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        navigationId = bottomNavigationView.getSelectedItemId();
    }

    @Override
    protected void onDestroy() {
        mBillingManager.destroy();
        super.onDestroy();
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.frame_container;
    }

    public interface AdsCallback {
        void showAds(boolean show);
    }

    private class PurchaseEnableListener implements BillingReadyListener {

        @Override
        public void billingReady() {
            if (mBillingManager != null
                    && mBillingManager.getBillingClientResponseCode() > BILLING_MANAGER_NOT_INITIALIZED) {
                mAdsCallback.showAds(!mBillingManager.isPremium());
            }
        }
    }
}
