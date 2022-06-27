package com.footzone.footzone.ui.fragments.playing

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
import com.footzone.footzone.adapter.PlayingPitchAdapter
import com.footzone.footzone.databinding.FragmentPlayingPitchBinding
import com.footzone.footzone.helper.OnClickEventPlayingSoon
import com.footzone.footzone.model.PlayedHistoryResponseData
import com.footzone.footzone.model.PlayingSoonHistoryResponseData
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayingPitchFragment : BaseFragment(R.layout.fragment_playing_pitch) {

    private lateinit var binding: FragmentPlayingPitchBinding
    private lateinit var playingPitchAdapter: PlayingPitchAdapter
    private val viewModel by viewModels<PlayingPitchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPlayingPitchBinding.bind(view)
        viewModel.getPlayingSoonStadium()
        Log.d("TAG", "onViewCreated: okkkk")
        initViews()
    }

    private fun initViews() {
        playingPitchAdapter = PlayingPitchAdapter(object : OnClickEventPlayingSoon {
            override fun onNavigateClick(latitude: Double, longitude: Double) {
                requireActivity().shareLocationToGoogleMap(latitude, longitude)
            }

            override fun onStadiumClick(stadiumId: String) {
                openPitchDetailFragment(stadiumId, false)
            }
        })

        observePlayingSoon()
    }

    private fun observePlayingSoon() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playingSoonStadiums.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            refreshAdapter(it.data.data)
                        }
                        is UiStateObject.ERROR -> {
                            hideProgress()
                        }
                        else -> {
                            hideProgress()
                        }
                    }
                }
            }
        }
    }

    private fun openPitchDetailFragment(stadiumId: String, isFavourite: Boolean) {
        findNavController().navigate(
            R.id.action_tableFragment_to_pitchDetailFragment,
            bundleOf(
                KeyValues.STADIUM_ID to stadiumId,
                KeyValues.IS_FAVOURITE_STADIUM to isFavourite
            )
        )
    }

    private fun refreshAdapter(playingSoonList: List<PlayingSoonHistoryResponseData>) {

        if (playingSoonList.isEmpty()) {
            binding.tvEmptyListAlert.visibility = View.VISIBLE
            binding.rvPlayingSoon.visibility = View.GONE
            return
        } else {
            binding.tvEmptyListAlert.visibility = View.GONE
            binding.rvPlayingSoon.visibility = View.VISIBLE
        }

        playingPitchAdapter.submitData(playingSoonList)
        binding.rvPlayingSoon.adapter = playingPitchAdapter
    }
}