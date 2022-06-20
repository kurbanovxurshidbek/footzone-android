package com.footzone.footzone.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.footzone.footzone.R
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.MyBackgroundService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        registerReceiver(locationReceiver, IntentFilter("location.update"))
        val intent = Intent(this, MyBackgroundService::class.java)
        startService(intent)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        permissionRequest()
    }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1 != null) {
                if (p1.action == "location.update") {
                    Toast.makeText(this@SplashActivity, "location", Toast.LENGTH_SHORT).show()
                    val location = p1.getParcelableExtra<Location>("location") as Location
                    KeyValues.LOCATION = location.toString()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationReceiver)
    }
}