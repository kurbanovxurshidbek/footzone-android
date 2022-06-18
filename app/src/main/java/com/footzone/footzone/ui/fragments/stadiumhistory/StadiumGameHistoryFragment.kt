package com.footzone.footzone.ui.fragments.stadiumhistory

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.footzone.footzone.R
import com.footzone.footzone.adapter.StadiumPlayedHistoryAdapter
import com.footzone.footzone.databinding.FragmentPlayedPitchBinding
import com.footzone.footzone.databinding.FragmentStadiumGameHistoryBinding
import com.footzone.footzone.model.StadiumBookSentResponseData
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StadiumGameHistoryFragment : BaseFragment(R.layout.fragment_stadium_game_history) {

    private lateinit var binding: FragmentStadiumGameHistoryBinding
    private lateinit var playedPitchAdapter: StadiumPlayedHistoryAdapter
    private val viewModel by viewModels<StadiumHistoryViewModel>()

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStadiumGameHistoryBinding.bind(view)
        if (sharedPref.getUserID(KeyValues.USER_ID, "").isNotEmpty())
            viewModel.getStadiumPlayedHistory("PLAYED")
        initViews()
    }

    private fun initViews() {
        playedPitchAdapter = StadiumPlayedHistoryAdapter()

        observePlayedStadium()
    }

    private fun observePlayedStadium() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.stadiumPlayedHistory.collect {
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

    private fun refreshAdapter(history: List<StadiumBookSentResponseData>) {
        if (history.isEmpty()) {
            binding.tvEmptyListAlert.visibility = View.VISIBLE
            binding.rvStadiumGameHistory.visibility = View.GONE
            return
        } else {
            binding.tvEmptyListAlert.visibility = View.GONE
            binding.rvStadiumGameHistory.visibility = View.VISIBLE
        }

        playedPitchAdapter.submitData(history)
        binding.rvStadiumGameHistory.adapter = playedPitchAdapter
    }
}