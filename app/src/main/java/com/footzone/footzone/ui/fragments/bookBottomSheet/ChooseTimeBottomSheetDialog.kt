package com.footzone.footzone.ui.fragments.bookBottomSheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.utils.CalendarDIalog
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentChooseTimeBottomSheetDialogBinding
import com.footzone.footzone.helper.TimeSharedViewModel
import com.footzone.footzone.model.BookingRequest
import com.footzone.footzone.model.StadiumDataToBottomSheetDialog
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues.STADIUM_DATA
import com.footzone.footzone.utils.KeyValues.STADIUM_ID
import com.footzone.footzone.utils.ProgressBarDialog
import com.footzone.footzone.utils.UiStateObject
import com.footzone.footzone.utils.commonfunction.Functions.calculateInHours
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime

@AndroidEntryPoint
class ChooseTimeBottomSheetDialog(private val stadiumData: StadiumDataToBottomSheetDialog) :
    BottomSheetDialogFragment() {

    lateinit var binding: FragmentChooseTimeBottomSheetDialogBinding
    private val viewModel by viewModels<BookDialogViewModel>()
    private val timeSharedViewModel by activityViewModels<TimeSharedViewModel>()
    private var startTime: String? = null
    private var endTime: String? = null
    private var bookData: String? = null
    var isCheck = false
    var dayOfWeek = 0
    lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingDialog = ProgressBarDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_time_bottom_sheet_dialog, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseTimeBottomSheetDialogBinding.bind(view)
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun initView() {

        timeSharedViewModel.getTime().observe(viewLifecycleOwner) {
            startTime = it?.startTime
            endTime = it?.finishTime
            bookData = it?.day
            if (startTime != null && endTime != null) {
                binding.tvChooseTime.text = "$startTime - $endTime"
                binding.tvBook.setBackgroundResource(R.drawable.button_filled_rounded_corner)
                binding.tvBook.isClickable = true
                binding.tvBook.setTextColor(Color.WHITE)
                val playTime =
                    calculateInHours(LocalTime.parse(startTime), LocalTime.parse(endTime))
                binding.tvGameTime.text = "${playTime.toInt()} "
                binding.tvTotalPrice.text = "${(playTime * stadiumData.hourlyPrice).toInt()} "
                isCheck = true
            } else {
                isCheck = false
                binding.tvBook.setBackgroundResource(R.drawable.button_round_corner_grey)
                binding.tvBook.isClickable = false
                binding.tvBook.setTextColor(Color.BLACK)
            }
            val sourceFormat = SimpleDateFormat("yyyy-MM-dd")
            val destFormat = SimpleDateFormat("dd MMM yyy")
            val convertedDate = sourceFormat.parse(bookData)

            binding.tvDate.setText(destFormat.format(convertedDate).toString())
        }

        binding.llDate.setOnClickListener {
            binding.tvChooseTime.text = ""
            binding.tvGameTime.text = ""
            binding.tvTotalPrice.text = ""
            binding.tvBook.setBackgroundResource(R.drawable.button_round_corner_grey)
            isCheck = false
            val dialog = CalendarDIalog { date, week ->
                binding.tvDate.text = date
                dayOfWeek = week

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
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.str_date_incomplete),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.rlTimeInterval.setOnClickListener {
            if (binding.tvDate.text.toString().length > 4) {

                val array = resources.getStringArray(R.array.daysWeek)
                val sourceFormat = SimpleDateFormat("dd MMM yyy")
                val destFormat = SimpleDateFormat("yyyy-MM-dd")
                val convertedDate = sourceFormat.parse(binding.tvDate.text.toString())

                for (pos in 0..stadiumData.workingDays.size - 1) {
                    if (stadiumData.workingDays[pos].dayName.equals(array[dayOfWeek - 1])) {
                        val bookDate: String = destFormat.format(convertedDate)
                        findNavController().navigate(
                            R.id.action_pitchDetailFragment_to_timeIntervalFragment,
                            bundleOf(STADIUM_ID to stadiumData.stadiumId, STADIUM_DATA to bookDate)
                        )
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.str_select_dat_game),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun observeSendBooking() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookRequest.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            loadingDialog.show()
                        }

                        is UiStateObject.SUCCESS -> {
                            loadingDialog.dismiss()
                            dismiss()
                            Toast.makeText(
                                requireContext(),
                                getText(R.string.str_send_request),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is UiStateObject.ERROR -> {
                            loadingDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                getText(R.string.str_not_send_request),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                        }
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