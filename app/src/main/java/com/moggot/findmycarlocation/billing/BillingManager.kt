package com.moggot.findmycarlocation.billing

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.moggot.findmycarlocation.App.Companion.TAG
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber

@Singleton
class BillingManager @Inject constructor(private val mActivity: Activity) {

    private val _purchases = MutableStateFlow<List<Purchase>>(listOf())
    val isPremium: Flow<Boolean> = _purchases.asStateFlow().map { it.isNotEmpty() }

    private val mBillingClient: BillingClient = BillingClient.newBuilder(mActivity)
        .enablePendingPurchases()
        .setListener { result, purchases: MutableList<Purchase>? ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                _purchases.value = purchases
            }
        }.build()

    var billingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED
        private set

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
                    mBillingClient.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS).build()
                    ) { billingResult, purchaseList ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            if (purchaseList.isNotEmpty()) {
                                _purchases.value = purchaseList
                            } else {
                                _purchases.value = emptyList()
                            }
                        } else {
                            Timber.tag(TAG).e(billingResult.debugMessage)
                        }
                    }
                }
            }
        })
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
                mBillingClient.queryProductDetailsAsync(
                    productDetailsParams.build()
                ) { result, skuDetailsList ->
                    val responseCode = result.responseCode
                    if (responseCode == BillingClient.BillingResponseCode.OK) {
                        for (skuDetails: ProductDetails in skuDetailsList) {
                            val offers: List<ProductDetails.SubscriptionOfferDetails>? =
                                skuDetails.subscriptionOfferDetails
                            val offerToken = offers?.firstOrNull()
                            offerToken?.let { token: ProductDetails.SubscriptionOfferDetails ->
                                val builder = BillingFlowParams.newBuilder()
                                    .setProductDetailsParamsList(
                                        listOf(
                                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                                .setProductDetails(skuDetails)
                                                .setOfferToken(token.offerToken)
                                                .build()
                                        )
                                    )
                                if (mBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).responseCode
                                    == BillingClient.BillingResponseCode.OK
                                ) {
                                    mBillingClient.launchBillingFlow(
                                        mActivity,
                                        builder.build()
                                    )
                                }
                            }
                        }
                    } else startConnection()
                }
            }
        }
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
