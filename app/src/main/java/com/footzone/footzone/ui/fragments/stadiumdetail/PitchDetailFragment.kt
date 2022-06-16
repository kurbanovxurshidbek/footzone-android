package com.footzone.footzone.ui.fragments.stadiumdetail

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.footzone.footzone.R
import com.footzone.footzone.adapter.CommentAdapter
import com.footzone.footzone.adapter.CustomAdapter
import com.footzone.footzone.databinding.FragmentPitchDetailBinding
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
import com.footzone.footzone.utils.commonfunction.Functions
import com.footzone.footzone.utils.commonfunction.Functions.showStadiumOpenOrClose
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PitchDetailFragment : BaseFragment(R.layout.fragment_pitch_detail) {

    private lateinit var binding: FragmentPitchDetailBinding
    lateinit var adapter: CustomAdapter
    lateinit var adapterComment: CommentAdapter
    private val viewModel by viewModels<PitchDetailViewModel>()
    private lateinit var stadiumId: String
    private lateinit var stadiumDataToBottomSheetDialog: StadiumDataToBottomSheetDialog
    private var isFavouriteStadium: Boolean = false
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
        val averageRate = Functions.resRating(data.commentInfo as ArrayList<Comment>)
        val rateNumberPercentage = Functions.rateNumbers(data.commentInfo)
        Log.d("@@@", rateNumberPercentage.toString())
        binding.apply {
            tvAverageRate.setText(averageRate.toString())
            ratingOne.setProgress(rateNumberPercentage.one)
            ratingTwo.setProgress(rateNumberPercentage.two)
            ratingThree.setProgress(rateNumberPercentage.three)
            ratingFour.setProgress(rateNumberPercentage.four)
            ratingFive.setProgress(rateNumberPercentage.five)
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
                        Log.d("TAG", "setupObservers: ${it.data}")
                        val stadiumData = it.data.data
                        stadiumDataToBottomSheetDialog = StadiumDataToBottomSheetDialog(
                            stadiumData.stadiumId,
                            stadiumData.hourlyPrice,
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
            requireActivity().shareLocationToGoogleMap(41.33324, 69.21896)
        }
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