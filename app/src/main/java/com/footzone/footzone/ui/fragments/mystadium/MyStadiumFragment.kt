package com.footzone.footzone.ui.fragments.mystadium

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.adapter.MyPitchAdapter
import com.footzone.footzone.databinding.FragmentMyStadiumBinding
import com.footzone.footzone.model.holderpitchs.Comment
import com.footzone.footzone.model.holderpitchs.Data
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.USER_ID
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MyStadiumFragment : BaseFragment(R.layout.fragment_my_stadium) {

    lateinit var binding: FragmentMyStadiumBinding
    private val viewModel by viewModels<MyStadiumViewModel>()
    @Inject
    lateinit var sharedPref: SharedPref

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyStadiumBinding.bind(view)
        val userId = sharedPref.getUserID(USER_ID, "")
        viewModel.getHolderStadiums(userId)
        setupObservers()
        initViews()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.holderStadiums.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        val holderStadiumsList = it.data.data as ArrayList<Data>
                        controlVisibility(holderStadiumsList.isEmpty())
                        if (holderStadiumsList.isNotEmpty()) {
                            refreshAdapter(holderStadiumsList)
                        }
                    }
                    is UiStateObject.ERROR -> {

                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun initViews() {
        binding.apply {
            icClose.setOnClickListener { requireActivity().onBackPressed() }
        }

        binding.ivAdd.setOnClickListener {
            openAddStadium()
        }
    }

    private fun controlVisibility(isListEmpty: Boolean) {
        if (!isListEmpty) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.llView.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.llView.visibility = View.VISIBLE
            binding.tvJustText.text =
                "Hozir sizda maydonlar mavjud emas.\nMaydon qo’shish uchun yuqoridagi\n“+” tugmasini bosing"

        }
    }

    private fun refreshAdapter(stadiums: ArrayList<Data>) {
        val adapter = MyPitchAdapter(this, stadiums) { stadiumId ->
            openEditStadium(stadiumId)
        }
        binding.recyclerView.adapter = adapter
    }


    fun openMap() {
        requireActivity().shareLocationToGoogleMap(41.33324, 69.21896)
    }

    private fun openAddStadium() {
        findNavController().navigate(R.id.action_myStadiumFragment_to_addStadiumFragment,
            bundleOf(KeyValues.TYPE_DETAIL to false))
    }

    private fun openEditStadium(stadiumId: String) {
        findNavController().navigate(
            R.id.action_myStadiumFragment_to_stadiumFragment,
            bundleOf(KeyValues.PITCH_DETAIL to stadiumId)
        )
    }
}