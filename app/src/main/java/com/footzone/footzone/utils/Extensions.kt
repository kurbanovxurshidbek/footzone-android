package com.footzone.footzone.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.footzone.footzone.R
import com.google.android.material.bottomsheet.BottomSheetBehavior

object Extensions {
    fun TextView.changeTextColorRed() {
        this.setTextColor(resources.getColor(R.color.red))
    }

    fun TextView.changeTextColorYellow() {
        this.setTextColor(resources.getColor(R.color.yellow))
    }

    fun TextView.changeTextColorGreen() {
        this.setTextColor(resources.getColor(R.color.green))
    }

    fun TextView.changeTextBackgroundBlue(boolStart: Boolean, boolFinish: Boolean) {
        if (boolStart && boolFinish) {
            this.setBackgroundResource(R.drawable.view_rounded_corners_blue)
            this.setTextColor(resources.getColor(R.color.white))
        } else {
            this.setBackgroundResource(R.drawable.view_rounded_corners_grey)
            this.setTextColor(resources.getColor(R.color.white))
        }
    }


    fun ImageView.setImageViewBusy() {
        this.setImageResource(R.drawable.ic_busy)
    }

    fun ImageView.setImageViewisBusy() {
        this.setImageResource(R.drawable.ic_is_busy)
    }

    fun BottomSheetBehavior<View>.showBottomSheet(){
        this.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun BottomSheetBehavior<View>.hideBottomSheet(){
        this.state = BottomSheetBehavior.STATE_HIDDEN
    }
}