package com.footzone.footzone.ui.fragments.stadiumdetail

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.footzone.footzone.R
import com.footzone.footzone.adapter.CommentAdapter
import com.footzone.footzone.adapter.CustomAdapter
import com.footzone.footzone.databinding.FragmentPitchDetailBinding
import com.footzone.footzone.databinding.LayoutAcceptBinding
import com.footzone.footzone.databinding.LayoutTimetableDialogBinding
import com.footzone.footzone.model.*
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.ui.fragments.bookBottomSheet.ChooseTimeBottomSheetDialog
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues.IS_FAVOURITE_STADIUM
import com.footzone.footzone.utils.KeyValues.STADIUM_ID
import com.footzone.footzone.utils.KeyValues.USER_ID
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import com.footzone.footzone.utils.commonfunction.Functions
import com.footzone.footzone.utils.commonfunction.Functions.showStadiumOpenOrClose
import com.footzone.footzone.utils.commonfunction.Functions.textCutter
import com.footzone.footzone.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PitchDetailFragment : BaseFragment(R.layout.fragment_pitch_detail) {

    private lateinit var binding: FragmentPitchDetailBinding
    lateinit var adapter: CustomAdapter
    lateinit var adapterComment: CommentAdapter
    private val viewModel by viewModels<PitchDetailViewModel>()
    private lateinit var stadiumId: String
    lateinit var workingDays: List<WorkingDay>
    private lateinit var stadiumDataToBottomSheetDialog: StadiumDataToBottomSheetDialog
    private var isFavouriteStadium: Boolean = false
    private lateinit var stadiumData: StadiumData
    var times: ArrayList<TimeManager> = ArrayList()

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.get(STADIUM_ID).toString()
        isFavouriteStadium = arguments?.get(IS_FAVOURITE_STADIUM).toString().toBoolean()
        viewModel.getPitchData(stadiumId)
        viewModel.getCommentAllByStadiumId(stadiumId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPitchDetailBinding.bind(view)
        setupObservers()
        setupCommentObservers()
        initViews()
    }

    private fun setupCommentObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.pitchComment.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "setupObservers: ${it.data}")
                        showPitchComments(it.data.data)
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

    private fun showPitchComments(data: Data) {
        Log.d("@@comments", data.toString())
        showRatingBarInfo(data)
        refreshCommentAdapter(data)
    }

    private fun showRatingBarInfo(data: Data) {
        val averageRate: Float = Functions.resRating(data.commentInfo as ArrayList<Comment>)
        val rateNumberPercentage = Functions.rateNumbers(comments = data.commentInfo)
        val viewRateCount: Int = (data.commentInfo.sumOf { it.number })
        binding.apply {
            textViewRateCount.text = viewRateCount.toString()
            if (averageRate > 0) {
                tvAverageRate.text = averageRate.toString()
            } else {
                tvAverageRate.text = "0"
            }

            rbRate.rating = averageRate
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ratingOne.setProgress(rateNumberPercentage.one, true)
                ratingTwo.setProgress(rateNumberPercentage.two, true)
                ratingThree.setProgress(rateNumberPercentage.three, true)
                ratingFour.setProgress(rateNumberPercentage.four, true)
                ratingFive.setProgress(rateNumberPercentage.five, true)
            } else {
                ratingOne.progress = rateNumberPercentage.one
                ratingTwo.progress = rateNumberPercentage.two
                ratingThree.progress = rateNumberPercentage.three
                ratingFour.progress = rateNumberPercentage.four
                ratingFive.progress = rateNumberPercentage.five
            }

        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.pitchData.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        stadiumData = it.data.data
                        stadiumDataToBottomSheetDialog = StadiumDataToBottomSheetDialog(
                            stadiumData.stadiumId,
                            stadiumData.hourlyPrice.toInt(),
                            stadiumData.workingDays
                        )

                        showPitchData(stadiumData)
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

    private fun showPitchData(data: StadiumData) {
        binding.apply {

            refreshAdapter(data.photos)

            tvAppBarPitchName.text = data.stadiumName
            tvStadiumName.text = data.stadiumName
            tvAddress.text = data.address
            tvNumber.text = data.number
            showStadiumOpenOrClose(tvStatus, tvTime, data.isOpen)
            tvPrice.text = data.hourlyPrice.toString()
        }
    }

    private fun initViews() {

        binding.rbRate.setIsIndicator(true)

        if (isFavouriteStadium) {
            changeLinearAddFavourite()
        }

        binding.linearFavourite.setOnClickListener {
            if (sharedPref.getUserID(USER_ID, "").isNotEmpty()) {
                viewModel.addToFavouriteStadiums(
                    FavouriteStadiumRequest(
                        stadiumId,
                        sharedPref.getUserID(USER_ID, "")
                    )
                )
                observeAddFavourite()
            } else
                toast(
                    "Siz hali ro'yxatdan o'tmagansiz.\n" +
                            "Sahifam bo'limidan ro'yxatdan o'tishingiz mumkin"
                )
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnOpenBottomSheet.setOnClickListener {
            val chooseTimeBottomSheetDialog =
                ChooseTimeBottomSheetDialog(stadiumDataToBottomSheetDialog)
            chooseTimeBottomSheetDialog.show(childFragmentManager, chooseTimeBottomSheetDialog.tag)
        }

        binding.linearNavigation.setOnClickListener {
            requireActivity().shareLocationToGoogleMap(stadiumData.latitude, stadiumData.longitude)
        }
        binding.icTimetable.setOnClickListener {
            Log.d("TAG", "initViews: ${workingDays}")
            if (workingDays.isNotEmpty()) {
                openTimeTableDialog()
            }
        }
    }

    private fun openTimeTableDialog() {
        val dialog = Dialog(requireContext())
        val dialogLayout =
            LayoutTimetableDialogBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dialogLayout.root)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.dialog_background)

        val layoutParams = dialogLayout.root.layoutParams
        layoutParams.width = (312 * requireContext().resources.displayMetrics.density).toInt()
        dialogLayout.apply {
            val daysWeek = requireContext().resources.getStringArray(R.array.daysWeek)
            tvClose.setOnClickListener { dialog.dismiss() }
            workingDays.forEach {
                when (it.dayName) {
                    daysWeek[0] -> {
                        timeTableDayControl(mondayLayout,tvMondayOpenTime,tvMondayCloseTime,it)
                    }
                    daysWeek[1] -> {
                        timeTableDayControl(tuesdayLayout,tvTuesdayOpenTime,tvTuesdayCloseTime,it)
                    }
                    daysWeek[2] -> {
                        timeTableDayControl(wednesdayLayout,tvWednesdayOpenTime,tvWednesdayCloseTime,it)
                    }
                    daysWeek[3] -> {
                        timeTableDayControl(thursdayLayout,tvThursdayOpenTime,tvThursdayCloseTime,it)
                    }
                    daysWeek[4] -> {
                        timeTableDayControl(fridayLayout,tvFridayOpenTime,tvFridayCloseTime,it)
                    }
                    daysWeek[5] -> {
                        timeTableDayControl(saturdayLayout,tvSaturdayOpenTime,tvSaturdayCloseTime,it)
                    }
                    daysWeek[6] -> {
                        timeTableDayControl(sundayLayout,tvSundayOpenTime,tvSundayCloseTime,it)
                    }
                }
            }
        }

        dialog.show()
    }

    private fun timeTableDayControl(layout: LinearLayout, openTime: TextView, closeTime: TextView, workingDay: WorkingDay) {
        layout.show()
        openTime.text = textCutter(workingDay.startTime,0,5)
        closeTime.text = textCutter(workingDay.endTime,0,5)
    }


    private fun changeLinearAddFavourite() {
        binding.linearFavourite.setBackgroundResource(R.drawable.button_filled_rounded_corner)
        binding.ivFavourite.setColorFilter(Color.parseColor("#ffffff"))
        binding.tvFavourite.setTextColor(Color.parseColor("#ffffff"))
        binding.tvFavourite.text = "Tanlanganlardan o'chirish"
    }

    private fun changeLinearRemoveFavourite() {
        binding.linearFavourite.setBackgroundResource(R.drawable.button_rounded_corner)
        binding.ivFavourite.setColorFilter(Color.parseColor("#0C64E6"))
        binding.tvFavourite.setTextColor(Color.parseColor("#0C64E6"))
        binding.tvFavourite.text = "Tanlanganlarga qo'shish"
    }

    private fun observeAddFavourite() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.addToFavouriteStadiums.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        if (it.data.message == "add success") {
                            changeLinearAddFavourite()
                        }

                        if (it.data.message == "delete success") {
                            changeLinearRemoveFavourite()
                        }
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

    private fun refreshAdapter(photos: ArrayList<StadiumPhoto>) {
        adapter = CustomAdapter(photos)
        binding.recyclerView.adapter = adapter
    }

    private fun refreshCommentAdapter(data: Data) {
        adapterComment = CommentAdapter(data.allComments, requireContext())
        binding.recyclerViewComment.adapter = adapterComment
    }

}