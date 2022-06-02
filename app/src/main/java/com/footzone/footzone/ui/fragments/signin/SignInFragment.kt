package com.footzone.footzone.ui.fragments.signin

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
import com.footzone.footzone.databinding.FragmentSignInBinding
import com.footzone.footzone.ui.fragments.BaseFragment

class SignInFragment : BaseFragment(R.layout.fragment_sign_in) {
    lateinit var binding: FragmentSignInBinding

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
        binding.enterButton.setOnClickListener {

            Log.d("TAG", "initViews: ${binding.editTextNumber.text.toString().replace("\\s".toRegex(), "")}")

            findNavController().navigate(R.id.action_signInFragment_to_verificationFragment)
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