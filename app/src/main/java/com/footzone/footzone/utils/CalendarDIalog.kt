package com.footzone.footzone.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.footzone.footzone.databinding.CalendarDialogBinding
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.util.*


/**
 * In the CalendarDIalog class, the stadium reservation calendar opens
 */

class CalendarDIalog(private var onEnterClick: ((String, Int) -> Unit)) {
    lateinit var dateChoose: String
    var isCheck = false
    var dayOfWeek = 0

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

        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(binding.root)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(true)
        }

        binding.calendarView.setOnDateChangeListener { view, year, month, day ->
            val monthad: String = DateFormatSymbols().months[month]
            dateChoose = "$day ${monthad.lowercase()} $year"
            val calendar = Calendar.getInstance()
            calendar[year, month] = day
            dayOfWeek = calendar[Calendar.DAY_OF_WEEK]
            isCheck = true
        }

        binding.tvSelection.setOnClickListener {
            if (isCheck) {
                onEnterClick.invoke(dateChoose, dayOfWeek)

                dialog.dismiss()
            } else {
                Toast.makeText(activity, "O'yin kunini tanlang", Toast.LENGTH_SHORT).show()
            }
        }

        mCalendar.set(year, month.value - 1, day)

        binding.calendarView.minDate = mCalendar.timeInMillis

        mCalendar.set(year, month.value - 1, day + 7)

        binding.calendarView.maxDate = mCalendar.timeInMillis

        binding.tvCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}