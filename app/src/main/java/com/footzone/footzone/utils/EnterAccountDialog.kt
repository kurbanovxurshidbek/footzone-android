package com.footzone.footzone.utils

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.footzone.footzone.R

class EnterAccountDialog(
    private val context1: Context,
    private val onEnterClick: (() -> Unit)
) : Dialog(context1) {
    private var _instance: EnterAccountDialog? = null

    fun instance(layoutResID: ConstraintLayout): EnterAccountDialog {
        if (_instance == null) {
            _instance = this
        }
        setLayout(layoutResID)
        return _instance!!
    }

    private fun setLayout(layoutResID: ConstraintLayout) {
        _instance!!.setContentView(layoutResID)
        _instance!!.window!!.setLayout(
            pxFromDp(context1, 312).toInt(),
            pxFromDp(context1, 200).toInt()
        )
        _instance!!.window!!.setBackgroundDrawableResource(R.drawable.rounded_view)

        val tvYes = layoutResID.findViewById<TextView>(R.id.tvEnter)

        tvYes.setOnClickListener {
            onEnterClick.invoke()
        }
    }

    private fun pxFromDp(context: Context, dp: Int): Float {
        return dp * context.resources.displayMetrics.density
    }
}