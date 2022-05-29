package com.footzone.footzone

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.Window
import androidx.annotation.RequiresApi
import com.footzone.footzone.databinding.CalendarDialogBinding
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.util.*


/**
 * In the CalendarDIalog class, the stadium reservation calendar opens
 */

class CalendarDIalog(private var onEnterClick: ((String) -> Unit)) {
    lateinit var dateChoose: String

    @RequiresApi(Build.VERSION_CODES.O)
    fun showCalendarDialog(activity: Activity?) {
        val mCalendar = Calendar.getInstance()
        val currentdate: LocalDate = LocalDate.now()
        val day = currentdate.getDayOfMonth();
        val month = currentdate.month
        val year = currentdate.getYear()
        dateChoose = "$day ${month.toString().lowercase()} $year"

        val binding = CalendarDialogBinding.inflate(LayoutInflater.from(activity))
        val dialog = Dialog(activity!!)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        binding.calendarView.setOnDateChangeListener { _, year, month, date ->
            val monthad: String = DateFormatSymbols().getMonths().get(month - 1)
            dateChoose = "$day ${monthad.lowercase()} $year"
        }

        binding.tvSelection.setOnClickListener {

            onEnterClick.invoke(dateChoose)

            dialog.dismiss()
        }

        mCalendar.set(year, month.value - 1, day)

        binding.calendarView.minDate = mCalendar.timeInMillis

        mCalendar.set(year, month.value - 1, day + 7)

        binding.calendarView.maxDate = mCalendar.timeInMillis

        binding.tvCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

}