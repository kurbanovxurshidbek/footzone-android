package com.footzone.footzone.ui.fragments.signin

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentSignInBinding
import com.footzone.footzone.security.Symmetric.decrypt
import com.footzone.footzone.security.Symmetric.encrypt
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues.PHONE_NUMBER
import com.footzone.footzone.utils.KeyValues.SMS_CODE
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : BaseFragment(R.layout.fragment_sign_in) {
    private var phoneNumber: String? = null
    lateinit var binding: FragmentSignInBinding
    private val viewModel by viewModels<SignInViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignInBinding.bind(view)

        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {

        binding.apply {

            textViewSignUp.setOnClickListener {
                openSignUpFragment()
            }

            ivBack.setOnClickListener {
                back()
            }

            enterButton.setOnClickListener {
                sendLogInRequest()
            }

            checkAllFields()

            editTextNumber.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendLogInRequest()
                    true
                } else false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendLogInRequest() {
        if (binding.editTextNumber.text!!.isEmpty() && binding.editTextNumber.text.toString().length != 12) {
            toast("Raqam noto'g'ri kiritildi!")
        } else {
            phoneNumber = getPhoneNumber()

            viewModel.signIn(encrypt(phoneNumber!!)!!)
        }
        setupObservers()
    }

    private fun getPhoneNumber(): String =
        "+998${binding.editTextNumber.text.toString().replace("\\s".toRegex(), "")}"

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userPhoneNumber.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            val smsCode = decrypt(it.data.data)!!
                            toastLong(smsCode)
                            openVerificationFragment(smsCode)
                        }
                        is UiStateObject.ERROR -> {
                            hideProgress()
                            toastLong(
                                "Bu raqam orqali ro'yxatdan o'tilmagan. Iltimos ro'yxatdan o'ting."
                            )
                            openSignUpFragment()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun openVerificationFragment(smsCode: String) {
        findNavController().navigate(
            R.id.action_signInFragment_to_verificationFragment,
            bundleOf(PHONE_NUMBER to phoneNumber, SMS_CODE to smsCode)
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
                enterButton.setBackgroundResource(R.drawable.linear_rounded_background)
                enterButton.setTextColor(Color.WHITE)
            }
        } else {
            binding.apply {
                enterButton.setBackgroundResource(R.drawable.button_register_filled_rounded_corner1)
                enterButton.setTextColor(R.color.buttonDisabledTextColor)
            }
        }
    }

    private fun checkData(): Boolean {
        val number = binding.editTextNumber.text.toString()
        return number.length == 12
    }

    private fun openSignUpFragment() {
        findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
    }
}