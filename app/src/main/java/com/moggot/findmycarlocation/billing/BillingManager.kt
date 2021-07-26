package com.moggot.findmycarlocation.billing

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import java.io.IOException
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(private val mActivity: Activity) {

    // private val mBillingClient: BillingClient  = BillingClient.newBuilder(mActivity)
    //     .setListener(object : PurchasesUpdatedListener {
    //         fun onPurchasesUpdated(responseCode: Int, purchases: List<Purchase>) {
    //             if (responseCode == BillingClient.BillingResponse.OK &&
    //                 purchases != null
    //             ) {
    //                 for (purchase in purchases) {
    //                     if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
    //                         return
    //                     }
    //                     if (purchase.isAutoRenewing) {
    //                         showAds = false
    //                     }
    //                 }
    //             }
    //         }
    //     }).build()     mBillingClient = BillingClient.newBuilder(mActivity)
    //     .setListener(object : PurchasesUpdatedListener {
    //         fun onPurchasesUpdated(responseCode: Int, @Nullable purchases: List<Purchase>?) {
    //             if (responseCode == BillingClient.BillingResponse.OK &&
    //                 purchases != null
    //             ) {
    //                 for (purchase in purchases) {
    //                     if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
    //                         return
    //                     }
    //                     if (purchase.isAutoRenewing) {
    //                         showAds = false
    //                     }
    //                 }
    //             }
    //         }
    //     }).build()
    //
    // private var mBillingReadyListener: BillingReadyListener? = null
    // var billingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED
    //     private set
    // private var showAds = true
    // fun startConnection() {
    //     mBillingClient!!.startConnection(object : BillingClientStateListener {
    //         fun onBillingSetupFinished(responseCode: Int) {
    //             billingClientResponseCode = responseCode
    //             if (responseCode == BillingClient.BillingResponse.OK) {
    //                 val result = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS)
    //                 val purchases = result.purchasesList
    //                 for (purchase in purchases!!) {
    //                     if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
    //                         return
    //                     }
    //                     if (purchase.isAutoRenewing) {
    //                         showAds = false
    //                     }
    //                     if (mBillingReadyListener != null) {
    //                         mBillingReadyListener!!.billingReady()
    //                     }
    //                 }
    //             }
    //         }
    //
    //         override fun onBillingServiceDisconnected() {
    //             mBillingClient.startConnection(this)
    //         }
    //     })
    // }
    //
    // val isPremium: Boolean
    //     get() = !showAds
    //
    // fun setAdsShowListener(billingReadyListener: BillingReadyListener?) {
    //     mBillingReadyListener = billingReadyListener
    // }
    //
    // fun requestSubscription() {
    //     val skuList: List<String> = object : ArrayList<String?>() {
    //         init {
    //             add(SKU_NAME)
    //         }
    //     }
    //     val params = SkuDetailsParams.newBuilder()
    //         .setSkusList(skuList)
    //         .setType(BillingClient.SkuType.SUBS)
    //         .build()
    //     mBillingClient!!.querySkuDetailsAsync(params, object : SkuDetailsResponseListener {
    //         fun onSkuDetailsResponse(responseCode: Int, skuDetailsList: List<SkuDetails?>?) {
    //             if (responseCode == BillingClient.BillingResponse.OK &&
    //                 skuDetailsList != null
    //             ) {
    //                 for (skuDetails in skuDetailsList) {
    //                     val builder = BillingFlowParams.newBuilder()
    //                         .setSkuDetails(skuDetails!!)
    //                     if (mBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
    //                         == BillingClient.BillingResponse.OK
    //                     ) {
    //                         mBillingClient.launchBillingFlow(mActivity, builder.build())
    //                     }
    //                 }
    //             } else {
    //                 startConnection()
    //             }
    //         }
    //     })
    // }
    //
    // fun destroy() {
    //     if (mBillingClient != null && mBillingClient.isReady) {
    //         mBillingClient.endConnection()
    //     }
    // }
    //
    // private fun verifyValidSignature(signedData: String, signature: String): Boolean {
    //     // Some sanity checks to see if the developer (that's you!) really followed the
    //     // instructions to run this sample (don't put these checks on your app!)
    //     return try {
    //         val key = Security.decrypt(KEY_SOLT, solt)
    //         Security.verifyPurchase(key, signedData, signature)
    //     } catch (e: IOException) {
    //         false
    //     }
    // }
    //
    // companion object {
    //     const val BILLING_MANAGER_NOT_INITIALIZED = -1
    //     private val KEY_SOLT = intArrayOf(
    //         105,
    //         81,
    //         80,
    //         97,
    //         48,
    //         73,
    //         48,
    //         53,
    //         87,
    //         67,
    //         51,
    //         72,
    //         75,
    //         82,
    //         74,
    //         54,
    //         162,
    //         66,
    //         116,
    //         90,
    //         88,
    //         82,
    //         60,
    //         101,
    //         48,
    //         58,
    //         90,
    //         103,
    //         89,
    //         104,
    //         123,
    //         56,
    //         110,
    //         56,
    //         50,
    //         87,
    //         103,
    //         63,
    //         82,
    //         96,
    //         56,
    //         82,
    //         52,
    //         58,
    //         61,
    //         107,
    //         105,
    //         97,
    //         87,
    //         58,
    //         80,
    //         80,
    //         107,
    //         76,
    //         124,
    //         119,
    //         52,
    //         119,
    //         105,
    //         70,
    //         163,
    //         77,
    //         69,
    //         64,
    //         110,
    //         75,
    //         100,
    //         150,
    //         118,
    //         167,
    //         95,
    //         100,
    //         114,
    //         83,
    //         95,
    //         96,
    //         83,
    //         102,
    //         92,
    //         80,
    //         67,
    //         52,
    //         83,
    //         107,
    //         48,
    //         88,
    //         70,
    //         92,
    //         86,
    //         69,
    //         67,
    //         129,
    //         48,
    //         85,
    //         51,
    //         98,
    //         73,
    //         105,
    //         67,
    //         125,
    //         54,
    //         58,
    //         89,
    //         106,
    //         69,
    //         64,
    //         169,
    //         132,
    //         104,
    //         61,
    //         61,
    //         55,
    //         170,
    //         106,
    //         165,
    //         171,
    //         103,
    //         74,
    //         53,
    //         88,
    //         80,
    //         62,
    //         75,
    //         51,
    //         86,
    //         130,
    //         110,
    //         81,
    //         110,
    //         140,
    //         174,
    //         101,
    //         89,
    //         108,
    //         99,
    //         76,
    //         91,
    //         95,
    //         98,
    //         78,
    //         65,
    //         158,
    //         144,
    //         91,
    //         92,
    //         75,
    //         97,
    //         68,
    //         95,
    //         115,
    //         94,
    //         168,
    //         99,
    //         111,
    //         57,
    //         58,
    //         97,
    //         48,
    //         54,
    //         161,
    //         99,
    //         94,
    //         69,
    //         129,
    //         93,
    //         82,
    //         60,
    //         116,
    //         95,
    //         75,
    //         59,
    //         69,
    //         66,
    //         49,
    //         59,
    //         73,
    //         108,
    //         82,
    //         109,
    //         63,
    //         143,
    //         52,
    //         87,
    //         72,
    //         48,
    //         117,
    //         56,
    //         48,
    //         133,
    //         110,
    //         74,
    //         53,
    //         101,
    //         58,
    //         64,
    //         165,
    //         90,
    //         97,
    //         65,
    //         136,
    //         111,
    //         89,
    //         83,
    //         52,
    //         48,
    //         148,
    //         122,
    //         77,
    //         55,
    //         97,
    //         66,
    //         87,
    //         103,
    //         165,
    //         175,
    //         90,
    //         85,
    //         110,
    //         56,
    //         53,
    //         173,
    //         50,
    //         65,
    //         148,
    //         76,
    //         125,
    //         75,
    //         93,
    //         78,
    //         171,
    //         85,
    //         70,
    //         62,
    //         81,
    //         107,
    //         106,
    //         139,
    //         80,
    //         74,
    //         64,
    //         48,
    //         80,
    //         126,
    //         115,
    //         57,
    //         109,
    //         66,
    //         63,
    //         114,
    //         92,
    //         86,
    //         56,
    //         72,
    //         93,
    //         137,
    //         97,
    //         75,
    //         72,
    //         102,
    //         62,
    //         83,
    //         110,
    //         85,
    //         97,
    //         96,
    //         91,
    //         98,
    //         169,
    //         99,
    //         103,
    //         77,
    //         74,
    //         52,
    //         79,
    //         87,
    //         110,
    //         94,
    //         109,
    //         69,
    //         111,
    //         108,
    //         128,
    //         49,
    //         50,
    //         59,
    //         97,
    //         108,
    //         83,
    //         86,
    //         138,
    //         52,
    //         54,
    //         104,
    //         82,
    //         166,
    //         52,
    //         87,
    //         87,
    //         52,
    //         94,
    //         50,
    //         74,
    //         111,
    //         62,
    //         99,
    //         61,
    //         84,
    //         84,
    //         77,
    //         115,
    //         58,
    //         85,
    //         62,
    //         62,
    //         103,
    //         118,
    //         110,
    //         90,
    //         70,
    //         58,
    //         78,
    //         48,
    //         89,
    //         57,
    //         62,
    //         97,
    //         94,
    //         77,
    //         61,
    //         85,
    //         65,
    //         74,
    //         103,
    //         124,
    //         141,
    //         73,
    //         64,
    //         58,
    //         68,
    //         87,
    //         106,
    //         128,
    //         98,
    //         84,
    //         96,
    //         136,
    //         106,
    //         110,
    //         162,
    //         97,
    //         133,
    //         85,
    //         111,
    //         142,
    //         103,
    //         52,
    //         85,
    //         165,
    //         90,
    //         66,
    //         48,
    //         136,
    //         109,
    //         72,
    //         111,
    //         53,
    //         103,
    //         81,
    //         134,
    //         113,
    //         115,
    //         48,
    //         71,
    //         173,
    //         85,
    //         63,
    //         144,
    //         99,
    //         92,
    //         54,
    //         143,
    //         117,
    //         62,
    //         50,
    //         107,
    //         74,
    //         92,
    //         96,
    //         89,
    //         104,
    //         98,
    //         59
    //     )
    //     private const val solt = "thisIsAKey"
    //     private const val SKU_NAME = "ads_disable_subscription"
    // }
    //
    // init {
    //     mBillingClient = BillingClient.newBuilder(mActivity)
    //         .setListener(object : PurchasesUpdatedListener {
    //             fun onPurchasesUpdated(responseCode: Int, @Nullable purchases: List<Purchase>?) {
    //                 if (responseCode == BillingClient.BillingResponse.OK &&
    //                     purchases != null
    //                 ) {
    //                     for (purchase in purchases) {
    //                         if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
    //                             return
    //                         }
    //                         if (purchase.isAutoRenewing) {
    //                             showAds = false
    //                         }
    //                     }
    //                 }
    //             }
    //         }).build()
    // }
}