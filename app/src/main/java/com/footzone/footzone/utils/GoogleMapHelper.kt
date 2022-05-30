package com.footzone.footzone.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast


object GoogleMapHelper {
    fun Activity.shareLocationToGoogleMap(latitude: Double, longitude: Double) {
        try {
            val uriYandex = "yandexnavi://build_route_on_map?lat_to=${latitude}&lon_to=${longitude}"
            val intentYandex = Intent(Intent.ACTION_VIEW, Uri.parse(uriYandex))
            intentYandex.setPackage("ru.yandex.yandexnavi")

            val uriGoogle = Uri.parse("google.navigation:q=${latitude},${longitude}&mode=w")
            val intentGoogle = Intent(Intent.ACTION_VIEW, uriGoogle)
            intentGoogle.setPackage("com.google.android.apps.maps")

            val chooserIntent =
                Intent.createChooser(intentYandex, "Yo'lanishni ko'rish uchun tanlang")
            val arr = arrayOfNulls<Intent>(2)
            arr[0] = intentYandex
            arr[1] = intentGoogle

            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arr)
            val activities = packageManager.queryIntentActivities(chooserIntent, 0)
            if (activities.size > 0) {
                startActivity(chooserIntent)
            } else {
                Toast.makeText(
                    this,
                    "Sizda berilgan manzilga mashrutni ko'rsatadigan ilova o'rnatilmagan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {

        }
    }
}