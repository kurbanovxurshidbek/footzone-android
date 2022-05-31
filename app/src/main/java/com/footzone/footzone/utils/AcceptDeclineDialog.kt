package com.footzone.footzone.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.footzone.footzone.R
import com.footzone.footzone.databinding.LayoutAcceptDeclineBinding

class AcceptDeclineDialog(val context1: Context) : Dialog(context1) {
    private var _instance: AcceptDeclineDialog? = null
    val binding = LayoutAcceptDeclineBinding.inflate(LayoutInflater.from(context1))

    fun instance(layoutResID: Int): AcceptDeclineDialog {
        if (_instance == null) {
            _instance = this
        }
        setLayout(layoutResID)
        return _instance!!
    }

    private fun setLayout(layoutResID: Int) {
        _instance!!.setContentView(layoutResID)
        _instance!!.window!!.setBackgroundDrawableResource(R.drawable.rounded_view)
        _instance!!.window!!.setLayout(
            pxFromDp(context1, 320).toInt(),
            pxFromDp(context1, 200).toInt()
        )
    }

    fun manageResponse() {
        binding.tvYes.setOnClickListener {
            Toast.makeText(context1, "Ha", Toast.LENGTH_SHORT).show()
        }

        binding.tvNo.setOnClickListener {
            Toast.makeText(context1, "Yo'q", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pxFromDp(context: Context, dp: Int): Float {
        return dp * context.resources.displayMetrics.density
    }
}