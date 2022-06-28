package com.footzone.footzone.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

class DeclineNotificationReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Rad etildi", Toast.LENGTH_SHORT).show()

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.cancel(1)
    }
}