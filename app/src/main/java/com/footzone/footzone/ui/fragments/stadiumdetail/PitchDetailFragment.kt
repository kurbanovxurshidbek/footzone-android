package com.footzone.footzone.ui.fragments.stadiumdetail

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.footzone.footzone.R
import com.footzone.footzone.adapter.CommentAdapter
import com.footzone.footzone.adapter.CustomAdapter
import com.footzone.footzone.databinding.FragmentPitchDetailBinding
import com.footzone.footzone.model.FullComment
import com.footzone.footzone.model.StadiumData
import com.footzone.footzone.model.TimeManager
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.ui.fragments.ChooseTimeBottomSheetDialog
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues.STADIUM_ID
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PitchDetailFragment : BaseFragment(R.layout.fragment_pitch_detail) {

    private lateinit var binding: FragmentPitchDetailBinding
    lateinit var adapter: CustomAdapter
    lateinit var adapterComment: CommentAdapter
    private val viewModel by viewModels<PitchDetailViewModel>()
    lateinit var stadiumId: String
    var times: ArrayList<TimeManager> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.get(STADIUM_ID).toString()
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

    private fun showPitchComments(data: Any) {
        Log.d("@@comments", data.toString())
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
            tvAppBarPitchName.text = data.stadiumName
            tvStadiumName.text = data.stadiumName
            tvNumber.text = data.number
            if (data.isOpen.open) {
                tvStatus.text = Html.fromHtml("<font color=#177B4C>" + "Ochiq")
                tvTime.text = " · ${data.isOpen.time} da yopiladi"
            } else {
                tvStatus.text = Html.fromHtml("<font color=#C8303F>" + "Yopiq")
                tvTime.text = " · ${data.isOpen.time} da ochiladi"
            }
            tvPrice.text = data.hourlyPrice.toString()
        }
    }

    private fun initViews() {

        refreshAdapter()
        refreshCommentAdapter()
        binding.rbRate.setIsIndicator(true)

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }


        binding.btnOpenBottomSheet.setOnClickListener {
            val chooseTimeBottomSheetDialog = ChooseTimeBottomSheetDialog()
            chooseTimeBottomSheetDialog.show(childFragmentManager, chooseTimeBottomSheetDialog.tag)
        }

        binding.linearNavigation.setOnClickListener {
            requireActivity().shareLocationToGoogleMap(41.33324, 69.21896)
        }
    }

    private fun refreshAdapter() {
        adapter = CustomAdapter(ArrayList())
        binding.recyclerView.adapter = adapter
    }

    private fun refreshCommentAdapter() {
        adapterComment = CommentAdapter(getComments())
        binding.recyclerViewComment.adapter = adapterComment
    }

    private fun getComments(): ArrayList<FullComment> {
        return ArrayList<FullComment>()
    }
}