package com.footzone.footzone.ui.fragments.bookpitchsent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.footzone.footzone.R
import com.footzone.footzone.adapter.PitchBookSentAdapter
import com.footzone.footzone.databinding.FragmentBookPitchSentBinding
import com.footzone.footzone.databinding.LayoutAcceptBinding
import com.footzone.footzone.databinding.LayoutDeclineBinding
import com.footzone.footzone.helper.OnClickEventAcceptDecline
import com.footzone.footzone.model.AcceptDeclineRequest
import com.footzone.footzone.model.Hour
import com.footzone.footzone.model.PitchHistory
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.AcceptDeclineDialog
import com.footzone.footzone.utils.UiStateObject
import java.util.ArrayList

class BookPitchSentFragment : BaseFragment(R.layout.fragment_book_pitch_sent) {

    private lateinit var binding: FragmentBookPitchSentBinding
    private lateinit var playingPitchAdapter: PitchBookSentAdapter
    private val viewModel by viewModels<BookPitchSentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookPitchSentBinding.bind(view)

        initViews()
    }

    private fun initViews() {
        playingPitchAdapter = PitchBookSentAdapter { toAccept, stadiumId ->
            manageAcceptDeclineClick(toAccept, stadiumId)
        }
        playingPitchAdapter.submitData(getPlayedPitchHistory())
        refreshAdapter()
    }

    private fun manageAcceptDeclineClick(toAccept: Boolean, stadiumId: String) {
        if (toAccept) {
            val acceptDeclineDialog =
                AcceptDeclineDialog(requireContext(), object : OnClickEventAcceptDecline {
                    override fun accept(isAccepted: Boolean) {
                        viewModel.acceptOrDeclineBookingRequest(
                            AcceptDeclineRequest(
                                true,
                                stadiumId
                            )
                        )
                    }

                    override fun decline(isAccepted: Boolean) {}

                }).instance(
                    LayoutAcceptBinding.inflate(
                        LayoutInflater.from(requireContext())
                    ).root
                )
            acceptDeclineDialog.show()
        } else {
            val acceptDeclineDialog =
                AcceptDeclineDialog(requireContext(), object : OnClickEventAcceptDecline {
                    override fun accept(isAccepted: Boolean) {}

                    override fun decline(isAccepted: Boolean) {
                        viewModel.acceptOrDeclineBookingRequest(
                            AcceptDeclineRequest(
                                false,
                                stadiumId
                            )
                        )
                    }
                }).instance(
                    LayoutDeclineBinding.inflate(
                        LayoutInflater.from(requireContext())
                    ).root
                )
            acceptDeclineDialog.show()
        }
        observeAcceptDeclineResponse()
    }

    private fun observeAcceptDeclineResponse() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.acceptDecline.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "observeNearByStadiums: $it.data")
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

    private fun refreshAdapter() {
        binding.rvPlayingPitches.adapter = playingPitchAdapter
    }

    private fun getPlayedPitchHistory(): ArrayList<PitchHistory> {
        return ArrayList<PitchHistory>().apply {
            for (i in 0..5) {
                this.add(PitchHistory("Acme", "29-may, chorshanba", Hour("16:00", "18:00"), 100000))
            }
        }
    }
}