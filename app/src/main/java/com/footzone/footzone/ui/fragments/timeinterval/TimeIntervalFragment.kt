package com.footzone.footzone.ui.fragments.timeinterval

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.footzone.footzone.R
import com.footzone.footzone.adapter.TimeManagerAdapter
import com.footzone.footzone.databinding.FragmentTimeIntervalBinding
import com.footzone.footzone.databinding.ItemChooseTimeViewWhiteBinding
import com.footzone.footzone.model.LiveDataModel
import com.footzone.footzone.model.TimeManager
import com.footzone.footzone.model.sessionsday.SessionsData
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class TimeIntervalFragment : Fragment() {
    lateinit var binding: FragmentTimeIntervalBinding
    private lateinit var adapter: TimeManagerAdapter
    var beforePosition = -1
    var afterPosition = -1
    var list = LinkedList<Int>()
    private val viewModel by viewModels<TimeIntervalViewModel>()
    private val timeSharedViewModel by activityViewModels<TimeSharedViewModel>()
    lateinit var sessionsData: SessionsData
    var sessionTimes: ArrayList<TimeManager> = ArrayList()
    lateinit var stadiumId: String
    lateinit var bookDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.get(KeyValues.STADIUM_ID) as String
        bookDate = arguments?.get(KeyValues.STADIUM_DATA) as String
    }

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
        viewModel.getSessionsForSpecificDay(stadiumId, bookDate)
        setupObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.sessionsDay.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "setupObservers sfdd: ${it.data}")
                        sessionsData = it.data.data
                        val array = resources.getStringArray(R.array.timelist)
                        sessionsData.sessionTimes.forEach { data ->
                            sessionTimes.add(TimeManager(startTime = LocalTime.parse(data.startTime),
                                finishTime = LocalTime.parse(array[array.indexOf(data.endTime.substring(
                                    0,
                                    5)) - 1]),
                                status = "ACCEPTED"))
                        }
                        Log.d("TAG", "setupObservers: ${sessionTimes}")
                        initViews()
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupUI: ${it.message}")
                    }
                    else -> {
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        adapter = TimeManagerAdapter { p, view ->
            managerTime(p, view)
        }
        adapter.submitList(timeManager(allTime(LocalTime.parse(sessionsData.workingStartTime),
            LocalTime.parse(sessionsData.workingEndTime)),
            sessionTimes))

        binding.recyclerView.adapter = adapter
        binding.ivPinding.setOnClickListener {
            val startTime = binding.tvStartTime.text.toString()
            val finishTime = binding.tvFinishTime.text.toString()

            if (startTime.isNotEmpty()  && finishTime.isNotEmpty()) {
                timeSharedViewModel.setTime(LiveDataModel(startTime, finishTime, bookDate))
                requireActivity().onBackPressed()
            }else{
                Toast.makeText(requireContext(), "O'yin vaqtlarini tanlang", Toast.LENGTH_SHORT).show()
            }
        }

        binding.icClose.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun managerTime(p: Int, view: ItemChooseTimeViewWhiteBinding) {
        val array = timeManager(allTime(LocalTime.parse(sessionsData.workingStartTime),
            LocalTime.parse(sessionsData.workingEndTime)),
            sessionTimes)
        if (list.isEmpty()) {
            list.add(p)
            beforePosition = beforePosition(p)
            afterPosition = afterPosition(p)
            binding.tvStartTime.setText(array[list[0]].startTime!!.toString())
            view.linearFreeToBook.setBackgroundResource(R.drawable.view_rounded_corners_blue_4dp)
            view.tvFinishTime.setTextColor(R.color.white)
            view.tvLine.setTextColor(R.color.white)
            view.tvStartTime.setTextColor(R.color.white)
        } else if (list.size == 1 && list[0] == p) {
            list.remove(p)
            view.linearFreeToBook.setBackgroundResource(R.drawable.view_rounded_corners_white_4dp)
            view.tvFinishTime.setTextColor(Color.parseColor("#000000"))
            view.tvLine.setTextColor(Color.parseColor("#000000"))
            view.tvStartTime.setTextColor(Color.parseColor("#000000"))
            binding.tvStartTime.text!!.clear()
        } else if (list.size == 1 && p >= beforePosition && p <= afterPosition) {
            list.add(p)
            if (array[list[0]].startTime!! > array[list[1]].startTime) {
                binding.tvStartTime.setText(array[list[1]].startTime!!.toString())
                binding.tvFinishTime.setText(array[list[0]].finishTime!!.toString())
                for (pos in (list[1] + 1)..(list[0] - 1)) {
                    array[pos].between = true
                }
                adapter.submitList(array)
                view.linearFreeToBook.setBackgroundResource(R.drawable.view_rounded_corners_blue_4dp)
                view.tvFinishTime.setTextColor(R.color.white)
                view.tvLine.setTextColor(R.color.white)
                view.tvStartTime.setTextColor(R.color.white)
            } else {
                binding.tvStartTime.setText(array[list[0]].startTime!!.toString())
                binding.tvFinishTime.setText(array[list[1]].finishTime!!.toString())
                view.linearFreeToBook.setBackgroundResource(R.drawable.view_rounded_corners_blue_4dp)
                view.tvFinishTime.setTextColor(R.color.white)
                view.tvLine.setTextColor(R.color.white)
                view.tvStartTime.setTextColor(R.color.white)
                for (pos in (list[0] + 1)..(list[1]) - 1) {
                    array[pos].between = true
                }
                adapter.submitList(array)
            }
        } else if (list.size == 2 && list[0] == p) {
            if (list[0] < list[1]) {
                for (pos in (list[0] + 1)..(list[1]) - 1) {
                    array[pos].between = false
                }
            } else {
                for (pos in (list[1] + 1)..(list[0] - 1)) {
                    array[pos].between = false
                }
            }
            adapter.submitList(array)
            list.remove(p)
            view.linearFreeToBook.setBackgroundResource(R.drawable.view_rounded_corners_white_4dp)
            view.tvFinishTime.setTextColor(Color.parseColor("#000000"))
            view.tvLine.setTextColor(Color.parseColor("#000000"))
            view.tvStartTime.setTextColor(Color.parseColor("#000000"))
            binding.tvFinishTime.text!!.clear()
            binding.tvStartTime.setText(array[list[0]].startTime.toString())
        } else if (list.size == 2 && list[1] == p) {
            if (list[0] < list[1]) {
                for (pos in (list[0] + 1) until (list[1])) {
                    array[pos].between = false
                }
            } else {
                for (pos in (list[1] + 1)..(list[0] - 1)) {
                    array[pos].between = false
                }
            }
            adapter.submitList(array)
            list.remove(p)
            view.linearFreeToBook.setBackgroundResource(R.drawable.view_rounded_corners_white_4dp)
            view.tvFinishTime.setTextColor(Color.parseColor("#000000"))
            view.tvLine.setTextColor(Color.parseColor("#000000"))
            view.tvStartTime.setTextColor(Color.parseColor("#000000"))
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
        val array = timeManager(allTime(LocalTime.parse(sessionsData.workingStartTime),
            LocalTime.parse(sessionsData.workingEndTime)),
            sessionTimes)

        for (pos in position downTo 0) {
            if (array[pos].status == "ACCEPTED")
                return pos + 1
        }
        return 0
    }

    private fun afterPosition(position: Int): Int {
        val array = timeManager(allTime(LocalTime.parse(sessionsData.workingStartTime),
            LocalTime.parse(sessionsData.workingEndTime)),
            sessionTimes)

        for (pos in position until array.size) {
            if (array[pos].status == "ACCEPTED")
                return pos - 1
        }
        return array.size - 1
    }


    private fun LocalTime.isBetween(openTime: LocalTime, closeTime: LocalTime): Boolean =
        (this.isAfter(openTime) || this == openTime) && (this.isBefore(closeTime) || (this == closeTime))
}