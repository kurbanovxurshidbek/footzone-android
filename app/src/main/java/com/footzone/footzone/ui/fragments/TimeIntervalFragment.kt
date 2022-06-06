package com.footzone.footzone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import com.footzone.footzone.R
import com.footzone.footzone.adapter.TimeManagerAdapter
import com.footzone.footzone.databinding.FragmentTimeIntervalBinding
import com.footzone.footzone.model.TimeManager
import com.footzone.footzone.utils.KeyValues

class TimeIntervalFragment : Fragment() {
    lateinit var binding: FragmentTimeIntervalBinding

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
        val adapter = TimeManagerAdapter()

        adapter.submitList(allTime("00:00", "23:30"))

        binding.recyclerView.adapter = adapter

        binding.ivPinding.setOnClickListener {
            val result = "dscekv r"
            setFragmentResult(KeyValues.TYPE_CHOOSE_TIME, bundleOf("bundleKey" to result))
            requireActivity().onBackPressed()
        }
    }

    private fun allTime(openTime: String, closeTime: String): ArrayList<TimeManager> {
        val array = resources.getStringArray(R.array.timelist)
        val times = ArrayList<TimeManager>()

        for (index in array.indexOf(openTime)..array.indexOf(closeTime)-1 ){
            times.add(TimeManager(array[index], array[index + 1], ""))
        }
        return times
    }
}