package com.footzone.footzone.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import com.footzone.footzone.databinding.ToastChooseTimeBinding
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.SharedPref
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    @Inject
    lateinit var sharedPref: SharedPref
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
    }

    fun permissionRequest() {
        if (isLocationPermissionGranted()
        ) {
            showLocationOn()
        } else {
            request()
        }
    }

    fun request() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) { /* ... */
                    openMainActivity()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) { /* ... */
                    request()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }
            }).check()
    }

    open fun openMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(KeyValues.SPLASH_MESSAGE, true)
        startActivity(intent)
        finish()
    }

    open fun isLocationPermissionGranted(): Boolean =
        PermissionChecker.checkCallingOrSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED

    private fun showLocationOn() {
        val locationRequest = LocationRequest.create()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 30 * 1000.toLong()
            fastestInterval = 5 * 1000.toLong()
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result =
            LocationServices.getSettingsClient(context!!)
                .checkLocationSettings(builder.build())
        result.addOnCompleteListener {
            try {
                val response: LocationSettingsResponse = it.getResult(ApiException::class.java)
                if (response.locationSettingsStates!!.isGpsPresent) {
                    openMainActivity()
                }
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(e.status.resolution!!).build()
                        launcher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                    }
                }
            }
        }
    }

    open fun loadFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new FCM registration token
            // Save it in locally to use later
            val token = task.result
            sharedPref.saveFirebaseToken(KeyValues.FIREBASE_TOKEN, token.toString())
        })
    }

    private var launcher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                openMainActivity()
            } else {
                showLocationOn()
            }
        }

    open fun showToast(message: String, duration: Int) {
        val binding =
            ToastChooseTimeBinding.inflate(LayoutInflater.from(context))

        binding.tvToast.text = message
        val customToast = Toast(context)
        customToast.duration = duration
        customToast.view = binding.root
        customToast.show()
    }
}