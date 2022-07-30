package com.footzone.footzone.ui.fragments.bookpitchsent

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.PENDING
import com.footzone.footzone.utils.UiStateObject
import com.footzone.footzone.utils.extensions.hide
import com.footzone.footzone.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BookPitchSentFragment : BaseFragment(R.layout.fragment_book_pitch_sent) {

    private lateinit var acceptDialog: AcceptDialog
    private lateinit var declineDialog: DeclineDialog
    private lateinit var binding: FragmentBookPitchSentBinding
    private lateinit var playingPitchAdapter: PitchBookSentAdapter
    private val viewModel by viewModels<BookPitchSentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sendRequest()
    }

    private fun sendRequest() {
        viewModel.getSentBookingRequests(PENDING)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookPitchSentBinding.bind(view)

        initViews()
    }

    private fun initViews() {
        observeBookSentList()

        binding.apply {
            swipeContainer.setOnRefreshListener {
                sendRequest()
            }
        }
    }


    private fun observeBookSentList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookRequests.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            binding.swipeContainer.isRefreshing = false
                            refreshAdapter(it.data.data)
                        }

                        is UiStateObject.ERROR -> {
                            binding.swipeContainer.isRefreshing = false
                            hideProgress()
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun observeAcceptDeclineResponse(
        isToAccept: Boolean,
        tvStatus: TextView,
        linearAcceptDecline: LinearLayout,
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.acceptDecline.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            if (isToAccept) {
                                if (it.data.success) {
                                    acceptDialog.dismiss()
                                    tvStatus.show()
                                    linearAcceptDecline.hide()
                                }
                            } else {
                                if (it.data.success) {
                                    declineDialog.dismiss()
                                    tvStatus.setTextColor(Color.parseColor("#C8303F"))
                                    tvStatus.text = KeyValues.DECLINED_2
                                    tvStatus.show()
                                    linearAcceptDecline.hide()
                                }
                            }
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

    private fun refreshAdapter(requests: List<StadiumBookSentResponseData>) {
        if (requests.isEmpty()) {
            binding.tvEmptyListAlert.show()
            binding.rvBookSent.hide()
            return
        } else {
            binding.tvEmptyListAlert.hide()
            binding.rvBookSent.show()
        }
        playingPitchAdapter = PitchBookSentAdapter(requests, object : OnClickEventAcceptDecline {
            override fun onAccept(
                stadiumId: String,
                tvStatus: TextView,
                linearAcceptDecline: LinearLayout,
                position: Int,
            ) {
                acceptDialog =
                    AcceptDialog(requireContext()) {
                        //send accepted ok
                        viewModel.acceptOrDeclineBookingRequest(
                            AcceptDeclineRequest(
                                true,
                                stadiumId
                            )
                        )
                        observeAcceptDeclineResponse(true, tvStatus, linearAcceptDecline)
                    }.instance(
                        LayoutAcceptBinding.inflate(
                            LayoutInflater.from(requireContext())
                        ).root
                    )
                acceptDialog.show()
            }

            override fun onDecline(
                stadiumId: String,
                tvStatus: TextView,
                linearAcceptDecline: LinearLayout,
                position: Int,
            ) {
                declineDialog =
                    DeclineDialog(requireContext()) {
                        viewModel.acceptOrDeclineBookingRequest(
                            AcceptDeclineRequest(
                                false,
                                stadiumId
                            )
                        )
                        observeAcceptDeclineResponse(false, tvStatus, linearAcceptDecline)
                    }.instance(
                        LayoutDeclineBinding.inflate(
                            LayoutInflater.from(requireContext())
                        ).root
                    )
                declineDialog.show()
            }
        })

        binding.rvBookSent.adapter = playingPitchAdapter
    }

}