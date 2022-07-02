package com.footzone.footzone.helper

import android.widget.LinearLayout
import android.widget.TextView
import java.text.FieldPosition

interface OnClickEventAcceptDecline {
    fun onAccept(stadiumId: String, tvStatus: TextView, linearAcceptDecline: LinearLayout,position: Int)
    fun onDecline(stadiumId: String, tvStatus: TextView, linearAcceptDecline: LinearLayout,position: Int)
}