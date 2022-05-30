package com.footzone.footzone.ui.fragments

import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.CalendarDIalog
import com.footzone.footzone.R
import com.footzone.footzone.adapter.CommentAdapter
import com.footzone.footzone.adapter.CustomAdapter
import com.footzone.footzone.databinding.FragmentPitchDetailBinding
import com.footzone.footzone.databinding.FragmentStadiumBinding
import com.footzone.footzone.model.Comment
import com.footzone.footzone.model.Pitch
import com.footzone.footzone.model.TimeManager
import com.footzone.footzone.utils.Extensions.changeTextBackgroundBlue
import com.footzone.footzone.utils.Extensions.changeTextColorGreen
import com.footzone.footzone.utils.Extensions.changeTextColorRed
import com.footzone.footzone.utils.Extensions.changeTextColorYellow
import com.footzone.footzone.utils.Extensions.hideBottomSheet
import com.footzone.footzone.utils.Extensions.setImageViewBusy
import com.footzone.footzone.utils.Extensions.setImageViewisBusy
import com.footzone.footzone.utils.Extensions.showBottomSheet
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.PITCH_DETAIL
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*


class StadiumFragment : Fragment() {

    private lateinit var binding: FragmentStadiumBinding
    lateinit var adapter: CustomAdapter
    lateinit var adapterComment: CommentAdapter
    lateinit var pitch: Pitch
    var times: ArrayList<TimeManager> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pitch = arguments?.get(PITCH_DETAIL) as Pitch
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_stadium, container, false)
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

        binding.etStadium.setOnClickListener { openEditStadium(pitch) }
    }

    private fun refreshAdapter() {
        adapter = CustomAdapter(pitch.images)
        binding.recyclerView.adapter = adapter
    }

    private fun refreshCommentAdapter() {
        adapterComment = CommentAdapter(getComments())
        binding.recyclerViewComment.adapter = adapterComment
    }

    private fun getComments(): ArrayList<Comment> {
        val items = ArrayList<Comment>()
        items.add(
            Comment(
                "Jonibek Xolmonov",
                3.5f,
                "18.05.2002",
                "Measure the view and its content to determine the measured width and the measured height. This method is invoked by measure(int, int) and should be overridden by subclasses to provide accurate and efficient measurement of their contents."
            )
        )
        items.add(
            Comment(
                "Odilbek Rustamov",
                2f,
                "11.05.2002",
                "CONTRACT: When overriding this method, you must call setMeasuredDimension(int, int) to store the measured width and height of this view. Failure to do so will trigger an IllegalStateException, thrown by measure(int, int). Calling the superclass' onMeasure(int, int) is a valid use."
            )
        )
        return items
    }

    private fun openEditStadium(pitch: Pitch) {
        findNavController().navigate(
            R.id.action_stadiumFragment_to_addStadiumFragment,
            bundleOf(PITCH_DETAIL to pitch, KeyValues.TYPE_DETAIL to 1)
        )
    }
}