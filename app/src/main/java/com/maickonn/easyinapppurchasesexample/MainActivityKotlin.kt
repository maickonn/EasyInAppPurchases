package com.maickonn.easyinapppurchasesexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.maickonn.easyinapppurchasesexample.databinding.ActivityMainBinding
import com.maickonn.EasyInAppPurchases
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.BillingResult


class MainActivityKotlin : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var easyInAppPurchases: EasyInAppPurchases? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupEasyInAppPurchases()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        easyInAppPurchases?.queryPurchases()
    }

    private fun setupEasyInAppPurchases() {
        val skuList: ArrayList<String> = ArrayList()
        skuList.add("item1")
        skuList.add("item2")
        skuList.add("item3")
        skuList.add("subs1")
        skuList.add("subs2")
        skuList.add("subs3")

        easyInAppPurchases =  EasyInAppPurchases(this, skuList, object : EasyInAppPurchases.EasyInAppPurchasesCallback {
            override fun onProductPurchased(purchase: Purchase) {
                if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED)
                    return

                // Add your purchase control here
                Toast.makeText(baseContext, "You bought ${purchase.sku}", Toast.LENGTH_SHORT).show()
            }

            override fun onGetProductDetails(skuDetails: SkuDetails) {
                // SkuDetails usage example
                if (skuDetails.sku != "item1")
                    return

                val price = skuDetails.price
                val sku = skuDetails.sku
                binding.purchaseButton.text = "Launch Purchase - $sku ($price)"
            }

            override fun onProductRestored(purchase: Purchase) {
                // Example of use when you have an active purchase
                Toast.makeText(baseContext, "Purchase found: ${purchase.sku}", Toast.LENGTH_SHORT).show()
                if (purchase.sku == "item1")
                    supportActionBar?.title = "${getString(R.string.app_name)} (Premium)"
            }

            override fun onConsumeResponse(billingResult: BillingResult, purchaseToken: String) {
                supportActionBar?.title = getString(R.string.app_name)
                Toast.makeText(baseContext, "The purchase has been consumed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupButtons() {
        binding.purchaseButton.setOnClickListener {
            easyInAppPurchases?.launchPurchase("item1")
        }

        binding.queryPurchasesButton.setOnClickListener {
            easyInAppPurchases?.queryPurchases()
        }

        binding.consumePurchasesButton.setOnClickListener {
            easyInAppPurchases?.consumePurchase("item1")
        }

        binding.javaKotlinExampleButton.text = "Go to Java Example"
        binding.javaKotlinExampleButton.setOnClickListener {
            val intent = Intent(this, MainActivityJava::class.java)
            startActivity(intent)
            finish()
        }
    }
}