package com.footzone.footzone.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.footzone.footzone.R
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.MyBackgroundService

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        registerReceiver(locationReciever, IntentFilter("location.update"))
        val intent = Intent(this, MyBackgroundService::class.java)
        startService(intent)
    }

    private val locationReciever = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1 != null) {
                if (p1.action == "location.update") {
                    Toast.makeText(this@SplashActivity, "location", Toast.LENGTH_SHORT).show()
                    val location = p1.getParcelableExtra<Location>("location") as Location
                    KeyValues.location = location.toString()
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationReciever)
    }
}