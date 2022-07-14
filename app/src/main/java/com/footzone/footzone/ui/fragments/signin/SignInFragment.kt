package com.footzone.footzone.ui.fragments.signin

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.footzone.footzone.utils.KeyValues.CODE
import com.footzone.footzone.utils.KeyValues.PHONE_NUMBER
import com.footzone.footzone.utils.KeyValues.STORED_VERIFICATION_ID
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
    lateinit var auth: FirebaseAuth
    var storedVerificationId: String = ""
    lateinit var code: String
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private val viewModel by viewModels<SignInViewModel>()

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
                           // toastLong(it.data.data)
                            toastLong(decrypt(it.data.data)!!)
                            openVerificationFragment(it.data.data)
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

    private fun openVerificationFragment(code: String) {
        findNavController().navigate(
            R.id.action_signInFragment_to_verificationFragment,
            bundleOf(PHONE_NUMBER to phoneNumber, STORED_VERIFICATION_ID to storedVerificationId,"RESEND_TOKEN" to resendToken, CODE to code)
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
            sendVerificationCode(phoneNumber())
        } else {
            binding.apply {
                enterButton.setBackgroundResource(R.drawable.button_register_filled_rounded_corner1)
                enterButton.setTextColor(R.color.buttonDisabledTextColor)
            }
        }
    }
    private fun phoneNumber(): String =
        "+998${binding.editTextNumber.text.toString().replace("\\s".toRegex(), "")}"

    private fun checkData(): Boolean {
        val number = binding.editTextNumber.text.toString()
        return number.length == 12
    }

    private fun openSignUpFragment() {
        findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
    }
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
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
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

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }
}