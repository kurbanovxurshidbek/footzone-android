package com.footzone.footzone.utils

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.widget.Toast

class MyBackgroundService : Service(), LocationHelper.LocationListener {

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }


    private lateinit var locationHelper: LocationHelper
    override fun onCreate() {
        Toast.makeText(this, "create", Toast.LENGTH_SHORT).show()
        super.onCreate()
        locationHelper = LocationHelper()
        locationHelper.addLocationlistener(this)
        locationHelper.startLocationUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        val intent = Intent("location.update")
        intent.putExtra("location", location)
        sendBroadcast(intent)
    }


}