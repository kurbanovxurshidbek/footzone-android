package com.footzone.footzone.ui.fragments

import android.annotation.SuppressLint
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
import com.footzone.footzone.databinding.ItemChooseTimeViewWhiteBinding
import com.footzone.footzone.model.TimeManager
import com.footzone.footzone.utils.KeyValues
import com.google.type.DateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class TimeIntervalFragment : Fragment() {
    lateinit var binding: FragmentTimeIntervalBinding
    private lateinit var adapter: TimeManagerAdapter
    var beforePosition = -1
    var afterPosition = -1
    var list = LinkedList<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_time_interval, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTimeIntervalBinding.bind(view)
        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        adapter = TimeManagerAdapter { p, view->
           managerTime(p, view)
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

    @SuppressLint("ResourceAsColor")
    private fun managerTime(p: Int, view: ItemChooseTimeViewWhiteBinding) {
        val array = timeManager(allTime(LocalTime.parse("07:00"), LocalTime.parse("23:30")),
            gameTime())
        if (list.isEmpty()){
            list.add(p)
            beforePosition = beforePosition(p)
            afterPosition = afterPosition(p)
            binding.tvStartTime.setText(array[list[0]].startTime!!.toString())
            view.linearFreeToBook.setBackgroundResource(R.drawable.view_rounded_corners_blue_4dp)
            view.tvFinishTime.setTextColor(R.color.white)
            view.tvLine.setTextColor(R.color.white)
            view.tvStartTime.setTextColor(R.color.white)
        }else if (list.size == 1 && list[0] == p) {
            list.remove(p)
            view.linearFreeToBook.setBackgroundResource(R.drawable.view_rounded_corners_white_4dp)
            view.tvFinishTime.setTextColor(R.color.black)
            view.tvLine.setTextColor(R.color.black)
            view.tvStartTime.setTextColor(R.color.black)
            binding.tvStartTime.text!!.clear()
        } else if (list.size == 1 && p >= beforePosition && p <= afterPosition){
            list.add(p)
            if (array[list[0]].startTime!! > array[list[1]].startTime){
                binding.tvStartTime.setText(array[list[1]].startTime!!.toString())
                binding.tvFinishTime.setText(array[list[0]].finishTime!!.toString())
                for (pos in (list[1]+1)..(list[0]-1)){
                    array[pos].between = true
                }
                adapter.submitList(array)
                view.linearFreeToBook.setBackgroundResource(R.drawable.view_rounded_corners_blue_4dp)
                view.tvFinishTime.setTextColor(R.color.white)
                view.tvLine.setTextColor(R.color.white)
                view.tvStartTime.setTextColor(R.color.white)
            }else{
                binding.tvStartTime.setText(array[list[0]].startTime!!.toString())
                binding.tvFinishTime.setText(array[list[1]].finishTime!!.toString())
                view.linearFreeToBook.setBackgroundResource(R.drawable.view_rounded_corners_blue_4dp)
                view.tvFinishTime.setTextColor(R.color.white)
                view.tvLine.setTextColor(R.color.white)
                view.tvStartTime.setTextColor(R.color.white)
                for (pos in (list[0]+1)..(list[1])-1){
                    array[pos].between = true
                }
                adapter.submitList(array)
            }
        }else if (list.size == 2 && list[0] == p){
            if (list[0] < list[1]){
                for (pos in (list[0]+1)..(list[1])-1){
                    array[pos].between = false
                }
            }else{
                for (pos in (list[1]+1)..(list[0]-1)){
                    array[pos].between = false
                }
            }
            adapter.submitList(array)
            list.remove(p)
            view.linearFreeToBook.setBackgroundResource(R.drawable.view_rounded_corners_white_4dp)
            view.tvFinishTime.setTextColor(R.color.black)
            view.tvLine.setTextColor(R.color.black)
            view.tvStartTime.setTextColor(R.color.black)
            binding.tvFinishTime.text!!.clear()
            binding.tvStartTime.setText(array[list[0]].startTime.toString())
        }else if (list.size == 2 && list[1] == p){
            if (list[0] < list[1]){
                for (pos in (list[0]+1)..(list[1])-1){
                    array[pos].between = false
                }
            }else{
                for (pos in (list[1]+1)..(list[0]-1)){
                    array[pos].between = false
                }
            }
            adapter.submitList(array)
            list.remove(p)
            view.linearFreeToBook.setBackgroundResource(R.drawable.view_rounded_corners_white_4dp)
            view.tvFinishTime.setTextColor(R.color.black)
            view.tvLine.setTextColor(R.color.black)
            view.tvStartTime.setTextColor(R.color.black)
            binding.tvFinishTime.text!!.clear()
            binding.tvStartTime.setText(array[list[0]].startTime.toString())
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