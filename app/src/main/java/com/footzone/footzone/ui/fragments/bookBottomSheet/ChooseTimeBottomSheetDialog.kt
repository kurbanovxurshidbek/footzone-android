package com.footzone.footzone.ui.fragments.bookBottomSheet

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.CalendarDIalog
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentChooseTimeBottomSheetDialogBinding
import com.footzone.footzone.model.BookingRequest
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.UiStateObject
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalTime

@AndroidEntryPoint
class ChooseTimeBottomSheetDialog(private val stadiumId: String) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentChooseTimeBottomSheetDialogBinding
    private val viewModel by viewModels<BookDialogViewModel>()

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
            val dialog = CalendarDIalog { date ->
                binding.tvDate.text = date

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dialog.showCalendarDialog(requireActivity())
            }
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvBook.setOnClickListener {
            viewModel.sendBookingRequest(
                BookingRequest(
                    stadiumId,
                   "2022-06-14",
                    "20:00:00",
                    "22:00:00"
                )
            )
            observeSendBooking()
        }

        binding.rlTimeInterval.setOnClickListener {
            findNavController().navigate(R.id.action_pitchDetailFragment_to_timeIntervalFragment)
        }
    }

    private fun observeSendBooking() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.bookRequest.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "observeSendBooking: ${it.data}")
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

    @SuppressLint("ResourceType")
    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog;
    }
}