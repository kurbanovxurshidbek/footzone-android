package com.footzone.footzone.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.footzone.footzone.R
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.MyBackgroundService

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        registerReceiver(locationReceiver, IntentFilter("location.update"))
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val intent = Intent(this, MyBackgroundService::class.java)
            startService(intent)
        } else {
            //gpsni yoqing
            
        }

    }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1 != null) {
                if (p1.action == "location.update") {
                    Toast.makeText(this@SplashActivity, "location", Toast.LENGTH_SHORT).show()
                    val location = p1.getParcelableExtra<Location>("location") as Location
                    KeyValues.LOCATION = location.toString()
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationReceiver)
    }
}