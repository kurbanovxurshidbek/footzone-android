package com.footzone.footzone.ui.fragments.stadium

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.adapter.CommentAdapter
import com.footzone.footzone.adapter.CustomAdapter
import com.footzone.footzone.databinding.FragmentStadiumBinding
import com.footzone.footzone.model.TimeManager
import com.footzone.footzone.model.holders.Comment
import com.footzone.footzone.model.holders.Data
import com.footzone.footzone.model.holders.Photo
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.PITCH_DETAIL
import java.util.*
import kotlin.collections.ArrayList


class StadiumFragment : BaseFragment(R.layout.fragment_stadium) {

    private lateinit var binding: FragmentStadiumBinding
    lateinit var adapter: CustomAdapter
    lateinit var adapterComment: CommentAdapter
    lateinit var data: Data
    var times: ArrayList<TimeManager> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = arguments?.get(PITCH_DETAIL) as Data
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStadiumBinding.bind(view)
        initViews()
    }

    private fun initViews() {
        refreshAdapter()
        refreshCommentAdapter()
        binding.rbRate.setIsIndicator(true)

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.etStadium.setOnClickListener { openEditStadium(data) }

        binding.linearNavigation.setOnClickListener {
            requireActivity().shareLocationToGoogleMap(41.33324, 69.21896)
        }
    }

    private fun refreshAdapter() {
        adapter = CustomAdapter(data.photos as ArrayList<Photo>)
        binding.recyclerView.adapter = adapter
    }

    private fun refreshCommentAdapter() {
        adapterComment = CommentAdapter(data.comments as ArrayList<Comment>)
        binding.recyclerViewComment.adapter = adapterComment
    }


    private fun openEditStadium(data: Data) {
        findNavController().navigate(
            R.id.action_stadiumFragment_to_addStadiumFragment,
            bundleOf(PITCH_DETAIL to data, KeyValues.TYPE_DETAIL to 1)
        )
    }
}