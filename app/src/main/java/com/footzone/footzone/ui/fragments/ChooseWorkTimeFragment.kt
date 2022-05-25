package com.footzone.footzone.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.RelativeLayout
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentChooseWorkTimeBinding

class ChooseWorkTimeFragment : Fragment() {
    lateinit var binding: FragmentChooseWorkTimeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_work_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseWorkTimeBinding.bind(view)
        initViews()
    }

    private fun initViews() {

        binding.apply {
            icClose.setOnClickListener { requireActivity().onBackPressed() }
            tvCancel.setOnClickListener { requireActivity().onBackPressed() }
            tvSelection.setOnClickListener { requireActivity().onBackPressed() }
        }

        binding.switchMo.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutMo, isOn)
        }

        binding.switchTu.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutTu, isOn)
        }

        binding.switchWe.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutWe, isOn)
        }

        binding.switchTh.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutTh, isOn)
        }
        binding.switchFr.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutFr, isOn)
        }

        binding.switchSa.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutSa, isOn)
        }

        binding.switchSu.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutSu, isOn)
        }

        numberPicker(binding.startTimeMo, binding.finishTimeMo)
        numberPicker(binding.startTimeTu, binding.finishTimeTu)
        numberPicker(binding.startTimeWe, binding.finishTimeWe)
        numberPicker(binding.startTimeTh, binding.finishTimeTh)
        numberPicker(binding.startTimeFr, binding.finishTimeFr)
        numberPicker(binding.startTimeSa, binding.finishTimeSa)
        numberPicker(binding.startTimeSu, binding.finishTimeSu)

    }

    fun openTime(layout: RelativeLayout, bool: Boolean){
        if (bool){
            layout.visibility = View.VISIBLE
        }else{
            layout.visibility = View.GONE
        }
    }

    fun numberPicker(startTime: NumberPicker, finishTime: NumberPicker){
        val timeList = resources.getStringArray(R.array.timelist)
        startTime.minValue = 1
        startTime.maxValue = 47
        startTime.displayedValues = timeList
        finishTime.minValue = 1
        finishTime.maxValue = 47
        finishTime.displayedValues = timeList
    }
}