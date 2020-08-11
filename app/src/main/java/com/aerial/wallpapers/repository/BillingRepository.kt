package com.aerial.wallpapers.repository

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.aerial.wallpapers.R
import com.aerial.wallpapers.di.PersistStorage
import com.aerial.wallpapers.network.AppApi
import com.aerial.wallpapers.storage.Storage
import com.android.billingclient.api.*
import java.io.IOException
import java.time.Period
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class BillingRepository @Inject constructor(
    private val appApi: AppApi,
    @PersistStorage val storage: Storage,
    val context: Context
) : PurchasesUpdatedListener {

    private val billingClient: BillingClient
    private var pendingSubscribeSku: String? = null
    private var pendingSubscribeCallback: ((Boolean) -> Unit)? = null

    val subscribed = MutableLiveData<Boolean>()

    init {
        subscribed.postValue(storage.getBoolean(Storage.KEY_SUBSCRIPTION_PURCHASED))

        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                    return
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d("Billing", "error")
            }
        })
    }

    suspend fun getProductIds() = appApi.getProducts()

    suspend fun getSubscriptionPrices(products: List<String>): HashMap<String, String> {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(products)
            .setType(BillingClient.SkuType.SUBS)
            .build()

        val skuDetailsList = getPrices(params) ?: throw IOException("Empty skuDelailsList")

        val result = HashMap<String, String>()

        for (productId in products) {
            for (skuDetails in skuDetailsList) {
                if (productId == skuDetails.sku) {
                    var periodStr = ""

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val period = Period.parse(skuDetails.subscriptionPeriod)

                        if (period.years > 0) {
                            periodStr = "/${context.getString(R.string.year)}"
                        } else if (period.months > 0) {
                            periodStr = "/${context.getString(R.string.month)}"
                        } else {
                            periodStr = "/${context.getString(R.string.week)}"
                        }
                    } else {
                        periodStr = when (skuDetails.subscriptionPeriod[2]) {
                            "Y"[0] -> "/${context.getString(R.string.year)}"
                            "M"[0] -> "/${context.getString(R.string.month)}"
                            "W"[0] -> "/${context.getString(R.string.week)}"
                            else -> ""
                        }
                    }

                    result[productId] = skuDetails.price + periodStr
                }
            }
        }

        return result
    }

    private suspend fun getPrices(params: SkuDetailsParams): MutableList<SkuDetails>? {
        return suspendCoroutine { continuation ->
            billingClient.querySkuDetailsAsync(params, object : SkuDetailsResponseListener {
                override fun onSkuDetailsResponse(result: BillingResult?, skuDetailsList: MutableList<SkuDetails>?) {
                    continuation.resume(skuDetailsList)
                }
            })
        }
    }

    suspend fun subscribe(activity: Activity, product: String, callback: (Boolean) -> Unit): Boolean {
        if (pendingSubscribeSku != null) {
            return false
        }

        if (getIsSubscribed(product)) {
            subscribed.postValue(true)
            callback(true)
            return true
        }

        val params = SkuDetailsParams.newBuilder()
            .setSkusList(listOf(product))
            .setType(BillingClient.SkuType.SUBS)
            .build()

        val skuDetailsList = getPrices(params) //?: throw IOException("Empty skuDelailsList")

        if (skuDetailsList == null) {
            callback(false)
            return false
        }

        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetailsList[0])
            .build()

        pendingSubscribeSku = product
        pendingSubscribeCallback = callback

        billingClient.launchBillingFlow(activity, flowParams)

        return true
    }

    private fun getIsSubscribed(product: String): Boolean {
        val purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS)

        if (purchases.responseCode != BillingClient.BillingResponseCode.OK) {
            return false
        }

        for (purchase in purchases.purchasesList) {
            if (purchase.sku == product && purchase.isAutoRenewing && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                return true
            }
        }

        return false
    }

    override fun onPurchasesUpdated(result: BillingResult?, purchases: MutableList<Purchase>?) {
        if (purchases == null) {
            return
        }

        for (purchase in purchases) {
            if (purchase.sku == pendingSubscribeSku && purchase.isAutoRenewing) {
                if (result?.responseCode == BillingClient.BillingResponseCode.OK) {
                    subscribed.postValue(true)
                    pendingSubscribeCallback?.invoke(true)
                } else {
                    pendingSubscribeCallback?.invoke(false)
                }
                pendingSubscribeSku = null
                pendingSubscribeCallback = null
            }
        }
    }

}