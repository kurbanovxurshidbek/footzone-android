package com.footzone.footzone

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.footzone.footzone.databinding.ChooseLanguageDialogBinding

class ChooseLanguageDialog(private var onEnterClick: ((String) -> Unit)) {

    fun showChooseLanguageDialog(activity: Activity?) {

        val binding = ChooseLanguageDialogBinding.inflate(LayoutInflater.from(activity))
        val dialog = Dialog(activity!!)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        binding.llUz.setOnClickListener {
            onEnterClick.invoke( "uz")
            dialog.dismiss()
        }

        binding.llRu.setOnClickListener {
            onEnterClick.invoke( "ru")
            dialog.dismiss()
        }

        binding.llEng.setOnClickListener {
            onEnterClick.invoke( "en")
            dialog.dismiss()
        }
        dialog.show()
    }

}