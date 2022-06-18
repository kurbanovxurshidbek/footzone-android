package com.footzone.footzone.helper

import android.widget.LinearLayout
import android.widget.TextView

interface OnClickEventAcceptDecline {
    fun onAccept(stadiumId: String, tvStatus: TextView, linearAcceptDecline: LinearLayout)
    fun onDecline(stadiumId: String, tvStatus: TextView, linearAcceptDecline: LinearLayout)
}