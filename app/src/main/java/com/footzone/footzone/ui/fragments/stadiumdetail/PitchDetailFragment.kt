package com.footzone.footzone.ui.fragments.stadiumdetail

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.fragment.app.setFragmentResultListener
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
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.FINISHTIME
import com.footzone.footzone.utils.KeyValues.IS_FAVOURITE_STADIUM
import com.footzone.footzone.utils.KeyValues.STADIUM_ID
import com.footzone.footzone.utils.KeyValues.STARTTIME
import com.footzone.footzone.utils.KeyValues.USER_ID
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PitchDetailFragment : BaseFragment(R.layout.fragment_pitch_detail) {

    private lateinit var binding: FragmentPitchDetailBinding
    lateinit var adapter: CustomAdapter
    lateinit var adapterComment: CommentAdapter
    private val viewModel by viewModels<PitchDetailViewModel>()
    private lateinit var stadiumId: String
    private var isFavouriteStadium: Boolean = false
    var times: ArrayList<TimeManager> = ArrayList()
    private var startTime: String? = null
    private var endTime: String? = null

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.get(STADIUM_ID).toString()
        isFavouriteStadium = arguments?.get(IS_FAVOURITE_STADIUM).toString().toBoolean()
        viewModel.getPitchData(stadiumId)
        viewModel.getCommentAllByStadiumId(stadiumId)

        val startTime = arguments?.get(STARTTIME).toString()
        val finishTime = arguments?.get(FINISHTIME).toString()

        Log.d("TAG", "onCreate ds: ${startTime}")
        Log.d("TAG", "onCreate ds: ${finishTime}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPitchDetailBinding.bind(view)
        setupObservers()
        setupCommentObservers()
        initViews()

        setFragmentResultListener(KeyValues.TYPE_CHOOSE_TIME) { r, bundle ->
            startTime = bundle.getString(STARTTIME)!!
            endTime = bundle.getString(FINISHTIME)!!

            Log.d("TAG", "onCreate dsp: ${startTime}")
            Log.d("TAG", "onCreate dsp: ${endTime}")
        }
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
        refreshCommentAdapter(data)
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
                        showPitchData(it.data.data)
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
            if (data.isOpen.open) {
                tvStatus.text = Html.fromHtml("<font color=#177B4C>" + "Ochiq")
                tvTime.text = "${data.isOpen.time.substring(0, 5)} da yopiladi"
            } else {
                tvStatus.text = Html.fromHtml("<font color=#C8303F>" + "Yopiq")
                tvTime.text = "${data.isOpen.time.substring(0, 5)} da ochiladi"
            }
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
            val chooseTimeBottomSheetDialog = ChooseTimeBottomSheetDialog(stadiumId)
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