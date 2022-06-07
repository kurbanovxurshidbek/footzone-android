package com.footzone.footzone.ui.fragments.stadiumdetail

import android.os.Bundle
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
import com.footzone.footzone.model.Pitch
import com.footzone.footzone.model.TimeManager
import com.footzone.footzone.model.holders.Comment
import com.footzone.footzone.model.holders.Photo
import com.footzone.footzone.ui.fragments.ChooseTimeBottomSheetDialog
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues.PITCH_DETAIL
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PitchDetailFragment : Fragment() {

    private lateinit var binding: FragmentPitchDetailBinding
    lateinit var adapter: CustomAdapter
    lateinit var adapterComment: CommentAdapter
    private val viewModel by viewModels<PitchDetailViewModel>()
    lateinit var pitch: Pitch
    var times: ArrayList<TimeManager> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPitchData("4805e032-454a-40b5-9dda-ed8e06a1d3cc")
        pitch = arguments?.get(PITCH_DETAIL) as Pitch
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_pitch_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPitchDetailBinding.bind(view)
        setupObservers()
        initViews()
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

    private fun showPitchData(data: Any) {
        Log.d("@@@", "showPitchData: ")
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
            chooseTimeBottomSheetDialog.show(childFragmentManager,chooseTimeBottomSheetDialog.tag)
        }

        binding.linearNavigation.setOnClickListener {
            requireActivity().shareLocationToGoogleMap(41.33324, 69.21896)
        }
    }

    private fun refreshAdapter() {
        adapter = CustomAdapter(ArrayList<Photo>())
        binding.recyclerView.adapter = adapter
    }

    private fun refreshCommentAdapter() {
        adapterComment = CommentAdapter(getComments())
        binding.recyclerViewComment.adapter = adapterComment
    }

    private fun getComments(): ArrayList<Comment> {
        val items = ArrayList<Comment>()

        return items
    }
}