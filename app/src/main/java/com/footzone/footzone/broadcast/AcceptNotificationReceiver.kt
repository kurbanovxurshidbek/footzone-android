package com.footzone.footzone.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

class AcceptNotificationReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Qabul qilindi", Toast.LENGTH_SHORT).show()
        Log.d("TAG", "onReceive: ")
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.cancel(1)
    }
}