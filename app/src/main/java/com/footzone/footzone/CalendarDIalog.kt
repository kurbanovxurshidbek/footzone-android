package com.footzone.footzone

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.footzone.footzone.databinding.CalendarDialogBinding

class CalendarDIalog(private var onEnterClick: ((String) -> Unit)) {

    fun showOneIDLoginDialog(activity: Activity?) {

        val binding = CalendarDialogBinding.inflate(LayoutInflater.from(activity))
        val dialog = Dialog(activity!!)
        val resources = activity.resources

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        binding.tvSelection.setOnClickListener {

            onEnterClick.invoke("odil")
            dialog.dismiss()

        }

        binding.tvCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

}