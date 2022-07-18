package com.footzone.footzone.ui.fragments.played

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.footzone.footzone.R
import com.footzone.footzone.adapter.PlayedPitchAdapter
import com.footzone.footzone.databinding.FragmentPlayedPitchBinding
import com.footzone.footzone.model.PlayedHistoryResponseData
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues.USER_ID
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import com.footzone.footzone.utils.extensions.hide
import com.footzone.footzone.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
        if (sharedPref.getUserID(USER_ID, "").isNotEmpty())
            viewModel.getPlayedHistory(sharedPref.getUserID(USER_ID, ""))
        initViews()
    }

    private fun initViews() {
        playedPitchAdapter = PlayedPitchAdapter()

        observePlayedStadium()
    }

    private fun observePlayedStadium() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playedStadiums.collect {
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
                        }
                    }
                }
            }
        }
    }

    private fun refreshAdapter(playedStadiums: List<PlayedHistoryResponseData>) {

        if (playedStadiums.isEmpty()) {
            binding.tvEmptyListAlert.show()
            binding.rvPlayedStadiums.hide()
            return
        } else {
            binding.tvEmptyListAlert.hide()
            binding.rvPlayedStadiums.show()
        }

        playedPitchAdapter.submitData(playedStadiums)
        binding.rvPlayedStadiums.adapter = playedPitchAdapter
    }
}