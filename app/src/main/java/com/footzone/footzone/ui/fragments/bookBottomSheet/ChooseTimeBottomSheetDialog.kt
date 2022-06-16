package com.footzone.footzone.ui.fragments.bookBottomSheet

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.CalendarDIalog
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentChooseTimeBottomSheetDialogBinding
import com.footzone.footzone.model.BookingRequest
import com.footzone.footzone.model.StadiumDataToBottomSheetDialog
import com.footzone.footzone.ui.fragments.timeinterval.TimeSharedViewModel
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.FINISHTIME
import com.footzone.footzone.utils.KeyValues.STADIUM_DATA
import com.footzone.footzone.utils.KeyValues.STADIUM_ID
import com.footzone.footzone.utils.KeyValues.STARTTIME
import com.footzone.footzone.utils.UiStateObject
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class ChooseTimeBottomSheetDialog(private val stadiumData: StadiumDataToBottomSheetDialog) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentChooseTimeBottomSheetDialogBinding
    private val viewModel by viewModels<BookDialogViewModel>()
    private val timeSharedViewModel by activityViewModels<TimeSharedViewModel>()
    private var startTime: String? = null
    private var endTime: String? = null
    private var bookData: String? = null
    var isCheck = false


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

        timeSharedViewModel.getTime().observe(viewLifecycleOwner){
            startTime = it?.startTime
            endTime = it?.finishTime
            bookData = it?.day
            if (startTime != null && endTime != null){
                binding.tvChooseTime.text = "$startTime - $endTime"
                binding.tvBook.setBackgroundResource(R.drawable.view_rounded_corners_blue)
                binding.tvDate.isClickable = true
                binding.tvDate.setTextColor(Color.WHITE)
                isCheck = true
            }else{
                isCheck = false
                binding.tvBook.setBackgroundResource(R.drawable.view_rounded_corners_grey)
                binding.tvDate.isClickable = false
                binding.tvDate.setTextColor(Color.BLACK)
            }
            val sourceFormat = SimpleDateFormat("yyyy-MM-dd")
            val destFormat = SimpleDateFormat("dd MMM yyy")
            val convertedDate = sourceFormat.parse(bookData)

            binding.tvDate.text = destFormat.format(convertedDate)
        }

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
            if (isCheck) {
                viewModel.sendBookingRequest(
                    BookingRequest(
                        stadiumData.stadiumId,
                        bookData!!.toString(),
                        startTime!!.toString(),
                        endTime!!.toString()
                    )
                )
                observeSendBooking()
            }else{
                Toast.makeText(requireContext(), "Malumotlar to'liq to'ldirilmagan", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rlTimeInterval.setOnClickListener {
            if (binding.tvDate.text.toString().length > 4) {
                val sourceFormat = SimpleDateFormat("dd MMM yyy")
                val destFormat = SimpleDateFormat("yyyy-MM-dd")
                val convertedDate = sourceFormat.parse(binding.tvDate.text.toString())

                val bookDate: String = destFormat.format(convertedDate)
                findNavController().navigate(R.id.action_pitchDetailFragment_to_timeIntervalFragment,
                    bundleOf(STADIUM_ID to stadiumData.stadiumId, STADIUM_DATA to bookDate))
            } else {
                Toast.makeText(requireContext(), "O'yin kunini tanlang!!!", Toast.LENGTH_SHORT)
                    .show()
            }
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
                        dismiss()
                        Toast.makeText(requireContext(), "Sizning so'rovingiz yuborildi\n, tez orada javob qaytadi", Toast.LENGTH_SHORT).show()
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupUI: ${it.message}")
                        Toast.makeText(requireContext(), "Sizning so'rovingiz yuborilmadi\n, qayta urining", Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        super.onDestroy()
        activity?.viewModelStore?.clear()
    }
}