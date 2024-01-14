package com.moggot.findmycarlocation

import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.moggot.findmycarlocation.about.AboutFragment
import com.moggot.findmycarlocation.billing.BillingManager
import com.moggot.findmycarlocation.billing.BillingReadyListener
import com.moggot.findmycarlocation.common.BaseActivity
import com.moggot.findmycarlocation.common.BaseFragment
import com.moggot.findmycarlocation.databinding.ActivityMainBinding
import com.moggot.findmycarlocation.home.HomeFragment
import com.moggot.findmycarlocation.map.ui.GoogleMapFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    val billingManager: BillingManager by lazy { BillingManager(this) }
    private var navigationId = 0
    private var mAdsCallback: AdsCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingManager.startConnection()
        if (savedInstanceState != null) {
            viewBinding.bottomNavigation.selectedItemId = navigationId
        } else {
            showFragment(viewBinding.bottomNavigation.selectedItemId)
        }
        viewBinding.bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            showFragment(
                item.itemId
            )
        }
    }

    override fun performDataBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private fun showFragment(@IdRes itemId: Int): Boolean {
        if (itemId == R.id.navigation_map) {
            loadFragment(GoogleMapFragment.newInstance())
            return true
        } else if (itemId == R.id.navigation_about) {
            loadFragment(AboutFragment.newInstance())
            return true
        }
        loadFragment(HomeFragment.newInstance())
        return true
    }

    private fun loadFragment(fragment: BaseFragment) {
        val cachedFragment = supportFragmentManager.findFragmentByTag(fragment.fragmentTag)
        if (cachedFragment == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment, fragment.fragmentTag)
                .commit()
        }
    }

    //        return mBillingManager.isPremium();
    val isPremiumPurchased: Boolean
        get() =//        return mBillingManager.isPremium();
            false

    public override fun onResume() {
        super.onResume()
        viewBinding.bottomNavigation.selectedItemId = navigationId
    }

    fun setCallback(adsCallback: AdsCallback?) {
        mAdsCallback = adsCallback
    }

    fun switchToMap() {
        viewBinding.bottomNavigation.selectedItemId = R.id.navigation_map
    }

    fun switchToHome() {
        viewBinding.bottomNavigation.selectedItemId = R.id.navigation_home
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        navigationId = viewBinding.bottomNavigation.selectedItemId
    }

    override fun onDestroy() {
//        mBillingManager.destroy();
        super.onDestroy()
    }

    interface AdsCallback {
        fun showAds(show: Boolean)
    }

    private inner class PurchaseEnableListener : BillingReadyListener {
        override fun billingReady() {
//            if (mBillingManager != null
//                    && mBillingManager.getBillingClientResponseCode() > BILLING_MANAGER_NOT_INITIALIZED) {
//                mAdsCallback.showAds(!mBillingManager.isPremium());
//            }
        }
    }
}
