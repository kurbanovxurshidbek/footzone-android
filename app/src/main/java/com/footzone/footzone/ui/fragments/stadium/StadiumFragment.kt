package com.footzone.footzone.ui.fragments.stadium

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.footzone.footzone.utils.commonfunction.Functions.showStadiumOpenOrClose
import dagger.hilt.android.AndroidEntryPoint

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
        viewModel.getHolderStadiums(stadiumId)
        viewModel.getCommentAllByStadiumId(stadiumId)
        setupObservers()
        setupCommentObservers()
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
            bundleOf( KeyValues.TYPE_DETAIL to true, KeyValues.STADIUM_ID to stadiumId)
        )
    }

    private fun refreshAdapter(items: ArrayList<StadiumPhoto>) {
        val adapter = CustomAdapter(items)
        binding.recyclerView.adapter = adapter
    }


//    private fun refreshCommentAdapter(comments: ArrayList<Comment>) {
//        val adapterComment = HolderCommentAdapter(comments)
//        binding.recyclerViewComment.adapter = adapterComment
//    }
}