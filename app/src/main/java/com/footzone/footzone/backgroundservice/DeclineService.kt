package com.footzone.footzone.backgroundservice

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log


class DeclineService :
    Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("TAG", "onDestroy: stopped")
    }
}