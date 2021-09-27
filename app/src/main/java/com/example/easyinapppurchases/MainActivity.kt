package com.example.easyinapppurchases

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.easyinapppurchases.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bindind: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindind = ActivityMainBinding.inflate(layoutInflater)
        val view = bindind.root
        setContentView(view)

        bindind.purchaseButton.setOnClickListener {

        }
    }
}