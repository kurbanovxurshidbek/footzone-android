package com.footzone.footzone.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.footzone.footzone.model.AcceptDeclineRequest
import com.footzone.footzone.repository.main.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class AcceptNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var mainRepository: MainRepository

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        val sessionId: String

        if (extras != null) {
            sessionId = extras.getString("sessionId")!!
            val notificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManagerCompat.cancel(0)

            sendAcceptRequest(sessionId)
        }
    }

    private fun sendAcceptRequest(sessionId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            mainRepository.acceptOrDeclineBookingRequest(AcceptDeclineRequest(true, sessionId))
        }
    }
}