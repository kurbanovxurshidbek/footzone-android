package com.footzone.footzone.utils

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.footzone.footzone.R

class AcceptDialog(
    private val context1: Context,
    private val onAcceptClick: (() -> Unit)
) : Dialog(context1) {
    private var _instance: AcceptDialog? = null

    fun instance(layoutResID: ConstraintLayout): AcceptDialog {
        if (_instance == null) {
            _instance = this
        }
        setLayout(layoutResID)
        return _instance!!
    }

    private fun setLayout(layoutResID: ConstraintLayout) {
        _instance!!.setContentView(layoutResID)
        _instance!!.window!!.setLayout(
            pxFromDp(context1, 320).toInt(),
            pxFromDp(context1, 200).toInt()
        )
        _instance!!.window!!.setBackgroundDrawableResource(R.drawable.rounded_view)

        val tvYes = layoutResID.findViewById<TextView>(R.id.tvYes)
        val tvNo = layoutResID.findViewById<TextView>(R.id.tvNo)

        tvYes.setOnClickListener {
            onAcceptClick.invoke()
        }

        tvNo.setOnClickListener {
            _instance!!.dismiss()
        }
    }

    private fun pxFromDp(context: Context, dp: Int): Float {
        return dp * context.resources.displayMetrics.density
    }
}