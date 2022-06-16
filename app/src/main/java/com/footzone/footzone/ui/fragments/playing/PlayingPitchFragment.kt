package com.footzone.footzone.ui.fragments.playing

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.footzone.footzone.R
import com.footzone.footzone.adapter.PlayingPitchAdapter
import com.footzone.footzone.databinding.FragmentPlayingPitchBinding
import com.footzone.footzone.model.PlayedHistoryResponseData
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayingPitchFragment : BaseFragment(R.layout.fragment_playing_pitch) {

    private lateinit var binding: FragmentPlayingPitchBinding
    private lateinit var playingPitchAdapter: PlayingPitchAdapter
    private val viewModel by viewModels<PlayingPitchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPlayingPitchBinding.bind(view)
        viewModel.getPlayingSoonStadium()

        initViews()
    }

    private fun initViews() {
        playingPitchAdapter = PlayingPitchAdapter()

        observePlayingSoon()
    }

    private fun observePlayingSoon() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.playingSoonStadiums.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "observePlayingSoon: ${it.data}")
                        refreshAdapter(it.data.data)
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

    private fun refreshAdapter(playingSoonList: List<PlayedHistoryResponseData>) {
        playingPitchAdapter.submitData(playingSoonList)
        binding.rvPlayingPitches.adapter = playingPitchAdapter
    }
}