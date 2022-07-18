package com.footzone.footzone.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class InternetBroadcastReceiver : BroadcastReceiver() {

    var onInternetOff:(()->Unit)? = null
    var onInternetOn:(()->Unit)? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (isConnectingToInternet(context!!)) {
           onInternetOn!!.invoke()
        } else {
            onInternetOff!!.invoke()
        }
    }

    private fun isConnectingToInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}