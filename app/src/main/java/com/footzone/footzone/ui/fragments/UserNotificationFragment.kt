package com.footzone.footzone.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.footzone.footzone.R
import com.footzone.footzone.adapter.UserNotificationAdapter
import com.footzone.footzone.databinding.FragmentUserNotificationBinding
import com.footzone.footzone.model.UserNotification


class UserNotificationFragment : Fragment() {
    lateinit var binding: FragmentUserNotificationBinding
    lateinit var adapter: UserNotificationAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserNotificationBinding.bind(view)
        initViews()
    }

    private fun initViews() {
        refreshAdapter()
    }

    private fun refreshAdapter() {
        adapter = UserNotificationAdapter(getAllUserNotifications())
        binding.userNotificationItems.adapter = adapter
    }

    private fun getAllUserNotifications(): ArrayList<UserNotification> {
        var items = ArrayList<UserNotification>()
        items.add(UserNotification("Maydon band qilish uchun so’rovingiz maydon egasiga yuborildi. So’rovingiz qabul qilinishi yoki rad etilishi bilanoq sizga bildirishnoma keladi. So’rovingiz qabul qilinganda maydon “Jadval” bo’limida paydo bo’ladi.","17.04.2022 • 16:04",false))
        items.add(UserNotification("Maydon band qilish uchun so’rovingiz maydon egasiga yuborildi. So’rovingiz qabul qilinishi yoki rad etilishi bilanoq sizga bildirishnoma keladi. So’rovingiz qabul qilinganda maydon “Jadval” bo’limida paydo bo’ladi.","17.04.2022 • 16:05",false))
        items.add(UserNotification("Maydon band qilish uchun so’rovingiz maydon egasiga yuborildi. So’rovingiz qabul qilinishi yoki rad etilishi bilanoq sizga bildirishnoma keladi. So’rovingiz qabul qilinganda maydon “Jadval” bo’limida paydo bo’ladi.","17.04.2022 • 16:06",false))
        items.add(UserNotification("Maydon band qilish uchun so’rovingiz maydon egasiga yuborildi. So’rovingiz qabul qilinishi yoki rad etilishi bilanoq sizga bildirishnoma keladi. So’rovingiz qabul qilinganda maydon “Jadval” bo’limida paydo bo’ladi.","17.04.2022 • 16:07",false))
        items.add(UserNotification("Maydon band qilish uchun so’rovingiz maydon egasiga yuborildi. So’rovingiz qabul qilinishi yoki rad etilishi bilanoq sizga bildirishnoma keladi. So’rovingiz qabul qilinganda maydon “Jadval” bo’limida paydo bo’ladi.","17.04.2022 • 16:08",true))
        return items
    }
}