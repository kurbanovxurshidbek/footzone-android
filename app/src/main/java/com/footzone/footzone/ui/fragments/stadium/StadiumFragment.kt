package com.footzone.footzone.ui.fragments.stadium

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.adapter.CommentAdapter
import com.footzone.footzone.adapter.CustomAdapter
import com.footzone.footzone.databinding.FragmentStadiumBinding
import com.footzone.footzone.model.*
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.PITCH_DETAIL
import com.footzone.footzone.utils.UiStateObject
import com.footzone.footzone.utils.commonfunction.Functions
import com.footzone.footzone.utils.commonfunction.Functions.showStadiumOpenOrClose
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StadiumFragment : BaseFragment(R.layout.fragment_stadium) {

    private lateinit var binding: FragmentStadiumBinding
    lateinit var adapter: CustomAdapter
    lateinit var stadiumId: String
    private val viewModel by viewModels<StadiumViewModel>()
    lateinit var adapterComment: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.get(PITCH_DETAIL).toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStadiumBinding.bind(view)
        initViews()
    }

    private fun initViews() {

        binding.ivBack.setOnClickListener {
            back()
        }

        viewModel.getHolderStadiums(stadiumId)
        viewModel.getCommentAllByStadiumId(stadiumId)
        setupObservers()
        setupCommentObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getHolderStadium.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            refreshData(it.data.data)
                        }
                        is UiStateObject.ERROR -> {
                            hideProgress()
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun setupCommentObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pitchComment.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            //show progress
                        }

                        is UiStateObject.SUCCESS -> {
                            showPitchComments(it.data.data)
                        }
                        is UiStateObject.ERROR -> {
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun showPitchComments(data: Data) {
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

    private fun refreshCommentAdapter(data: Data) {
        adapterComment = CommentAdapter(data.allComments, requireContext())
        binding.recyclerViewComment.adapter = adapterComment
    }

    private fun refreshData(data: StadiumData) {
        binding.apply {
            tvAppBarPitchName.text = data.stadiumName
            tvStadiumName.text = data.stadiumName
            textViewAddress.text = data.address
            textViewNumber.text = data.number

            showStadiumOpenOrClose(tvOpenClose, tvOpenCloseHour, data.isOpen)

            textViewPrice.text = data.hourlyPrice.toString()

            linearNavigation.setOnClickListener {
                requireActivity().shareLocationToGoogleMap(data.latitude, data.longitude)
            }

            binding.linearShare.setOnClickListener {
                shareLocation(data.longitude, data.latitude, data.stadiumName)
            }

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        refreshAdapter(data.photos)

        binding.etStadium.setOnClickListener {
            openEditStadium(data.stadiumId)
        }
    }

    private fun openEditStadium(stadiumId: String) {
        findNavController().navigate(
            R.id.action_stadiumFragment_to_addStadiumFragment,
            bundleOf(KeyValues.TYPE_DETAIL to true, KeyValues.STADIUM_ID to stadiumId)
        )
    }

    private fun refreshAdapter(items: ArrayList<StadiumPhoto>) {
        val adapter = CustomAdapter(items)
        binding.recyclerView.adapter = adapter
    }
}