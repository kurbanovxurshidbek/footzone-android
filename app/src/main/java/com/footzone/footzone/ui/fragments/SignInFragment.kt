package com.footzone.footzone.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentPlayedPitchBinding
import com.footzone.footzone.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
    lateinit var binding: FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignInBinding.bind(view)

        initViews()
    }

    private fun initViews() {
        binding.textViewSignUp.setOnClickListener {
            openSignUpFragment()
        }
        binding.backButton.setOnClickListener {
            closeSignInFragment()
        }
        checkAllFields()
    }

    private fun checkAllFields() {
        binding.editTextNumber.doAfterTextChanged {
            enterButtonControl()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun enterButtonControl() {
        if (checkData()) {
            binding.apply {
                enterButton.setBackgroundResource(R.drawable.button_register_filled_rounded_corner2)
                enterButton.isClickable = true
                enterButton.setTextColor(Color.WHITE)
            }
        } else {
            binding.apply {
                enterButton.setBackgroundResource(R.drawable.button_register_filled_rounded_corner1)
                enterButton.isClickable = false
                enterButton.setTextColor(R.color.buttonDisabledTextColor)
            }
        }
    }

    private fun checkData(): Boolean {
        val number = binding.editTextNumber.text.toString()
        return number.length == 12
    }

    private fun closeSignInFragment() {
        findNavController().popBackStack()
    }

    private fun openSignUpFragment() {
        findNavController().navigate(R.id.signUpFragment)
    }
}