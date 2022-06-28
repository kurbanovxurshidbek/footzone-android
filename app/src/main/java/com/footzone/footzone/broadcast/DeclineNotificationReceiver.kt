package com.footzone.footzone.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.viewModels
import com.footzone.footzone.backgroundservice.AcceptService
import com.footzone.footzone.ui.fragments.home.HomeViewModel

class DeclineNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        val id: String

        if (extras != null) {
            id = extras.getString("sessionId")!!
            Log.d("TAG", "onReceive: $id")
            val notificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManagerCompat.cancel(1)
            val intent2 = Intent(context, AcceptService::class.java)
            intent2.putExtra("sessionId", id)
            context.startService(intent2)
        }
    }
}