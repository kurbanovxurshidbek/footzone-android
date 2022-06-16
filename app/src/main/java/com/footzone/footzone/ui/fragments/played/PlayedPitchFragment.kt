package com.footzone.footzone.ui.fragments.played

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.footzone.footzone.R
import com.footzone.footzone.adapter.PlayedPitchAdapter
import com.footzone.footzone.databinding.FragmentPlayedPitchBinding
import com.footzone.footzone.model.PlayedHistoryResponseData
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues.USER_ID
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayedPitchFragment : BaseFragment(R.layout.fragment_played_pitch) {

    private lateinit var binding: FragmentPlayedPitchBinding
    private lateinit var playedPitchAdapter: PlayedPitchAdapter
    private val viewModel by viewModels<PlayedPitchViewModel>()

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPlayedPitchBinding.bind(view)

        viewModel.getPlayedHistory(sharedPref.getUserID(USER_ID, ""))
        initViews()
    }

    private fun initViews() {
        playedPitchAdapter = PlayedPitchAdapter()

        observePlayedStadium()
    }

    private fun observePlayedStadium() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.playedStadiums.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "observePlayedStadium: ${it.data}")
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

    private fun refreshAdapter(data: List<PlayedHistoryResponseData>) {
         playedPitchAdapter.submitData(data)
        binding.rvPlayedPitches.adapter = playedPitchAdapter
    }
}