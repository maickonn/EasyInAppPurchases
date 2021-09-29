package com.maickonn

import android.app.Activity
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ConsumeParams

open class EasyInAppPurchases(
    private val activity: Activity,
    private val skuList: List<String>,
    private var easyInAppPurchasesCallback: EasyInAppPurchasesCallback?
)
{
    private lateinit var billingClient: BillingClient
    private val inAppSkuDetailsList: ArrayList<SkuDetails> = ArrayList()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK || purchases == null)
            return@PurchasesUpdatedListener

        purchases.forEach {
            if (it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(it.purchaseToken).build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener)
            }
            easyInAppPurchasesCallback?.onProductPurchased(it)
        }
    }

    private val billingClientStateListener = object : BillingClientStateListener{
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK)
                return
            querySkuDetailsList()
            queryPurchases()
        }

        override fun onBillingServiceDisconnected() {
        }
    }

    private val acknowledgePurchaseResponseListener = AcknowledgePurchaseResponseListener {
    }

    private val consumeResponseListener = ConsumeResponseListener {
            billingResult, purchaseToken -> easyInAppPurchasesCallback?.onConsumeResponse(billingResult, purchaseToken)
    }

    private fun querySkuDetailsList() {
        val inAppParams = SkuDetailsParams.newBuilder()
        inAppParams.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(inAppParams.build()) { _, list ->
            if (list != null) {
                inAppSkuDetailsList.addAll(list)
                list.forEach {
                    easyInAppPurchasesCallback?.onGetProductDetails(it)
                }
            }
        }
        val subsParams = SkuDetailsParams.newBuilder()
        subsParams.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        billingClient.querySkuDetailsAsync(subsParams.build()) { _, list ->
            if (list != null) {
                inAppSkuDetailsList.addAll(list)
                list.forEach {
                    easyInAppPurchasesCallback?.onGetProductDetails(it)
                }
            }
        }
    }

    fun queryPurchases() {
        val inAppPurchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        inAppPurchasesResult.purchasesList?.forEach {
            easyInAppPurchasesCallback?.onProductRestored(it)
        }
        val subsPurchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
        subsPurchasesResult.purchasesList?.forEach {
            easyInAppPurchasesCallback?.onProductRestored(it)
        }
    }

    fun launchPurchase(sku: String) {
        inAppSkuDetailsList.forEach {
            if (it.sku == sku) {
                val billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(it).build()
                billingClient.launchBillingFlow(activity, billingFlowParams)
                return
            }
        }
    }

    fun consumePurchase(sku: String) {
        val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        purchasesResult.purchasesList?.forEach {
            if (it.sku == sku) {
                val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(it.purchaseToken).build()
                billingClient.consumeAsync(consumeParams, consumeResponseListener)
                return
            }
        }
    }

    fun setOnInAppPurchaseUtilsCallback(easyInAppPurchasesCallback: EasyInAppPurchasesCallback?) {
        this.easyInAppPurchasesCallback = easyInAppPurchasesCallback
    }

    interface EasyInAppPurchasesCallback {
        fun onProductPurchased(purchase: Purchase)
        fun onGetProductDetails(skuDetails: SkuDetails)
        fun onProductRestored(purchase: Purchase)
        fun onConsumeResponse(billingResult: BillingResult, purchaseToken: String)
    }

    init {
        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient.startConnection(billingClientStateListener)
    }
}