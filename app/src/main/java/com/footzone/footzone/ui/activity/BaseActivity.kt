package com.footzone.footzone.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
    }

    fun permissionRequest() {
        if (isLocationPermissionGranted()
        ) {
            openMainActivity()
        } else {
            request()
        }
    }

    private fun request() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) { /* ... */
                    openMainActivity()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) { /* ... */
                    openMainActivity()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }
            }).check()
    }

    private fun openMainActivity() {
        startActivity(Intent(context, MainActivity::class.java))
    }

    open fun isLocationPermissionGranted(): Boolean =
        PermissionChecker.checkCallingOrSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED
}