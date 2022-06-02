package com.footzone.footzone.ui.fragments.signin

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentSignInBinding
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues.PHONE_NUMBER
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : BaseFragment(R.layout.fragment_sign_in) {
    private var phoneNumber: String? = null
    lateinit var binding: FragmentSignInBinding
    private val viewModel by viewModels<SignInViewModel>()

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

        binding.enterButton.setOnClickListener {

            phoneNumber = "+998${binding.editTextNumber.text.toString().replace("\\s".toRegex(), "")}"

            viewModel.signIn(phoneNumber!!)

            setupObservers()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.userPhoneNumber.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        openVerificationFragment()
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupUI: ${it.message}")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun openVerificationFragment(){
        findNavController().navigate(R.id.action_signInFragment_to_verificationFragment,
            bundleOf(PHONE_NUMBER to phoneNumber)
        )
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