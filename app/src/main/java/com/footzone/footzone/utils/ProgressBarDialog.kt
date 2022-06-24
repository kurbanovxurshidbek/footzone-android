package com.footzone.footzone.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.footzone.footzone.R

class ProgressBarDialog(context: Context) : Dialog(context) {
    init {
        val wlmp = window!!.attributes
        wlmp.gravity = Gravity.CENTER_HORIZONTAL
        window!!.attributes = wlmp
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setTitle(null)
        setCancelable(false)
        setOnCancelListener(null)
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.dialog_lottie, null
        )
        setContentView(view)
    }
}