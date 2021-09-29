package com.maickonn.easyinapppurchasesexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.maickonn.easyinapppurchasesexample.databinding.ActivityMainBinding;
import com.maickonn.EasyInAppPurchases;

import java.util.ArrayList;

public class MainActivityJava extends AppCompatActivity {
    private ActivityMainBinding binding;
    private EasyInAppPurchases easyInAppPurchases;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupEasyInAppPurchases();
        setupButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        easyInAppPurchases.queryPurchases();
    }

    private void setupEasyInAppPurchases() {
        ArrayList<String> skuList = new ArrayList<>();
        skuList.add("item1");
        skuList.add("item2");
        skuList.add("item3");
        skuList.add("subs1");
        skuList.add("subs2");
        skuList.add("subs3");

        easyInAppPurchases = new EasyInAppPurchases(this, skuList, new EasyInAppPurchases.EasyInAppPurchasesCallback() {
            @Override
            public void onProductPurchased(@NonNull Purchase purchase) {
                if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED)
                    return;

                // Add your purchase control here
                Toast.makeText(getBaseContext(), "You bought ${purchase.sku}", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGetProductDetails(@NonNull SkuDetails skuDetails) {
                // SkuDetails usage example
                if (!skuDetails.getSku().equals("item1"))
                    return;

                String price = skuDetails.getPrice();
                String sku = skuDetails.getSku();
                binding.purchaseButton.setText(String.format("Launch purchase - %s (%s)", sku, price));
            }

            @Override
            public void onProductRestored(@NonNull Purchase purchase) {
                // Example of use when you have an active purchase
                Toast.makeText(getBaseContext(), "Purchase found: " + purchase.getSku(), Toast.LENGTH_SHORT).show();
                if (purchase.getSku().equals("item1") && getSupportActionBar() != null)
                    getSupportActionBar().setTitle(getString(R.string.app_name) + " (Premium)");
            }

            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String purchaseToken) {
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.app_name);
                Toast.makeText(getBaseContext(), "The purchase has been consumed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupButtons() {
        binding.purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                easyInAppPurchases.launchPurchase("item1");
            }
        });

        binding.queryPurchasesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                easyInAppPurchases.queryPurchases();
            }
        });

        binding.consumePurchasesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                easyInAppPurchases.consumePurchase("item1");
            }
        });

        binding.javaKotlinExampleButton.setText("Go to Kotlin Example");
        binding.javaKotlinExampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityJava.this, MainActivityKotlin.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
