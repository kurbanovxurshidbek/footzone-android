package com.footzone.footzone.ui.fragments.signin

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.footzone.footzone.utils.UiStateObject
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SignInFragment : BaseFragment(R.layout.fragment_sign_in) {
    private var phoneNumber: String? = null
    lateinit var binding: FragmentSignInBinding
    private val viewModel by viewModels<SignInViewModel>()
    lateinit var auth: FirebaseAuth
    var storedVerificationId: String = ""
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    var code: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignInBinding.bind(view)

        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("uz")

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
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendLogInRequest() {
        if (binding.editTextNumber.text!!.isEmpty() && binding.editTextNumber.text.toString().length != 12) {
            toast(getString(R.string.str_incorrect_phonenumber))
        } else {
            phoneNumber = phoneNumber()

            viewModel.signIn(encrypt(phoneNumber!!)!!)
        }
        setupObservers()
    }

    private fun phoneNumber(): String =
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
                            toastLong(decrypt(it.data.data)!!)
                            code = decrypt(it.data.data)!!
                            sendVerificationCode(phoneNumber())
                        }
                        is UiStateObject.ERROR -> {
                            hideProgress()
                            toastLong(
                                getString(R.string.str_not_reg_with_number)
                            )
                            openSignUpFragment()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun openVerificationFragment(
        storedVerificationId: String,
        resendToken: PhoneAuthProvider.ForceResendingToken,
        code: String,
        phoneNumber: String
    ) {
        findNavController().navigate(
            R.id.action_signInFragment_to_verificationFragment,
            bundleOf(
                PHONE_NUMBER to this.phoneNumber,
                "STORED_VERIFICATION_ID" to storedVerificationId,
                "RESEND_TOKEN" to resendToken,
                "CODE" to code
            )
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

    //this functions provide to send sms for auth
    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        private val TAG = "@@@"
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded

            }
            hideProgress()
            Toast.makeText(
                requireContext(),
                "Birozdan so'ng harakat qilib ko'ring!",
                Toast.LENGTH_SHORT
            ).show()
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
            openVerificationFragment(storedVerificationId, resendToken, code, phoneNumber())
            hideProgress()
        }
    }

}