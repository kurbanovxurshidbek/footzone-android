package com.footzone.footzone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentStadiumLocationBinding
import com.footzone.footzone.utils.KeyValues


class StadiumLocationFragment : Fragment() {
    lateinit var binding: FragmentStadiumLocationBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_stadium_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStadiumLocationBinding.bind(view)
        initViews()
    }


    private fun initViews() {
        binding.apply {
            icClose.setOnClickListener { requireActivity().onBackPressed() }
            tvCancel.setOnClickListener { requireActivity().onBackPressed() }
            tvSelection.setOnClickListener {
                val result = "Toshkent, Bodomzor ko'chasi"
                setFragmentResult(KeyValues.TYPE_LOCATION, bundleOf("bundleKey" to result))
                requireActivity().onBackPressed()
            }
        }
    }
}