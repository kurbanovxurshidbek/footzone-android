package com.footzone.footzone.ui.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.CalendarDIalog
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentChooseTimeBottomSheetDialogBinding
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.STADIUM_ID
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChooseTimeBottomSheetDialog : BottomSheetDialogFragment() {
    lateinit var binding: FragmentChooseTimeBottomSheetDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_time_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseTimeBottomSheetDialogBinding.bind(view)
        initView()
    }

    private fun initView() {

        binding.llDate.setOnClickListener {
            val dialog = CalendarDIalog{ date ->
                binding.tvDate.text = date

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dialog.showCalendarDialog(requireActivity())
            }
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvOccupancy.setOnClickListener {
            val inflater = layoutInflater
            val view: View = inflater.inflate(R.layout.toast_choose_time, requireActivity().findViewById(R.id.mylayout) as ViewGroup?)

            val custToast = Toast(requireContext())
            custToast.setView(view)
            custToast.show()
           dismiss()
        }

        binding.rlTimeInterval.setOnClickListener {
            findNavController().navigate(R.id.action_pitchDetailFragment_to_timeIntervalFragment,
                bundleOf(STADIUM_ID, ))
        }
    }

    @SuppressLint("ResourceType")
    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog;
    }
}