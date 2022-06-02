package com.footzone.footzone.ui.activity

import android.Manifest
import android.app.Activity
import android.content.*
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import com.footzone.footzone.R
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.MyBackgroundService
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        registerReceiver(locationReceiver, IntentFilter("location.update"))
        val intent = Intent(this, MyBackgroundService::class.java)
        startService(intent)

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