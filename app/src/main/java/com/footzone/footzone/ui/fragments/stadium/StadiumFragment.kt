package com.footzone.footzone.ui.fragments.stadium

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.adapter.CustomAdapter
import com.footzone.footzone.adapter.HolderCommentAdapter
import com.footzone.footzone.adapter.HolderStadiumAdapter
import com.footzone.footzone.databinding.FragmentStadiumBinding
import com.footzone.footzone.model.holderstadium.Comment
import com.footzone.footzone.model.holderstadium.Data
import com.footzone.footzone.model.holderstadium.Photo
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.PITCH_DETAIL
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StadiumFragment : BaseFragment(R.layout.fragment_stadium) {

    private lateinit var binding: FragmentStadiumBinding
    lateinit var adapter: CustomAdapter
    lateinit var stadiumId: String
    private val viewModel by viewModels<StadiumViewModel>()

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
        viewModel.getHolderStadiums(stadiumId)
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getHolderStadium.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "setupObservers: ${it}")
                        refreshData(it.data.data)
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupObservers:${it}")
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun refreshData(data: com.footzone.footzone.model.holderstadium.Data) {
        binding.apply {
            tvAppBarPitchName.text = data.name
            tvStadiumName.text = data.name
            textViewAddress.text = data.address
            textViewNumber.text = data.number

            textViewRateCount.text = data.comments.size.toString()

            if (data.isOpen.open) {
                tvOpenClose.text = Html.fromHtml("<font color=#177B4C>" + "Ochiq")
                tvOpenCloseHour.text = " · ${data.isOpen.time.substring(0, 5)} da yopiladi"
            } else {
                if (data.isOpen.time != null){
                    tvOpenClose.text = Html.fromHtml("<font color=#C8303F>" + "Yopiq")
                    tvOpenCloseHour.text = " · ${data.isOpen.time.substring(0, 5)} da ochiladi"
                }else{
                    tvOpenCloseHour.text = "Stadion bugun ishlamaydi."
                    tvOpenClose.visibility = View.GONE
                }
            }

            textViewPrice.text = data.hourlyPrice.toString()

            linearNavigation.setOnClickListener {
                requireActivity().shareLocationToGoogleMap(data.latitude, data.longitude)
            }
            rbRate.setIsIndicator(true)

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        refreshAdapter(data.photos as ArrayList<Photo>)
        refreshCommentAdapter(data.comments as ArrayList<Comment>)
        resultRate(data.comments)

        binding.etStadium.setOnClickListener {
            openEditStadium(data.stadiumId)
        }
    }

    private fun openEditStadium(stadiumId: String) {
        findNavController().navigate(
            R.id.action_stadiumFragment_to_addStadiumFragment,
            bundleOf( KeyValues.TYPE_DETAIL to true, KeyValues.STADIUM_ID to stadiumId)
        )
    }

    private fun refreshAdapter(items: ArrayList<Photo>) {
        val adapter = HolderStadiumAdapter(items)
        binding.recyclerView.adapter = adapter
    }

    private fun refreshCommentAdapter(comments: ArrayList<Comment>) {
        val adapterComment = HolderCommentAdapter(comments)
        binding.recyclerViewComment.adapter = adapterComment
    }

    private fun resultRate(comments: ArrayList<Comment>){
        var rate5 = 0
        var rate4 = 0
        var rate3 = 0
        var rate2 = 0
        var rate1 = 0
        var result = 0
        for (comment in comments){
            if (comment.rate == 5){
                rate5++
            }else if (comment.rate == 4){
                rate4++
            }else if (comment.rate == 3){
                rate3++
            }else if (comment.rate == 2){
                rate2++
            }else{
                rate1++
            }
            result = result + comment.rate
        }
        binding.apply {
            tvResultRate.text = (result.toFloat() / comments.size).toString()
            lpRate5.progress = ((result.toFloat() / rate5) * 100).toInt()
            lpRate4.progress = ((result.toFloat() / rate4) * 100).toInt()
            lpRate3.progress = ((result.toFloat() / rate3) * 100).toInt()
            lpRate2.progress = ((result.toFloat() / rate2) * 100).toInt()
            lpRate1.progress = ((result.toFloat() / rate1) * 100).toInt()
        }
    }
}