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
import com.footzone.footzone.model.StadiumBookSentResponseData
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.AcceptDialog
import com.footzone.footzone.utils.DeclineDialog
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookPitchSentFragment : BaseFragment(R.layout.fragment_book_pitch_sent) {

    private lateinit var binding: FragmentBookPitchSentBinding
    private lateinit var playingPitchAdapter: PitchBookSentAdapter
    private val viewModel by viewModels<BookPitchSentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getSentBookingRequests("PENDING")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookPitchSentBinding.bind(view)

        initViews()
    }


    private fun initViews() {
        observeBookSentList()
    }

    private fun observeBookSentList() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.bookRequests.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "observeBookSentList: ${it.data}")
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

    private fun refreshAdapter(requests: List<StadiumBookSentResponseData>) {
        Log.d("TAG", "refreshAdapter: $requests")
        playingPitchAdapter = PitchBookSentAdapter(requests, object : OnClickEventAcceptDecline {
            override fun onAccept(stadiumId: String) {
                val acceptDialog =
                    AcceptDialog(requireContext()) {
                        //send accepted ok
                        viewModel.acceptOrDeclineBookingRequest(
                            AcceptDeclineRequest(
                                true,
                                stadiumId
                            )
                        )
                        observeAcceptDeclineResponse()
                    }.instance(
                        LayoutAcceptBinding.inflate(
                            LayoutInflater.from(requireContext())
                        ).root
                    )
                acceptDialog.show()
            }

            override fun onDecline(stadiumId: String) {
                val declineDialog =
                    DeclineDialog(requireContext()) {
                        viewModel.acceptOrDeclineBookingRequest(
                            AcceptDeclineRequest(
                                false,
                                stadiumId
                            )
                        )
                        observeAcceptDeclineResponse()
                    }.instance(
                        LayoutDeclineBinding.inflate(
                            LayoutInflater.from(requireContext())
                        ).root
                    )
                declineDialog.show()
            }
        })

        binding.rvPlayingPitches.adapter = playingPitchAdapter
    }
}