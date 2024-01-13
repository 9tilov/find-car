package com.moggot.findmycarlocation.billing

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.moggot.findmycarlocation.App.Companion.TAG
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class BillingManager @Inject constructor(private val mActivity: Activity) {

     private val mBillingClient: BillingClient  = BillingClient.newBuilder(mActivity)
         .setListener { result, purchases ->
             if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                 for (purchase in purchases) {
                     if (purchase.isAutoRenewing) {
                         showAds = false
                     }
                 }
             }
         }.build()

     private var mBillingReadyListener: BillingReadyListener? = null
     var billingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED
         private set
     private var showAds = true
     fun startConnection() {
         mBillingClient.startConnection(object : BillingClientStateListener {
             override fun onBillingServiceDisconnected() {
                 mBillingClient.startConnection(this)
             }

             override fun onBillingSetupFinished(result: BillingResult) {
                 billingClientResponseCode = result.responseCode
                 if (billingClientResponseCode == BillingClient.BillingResponseCode.OK) {
                     if (!mBillingClient.isReady) {
                         Timber.tag(TAG).e("queryPurchases: BillingClient is not ready")
                     }
                     mBillingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()) { billingResult, purchaseList ->
                         for (purchase in purchaseList) {
                             if (purchase.isAutoRenewing) {
                                 showAds = false
                             }
                             if (mBillingReadyListener != null) {
                                 mBillingReadyListener!!.billingReady()
                             }
                         }
                     }
                 }
             }
         })
     }

     val isPremium: Boolean
         get() = !showAds

     fun setAdsShowListener(billingReadyListener: BillingReadyListener?) {
         mBillingReadyListener = billingReadyListener
     }

     fun requestSubscription() {
         val params = QueryProductDetailsParams.newBuilder()
         val productList = mutableListOf<QueryProductDetailsParams.Product>()
         val listOfProducts = listOf(SKU_NAME)
         for (product in listOfProducts) {
             productList.add(
                 QueryProductDetailsParams.Product.newBuilder()
                     .setProductId(product)
                     .setProductType(BillingClient.ProductType.SUBS)
                     .build()
             )

             params.setProductList(productList).let { productDetailsParams ->
                 Timber.tag(TAG).i("queryProductDetailsAsync")
                 mBillingClient.queryProductDetailsAsync(productDetailsParams.build(), object : ProductDetailsResponseListener {
                     override fun onProductDetailsResponse(
                         result: BillingResult,
                         skuDetailsList: MutableList<ProductDetails>,
                     ) {
//                         val responseCode = result.responseCode
//                         if (responseCode == BillingClient.BillingResponseCode.OK) {
//                             for (skuDetails: ProductDetails in skuDetailsList) {
//                                 val offers = skuDetails.subscriptionOfferDetails?.let {
//                                     retrieveEligibleOffers(offerDetails = it)
//                                 }
//                                 val offerToken = offers?.let { leastPricedOfferToken(it) }
//                                 val builder = BillingFlowParams.newBuilder().setProductDetailsParamsList(
//                                     listOf(
//                                         BillingFlowParams.ProductDetailsParams.newBuilder()
//                                             .setProductDetails(skuDetails)
//                                             .setOfferToken(offerToken)
//                                             .build()
//                                     )
//                                 )
//                                 if (mBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).responseCode
//                                     == BillingClient.BillingResponseCode.OK
//                                 ) {
//                                     mBillingClient.launchBillingFlow(mActivity, builder.build())
//                                 }
//                             }
//                         } else {
//                             startConnection()
//                         }
                         Log.d(TAG, "onProductDetailsResponse: $skuDetailsList")
                     }
                 })
             }
         }
     }

    private fun retrieveEligibleOffers(
        offerDetails: MutableList<ProductDetails.SubscriptionOfferDetails>,
        tag: String
    ): List<ProductDetails.SubscriptionOfferDetails> {
        val eligibleOffers = emptyList<ProductDetails.SubscriptionOfferDetails>().toMutableList()
        offerDetails.forEach { offerDetail ->
            if (offerDetail.offerTags.contains(tag)) {
                eligibleOffers.add(offerDetail)
            }
        }

        return eligibleOffers
    }

     fun destroy() {
         if (mBillingClient.isReady) {
             mBillingClient.endConnection()
         }
     }

     companion object {
         const val BILLING_MANAGER_NOT_INITIALIZED = -1
         private const val SKU_NAME = "ads_disable_subscription"
     }
}
