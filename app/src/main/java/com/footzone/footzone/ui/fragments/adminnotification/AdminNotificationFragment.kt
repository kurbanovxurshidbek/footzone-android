package com.footzone.footzone.ui.fragments.adminnotification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentAdminNotificationBinding
import com.footzone.footzone.ui.fragments.BaseFragment

class AdminNotificationFragment : BaseFragment(R.layout.fragment_admin_notification) {

    lateinit var binding: FragmentAdminNotificationBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAdminNotificationBinding.bind(view)
        initViews()
    }

    private fun initViews() {

    }
}