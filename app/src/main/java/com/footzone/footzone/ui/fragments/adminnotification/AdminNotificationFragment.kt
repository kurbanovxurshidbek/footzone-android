package com.footzone.footzone.ui.fragments.adminnotification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.footzone.footzone.R
import com.footzone.footzone.adapter.AdminNotificationAdapter
import com.footzone.footzone.databinding.FragmentAdminNotificationBinding
import com.footzone.footzone.databinding.LayoutAcceptBinding
import com.footzone.footzone.databinding.LayoutDeclineBinding
import com.footzone.footzone.helper.OnClickEventAcceptDecline
import com.footzone.footzone.model.AcceptDeclineRequest
import com.footzone.footzone.model.StadiumBookSentResponseData
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.AcceptDialog
import com.footzone.footzone.utils.DeclineDialog
import com.footzone.footzone.utils.UiStateObject
import com.footzone.footzone.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminNotificationFragment : BaseFragment(R.layout.fragment_admin_notification) {

    private lateinit var declineDialog: DeclineDialog
    private lateinit var acceptDialog: AcceptDialog
    lateinit var binding: FragmentAdminNotificationBinding
    lateinit var adapter: AdminNotificationAdapter
    private val viewModel by viewModels<AdminNotificationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getAllNotifications("ALL")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAdminNotificationBinding.bind(view)

        initViews()
        observeAllNotifications()
    }

    private fun initViews() {
        binding.ivBack.setOnClickListener {
            back()
        }
    }

    private fun observeAllNotifications() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.adminNotification.collect {
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

    private fun refreshAdapter(notifications: List<StadiumBookSentResponseData>) {
        adapter = AdminNotificationAdapter(notifications, object : OnClickEventAcceptDecline {
            override fun onAccept(
                stadiumId: String,
                tvStatus: TextView,
                linearAcceptDecline: LinearLayout,
                position: Int
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
                        observeAcceptDeclineResponse(
                            true,
                            tvStatus,
                            linearAcceptDecline,
                            notifications,
                            position
                        )
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
                position: Int
            ) {
                declineDialog =
                    DeclineDialog(requireContext()) {
                        viewModel.acceptOrDeclineBookingRequest(
                            AcceptDeclineRequest(
                                false,
                                stadiumId
                            )
                        )
                        observeAcceptDeclineResponse(
                            false,
                            tvStatus,
                            linearAcceptDecline,
                            notifications,
                            position
                        )
                    }.instance(
                        LayoutDeclineBinding.inflate(
                            LayoutInflater.from(requireContext())
                        ).root
                    )
                declineDialog.show()
            }
        })
        binding.rvAdminNotification.adapter = adapter
    }

    private fun observeAcceptDeclineResponse(
        isToAccept: Boolean,
        tvStatus: TextView,
        linearAcceptDecline: LinearLayout,
        notifications: List<StadiumBookSentResponseData>,
        position: Int
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
                                    linearAcceptDecline.show()
                                    adapter.changeNotificationStatus("ACCEPTED", position)
                                }
                            } else {
                                if (it.data.success) {
                                    declineDialog.dismiss()
                                    linearAcceptDecline.show()
                                    adapter.changeNotificationStatus("DECLINED", position)
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
}