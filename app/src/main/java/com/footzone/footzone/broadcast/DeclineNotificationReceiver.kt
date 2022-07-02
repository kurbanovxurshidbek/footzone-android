package com.footzone.footzone.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.footzone.footzone.model.AcceptDeclineRequest
import com.footzone.footzone.repository.main.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DeclineNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var mainRepository: MainRepository

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        val sessionId: String

        if (extras != null) {
            sessionId = extras.getString("sessionId")!!
            val notificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManagerCompat.cancel(1)

            sendDeclineRequest(sessionId)
        }
    }

    private fun sendDeclineRequest(sessionId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            mainRepository.acceptOrDeclineBookingRequest(AcceptDeclineRequest(false, sessionId))
        }
    }
}