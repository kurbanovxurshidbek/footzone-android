package com.footzone.footzone.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.footzone.footzone.R
import com.footzone.footzone.broadcast.AcceptNotificationReceiver
import com.footzone.footzone.broadcast.DeclineNotificationReceiver
import com.footzone.footzone.model.SessionNotificationResponse
import com.footzone.footzone.ui.activity.MainActivity
import com.footzone.footzone.utils.KeyValues
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlin.random.Random


class FCMService : FirebaseMessagingService() {

    private val CHANNEL_ID: String = "Something"
    val TAG = FCMService::class.java.simpleName

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "Refreshed Token:: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.i(TAG, "Message: ${message.data}")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(KeyValues.NOTIFICATION_TITLE, message.data["title"])
        val requestCode = (0..10).random()
        val pendingIntent = PendingIntent.getActivity(this, requestCode, intent, FLAG_IMMUTABLE)

        val session =
            Gson().fromJson(message.data["session"], SessionNotificationResponse::class.java)

        val ACCEPT_ACTION = "Accept"
        val acceptIntent = Intent(this, AcceptNotificationReceiver::class.java)
        acceptIntent.action = ACCEPT_ACTION
        acceptIntent.putExtra("sessionId", session.sessionId)
        val acceptPendingIntent =
            PendingIntent.getBroadcast(this, 0, acceptIntent, FLAG_IMMUTABLE)

        val DECLINE_ACTION = "Decline"
        val declineIntent = Intent(this, DeclineNotificationReceiver::class.java)
        declineIntent.action = DECLINE_ACTION
        declineIntent.putExtra("sessionId", session.sessionId)
        val declinePendingIntent =
            PendingIntent.getBroadcast(this, 0, declineIntent, FLAG_IMMUTABLE)

        val notificationUser = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["body"])
            .setContentText(session.stadiumName + " · " + session.startDate + " · " + session.startTime.subSequence(0, 5) + "-" + session.endTime.subSequence(0, 5))
            .setSmallIcon(R.drawable.ic_ball)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_product_logo))
            .setStyle(
                NotificationCompat.DecoratedCustomViewStyle()
            )
            .setAutoCancel(true)
            .build()
        notificationUser.color = resources.getColor(R.color.colorBlue600)

        val collapsedView = RemoteViews(packageName, R.layout.notification_collapsed)
        val expandedView = RemoteViews(packageName, R.layout.notification_expanded)
        collapsedView.setTextViewText(R.id.tvBody, message.data["body"])
        collapsedView.setTextViewText(R.id.tvTitle, session.stadiumName + " · " + session.startDate + " · " + session.endTime.subSequence(0, 5) + "-" + session.startTime.subSequence(0, 5))
        expandedView.setTextViewText(R.id.tvBodyExpanded, message.data["body"])

        val notificationAdmin = NotificationCompat.Builder(this, CHANNEL_ID)
            .setCustomContentView(collapsedView)
            .setCustomBigContentView(expandedView)
            .setSmallIcon(R.drawable.ic_ball)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_product_logo))
            .setStyle(
                NotificationCompat.DecoratedCustomViewStyle()
            )
            .setAutoCancel(true)
            .addAction(R.drawable.ic_ball, "Qabul qilish", acceptPendingIntent)
            .addAction(R.drawable.ic_ball, "Rad etish", declinePendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel(manager)
        val notificationId = Random.nextInt()

        if (session.stadiumHolder) {
            manager.notify(notificationId, notificationAdmin)
        } else {
            manager.notify(notificationId, notificationUser)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(manager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.BLUE
        }
        manager.createNotificationChannel(channel)
    }
}