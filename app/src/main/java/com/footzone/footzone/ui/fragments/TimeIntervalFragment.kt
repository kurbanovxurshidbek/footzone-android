package com.footzone.footzone.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.R
import com.footzone.footzone.adapter.TimeManagerAdapter
import com.footzone.footzone.databinding.FragmentTimeIntervalBinding
import com.footzone.footzone.model.TimeManager
import com.footzone.footzone.utils.KeyValues
import com.google.type.DateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeIntervalFragment : Fragment() {
    lateinit var binding: FragmentTimeIntervalBinding
    private lateinit var adapter: TimeManagerAdapter
    var num: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_time_interval, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTimeIntervalBinding.bind(view)
        initViews()
    }

    private fun initViews() {
        val a = IntArray(2)
        a[0] = 49
        a[1] = -1
        adapter = TimeManagerAdapter { p ->

        }

        adapter.submitList(timeManager(allTime(LocalTime.parse("07:00"), LocalTime.parse("23:30")),
            gameTime()))

        binding.recyclerView.adapter = adapter
        binding.ivPinding.setOnClickListener {
            val result = "dscekv r"
            setFragmentResult(KeyValues.TYPE_CHOOSE_TIME, bundleOf("bundleKey" to result))
            requireActivity().onBackPressed()
        }

        binding.icClose.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun allTime(openTime: LocalTime, closeTime: LocalTime): ArrayList<TimeManager> {
        val array = resources.getStringArray(R.array.timelist)
        val halfIntervalHour = ArrayList<LocalTime>()
        val halfIntervalHourView = ArrayList<TimeManager>()

        array.forEach { date ->
            val time = LocalTime.parse(date)
            if (time!!.isBetween(openTime, closeTime)
            ) {
                halfIntervalHour.add(time)
            }
        }

        for (i in 0 until halfIntervalHour.size - 1) {
            halfIntervalHourView.add(TimeManager(halfIntervalHour[i], halfIntervalHour[i + 1]))
        }

        return halfIntervalHourView
    }

    private fun gameTime(): ArrayList<TimeManager> {
        val times = ArrayList<TimeManager>()
        times.add(TimeManager(LocalTime.parse("09:00"), LocalTime.parse("11:00"), "ACCEPTED"))
        times.add(TimeManager(LocalTime.parse("12:00"), LocalTime.parse("13:30"), "PENDING"))
        times.add(TimeManager(LocalTime.parse("15:00"), LocalTime.parse("16:30"), "PENDING"))
        times.add(TimeManager(LocalTime.parse("20:00"), LocalTime.parse("21:30"), "PENDING"))

        return times
    }

    private fun timeManager(
        halfIntervalHourView: ArrayList<TimeManager>,
        gameTime: ArrayList<TimeManager>,
    ): ArrayList<TimeManager> {

        halfIntervalHourView.forEach { half ->
            gameTime.forEach { game ->
                if (half.startTime!!.isBetween(game.startTime!!, game.finishTime!!)) {
                    half.status = game.status
                }
            }
        }

        return halfIntervalHourView
    }

    private fun beforePosition(position: Int): Int {
        val array = timeManager(allTime(LocalTime.parse("07:00"), LocalTime.parse("23:30")),
            gameTime())

        for (pos in position downTo 0) {
            if (array[pos].status == "ACCEPTED" || array[pos].status == "PENDING")
                return pos + 1
        }
        return 0
    }

    private fun afterPosition(position: Int): Int {
        val array = timeManager(allTime(LocalTime.parse("07:00"), LocalTime.parse("23:30")),
            gameTime())

        for (pos in position until array.size) {
            if (array[pos].status == "ACCEPTED" || array[pos].status == "PENDING")
                return pos - 1
        }
        return array.size - 1
    }


    private fun LocalTime.isBetween(openTime: LocalTime, closeTime: LocalTime): Boolean =
        (this.isAfter(openTime) || this == openTime) && (this.isBefore(closeTime) || (this == closeTime))
}