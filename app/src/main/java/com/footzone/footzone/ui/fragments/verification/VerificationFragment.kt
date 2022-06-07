package com.footzone.footzone.ui.fragments.verification

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentVerificationBinding
import com.footzone.footzone.model.SignInVerification
import com.footzone.footzone.model.SmsVerification
import com.footzone.footzone.model.User
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues.PHONE_NUMBER
import com.footzone.footzone.utils.KeyValues.USER_DETAIL
import com.footzone.footzone.utils.KeyValues.USER_ID
import com.footzone.footzone.utils.KeyValues.USER_TOKEN
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerificationFragment : BaseFragment(R.layout.fragment_verification) {

    lateinit var sharedPref: SharedPref
    lateinit var binding: FragmentVerificationBinding
    private val viewModel by viewModels<VerificationViewModel>()
    private var user: User? = null
    private var phoneNumber: String? = null
    private var isToSignUp = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentVerificationBinding.bind(view)
        if (arguments?.containsKey(USER_DETAIL)!!) {
            user = arguments?.get(USER_DETAIL) as User
            isToSignUp = true
            Log.d("TAG", "onViewCreated: $user")
        }
        if (arguments?.containsKey(PHONE_NUMBER)!!) {
            phoneNumber = arguments?.get(PHONE_NUMBER).toString()
            Log.d("TAG", "onViewCreatedd: $phoneNumber")
            isToSignUp = false
        }

        initViews()
    }

    private fun initViews() {
        sharedPref = SharedPref(requireContext())
        verificationCodeErrorControl()
        binding.backButton.setOnClickListener {
            closeSignInFragment()
        }
        binding.confirmationButton.setOnClickListener {
            if (isToSignUp) {
                sendRequestToSignUp()
                setupObserversSignUp()
            } else {
                sendRequestToSignIn()
                setupObserversSignIn()
            }
        }
    }

    private fun sendRequestToSignIn() {
        viewModel.signIn(
            SignInVerification(
                binding.editTextVerificationCode.text.toString().toInt(),
                "Android",
                "jwsbcbwcvuvc",
                "Mobile",
                phoneNumber!!
            )
        )
    }

    private fun sendRequestToSignUp() {
        viewModel.signUp(
            SmsVerification(
                binding.editTextVerificationCode.text.toString().toInt(),
                user?.phoneNumber.toString()
            )
        )
    }

    private fun setupObserversSignIn() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.signInVerification.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        if (it.data.success) {
                            Log.d("TAG", "setupObserversSignIn: ${it.data}")
                            returnHomeFragment()
                        }
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupUI: ${it.message}")
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun setupObserversSignUp() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.smsVerification.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        if (it.data.success) {
                            Log.d("TAG", "setupObserversSignUp: ${it.data.success}")
                            user!!.smsCode = binding.editTextVerificationCode.text.toString()
                            viewModel.registerUser(user!!)
                            setupObserversRegister()
                        }
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupUI: ${it.message}")
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun saveToSharedPref(userID: String, userToken: String) {
        sharedPref.saveLogIn("LogIn", true)
        sharedPref.saveUserId(USER_ID, userID)
        sharedPref.saveUserToken(USER_TOKEN, userToken)

    }

    private fun setupObserversRegister() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.registerUser.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        saveToSharedPref("","")
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupUI: ${it.message} error")
                    }
                    else -> {
                    }
                }
            }
        }
    }


    private fun returnHomeFragment() {
        findNavController().navigate(R.id.action_verificationFragment_to_homeFragment)
    }

    private fun verificationCodeErrorControl() {
        binding.editTextVerificationCode.doAfterTextChanged {
            if (it!!.toString().length != 6) {
                binding.textInputLayoutVerificationCode.error = "Tekshirib ko'ring"
                registerButtonControl()
            } else {
                binding.textInputLayoutVerificationCode.error = null
                registerButtonControl()
            }
        }
    }

    private fun closeSignInFragment() {
        findNavController().popBackStack()
    }

    @SuppressLint("ResourceAsColor")
    private fun registerButtonControl() {
        if (checkData()) {
            binding.apply {
                confirmationButton.setBackgroundResource(R.drawable.button_register_filled_rounded_corner2)
                confirmationButton.isClickable = true
                confirmationButton.setTextColor(Color.WHITE)
            }
        } else {
            binding.apply {
                confirmationButton.setBackgroundResource(R.drawable.button_register_filled_rounded_corner1)
                confirmationButton.isClickable = false
                confirmationButton.setTextColor(R.color.buttonDisabledTextColor)

            }
        }
    }

    private fun checkData(): Boolean {
        return binding.editTextVerificationCode.text!!.toString().length == 6
    }
}