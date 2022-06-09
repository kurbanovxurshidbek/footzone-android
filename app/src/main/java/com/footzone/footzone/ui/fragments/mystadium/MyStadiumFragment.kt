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
import com.footzone.footzone.model.holders.Comment
import com.footzone.footzone.model.holders.Data
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class MyStadiumFragment : BaseFragment(R.layout.fragment_my_stadium) {

    lateinit var binding: FragmentMyStadiumBinding
    private val viewModel by viewModels<MyStadiumViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyStadiumBinding.bind(view)
        viewModel.getHolderStadiums("347829e7-832c-4257-9d26-25439b5f8d61")
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

                        Log.d("TAG", "setupObservers: ${holderStadiumsList}")
                        if (holderStadiumsList.isNotEmpty()) {
                            refreshAdapter(holderStadiumsList)
                        }
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupObservers:${it}")
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
        val adapter = MyPitchAdapter(this, stadiums) { data ->
            openEditStadium(data)
        }
        binding.recyclerView.adapter = adapter
    }

    fun resRating(comments: ArrayList<Comment>): Float {
        return try {
            (comments.sumOf { it.number * it.rate } / comments.sumOf { it.number }).toFloat()
        } catch (e: Exception) {
            2.5f
        }
    }


    fun openMap() {
        requireActivity().shareLocationToGoogleMap(41.33324, 69.21896)
    }

    private fun openAddStadium() {
        findNavController().navigate(R.id.action_myStadiumFragment_to_addStadiumFragment,
            bundleOf(KeyValues.TYPE_DETAIL to 2))
    }

    private fun openEditStadium(data: Data) {
        findNavController().navigate(
            R.id.action_myStadiumFragment_to_stadiumFragment,
            bundleOf(KeyValues.PITCH_DETAIL to data)
        )
    }
}