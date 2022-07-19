package com.footzone.footzone.ui.fragments.verification

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentVerificationBinding
import com.footzone.footzone.model.SignInVerification
import com.footzone.footzone.model.SmsVerification
import com.footzone.footzone.model.User
import com.footzone.footzone.security.Symmetric.decrypt
import com.footzone.footzone.security.Symmetric.encrypt
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.IS_OWNER
import com.footzone.footzone.utils.KeyValues.LOG_IN
import com.footzone.footzone.utils.KeyValues.PHONE_NUMBER
import com.footzone.footzone.utils.KeyValues.USER_DETAIL
import com.footzone.footzone.utils.KeyValues.USER_ID
import com.footzone.footzone.utils.KeyValues.USER_TOKEN
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class VerificationFragment : BaseFragment(R.layout.fragment_verification) {

    @Inject
    lateinit var sharedPref: SharedPref
    lateinit var binding: FragmentVerificationBinding
    private val viewModel by viewModels<VerificationViewModel>()
    private var user: User? = null
    private var phoneNumber: String? = null
    lateinit var auth: FirebaseAuth
    private var isToSignUp = false
    lateinit var codeForBackend: String
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentVerificationBinding.bind(view)

        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("uz")

        if (arguments?.containsKey(USER_DETAIL)!!) {
            user = arguments?.get(USER_DETAIL) as User
            isToSignUp = true
        } else {
            isToSignUp = false
        }

        phoneNumber = arguments?.get("PHONE_NUMBER").toString()
        codeForBackend = arguments?.get("CODE").toString()
        storedVerificationId = arguments?.get("STORED_VERIFICATION_ID").toString()
        resendToken = arguments?.get("RESEND_TOKEN") as PhoneAuthProvider.ForceResendingToken

        Log.d("@@@", "onViewCreated: code: $codeForBackend storedVerificationId: $storedVerificationId resendToken: $resendToken" )

        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        verificationCodeErrorControl()
        binding.apply {
            ivBack.setOnClickListener {
                back()
            }

            confirmationButton.setOnClickListener {
                // checkAndSingInUp()
                showProgress()
                verifyCode()
            }

            tvResendCode.setOnClickListener {
                //code resent
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAndSingInUp() {
        if (isToSignUp) {
            sendRequestToSignUp()
        } else {
            sendRequestToSignIn()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendRequestToSignIn() {
        Log.d("@@@", "codeForBackend $codeForBackend")
        Log.d("@@@", "getFirebaseToken ${sharedPref.getFirebaseToken(KeyValues.FIREBASE_TOKEN)}")
        viewModel.signIn(
            SignInVerification(
                encrypt(codeForBackend)!!,
                getDeviceName()!!,
                sharedPref.getFirebaseToken(KeyValues.FIREBASE_TOKEN),
                "Mobile",
                encrypt(phoneNumber.toString())!!
            )
        )
        Log.d("@@@", "sendRequestToSignIn: ")
        setupObserversSignIn()
    }

    private fun sendRequestToSignUp() {
        viewModel.signUp(
            SmsVerification(
                encrypt(codeForBackend)!!,
                encrypt(phoneNumber.toString())!!
            )
        )
        setupObserversSignUp()
    }

    private fun setupObserversSignIn() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInVerification.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            Log.d("@@@", "setupObserversSignIn: success")
                            if (it.data.success) {
                                hideProgress()
                                Log.d("@@@", "keldi")
                                val data = it.data.data
                                saveToSharedPref(data.user_id, data.token, data.stadiumHolder)
                            }
                        }
                        is UiStateObject.ERROR -> {
                            Log.d("@@@", "setupObserversSignIn: error")
                            hideProgress()
                            toastLong(getString(R.string.str_incorrect_code))
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObserversSignUp() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.smsVerification.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        showProgress()
                    }

                    is UiStateObject.SUCCESS -> {
                        if (it.data.success) {
                            hideProgress()
                            user!!.smsCode =
                                encrypt(binding.editTextVerificationCode.text.toString())
                            user!!.phoneNumber = encrypt(user!!.phoneNumber!!)
                            viewModel.registerUser(user!!)
                            setupObserversRegister()
                        }
                    }
                    is UiStateObject.ERROR -> {
                        hideProgress()
                        toastLong(getString(R.string.str_incorrect_code))
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun saveToSharedPref(userID: String, userToken: String, stadiumHolder: Boolean?) {
        sharedPref.saveLogIn(LOG_IN, true)
        sharedPref.saveUserId(USER_ID, userID)
        sharedPref.saveUserToken(USER_TOKEN, userToken)
        sharedPref.saveIsOwner(IS_OWNER, stadiumHolder!!)
        returnHomeFragment()
    }

    private fun setupObserversRegister() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerUser.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            //show progress
                        }

                        is UiStateObject.SUCCESS -> {
                            val userPriority = it.data.data
                            saveToSharedPref(
                                userPriority.user_id,
                                userPriority.token,
                                user!!.stadiumHolder
                            )
                        }
                        is UiStateObject.ERROR -> {
                        }
                        else -> {
                        }
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
                binding.textInputLayoutVerificationCode.error = getString(R.string.str_please_check)
                registerButtonControl()
            } else {
                binding.textInputLayoutVerificationCode.error = null
                registerButtonControl()
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun registerButtonControl() {
        if (checkData()) {
            binding.apply {
                confirmationButton.setBackgroundResource(R.drawable.linear_rounded_background)
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

    fun verifyCode(){
        val code = binding.editTextVerificationCode.text.toString()
        Log.d("@@@", "verifyCode: $code")
        if (code.length == 6){
            val credential = PhoneAuthProvider.getCredential(storedVerificationId,code)
            signInWithPhoneAuthCredential(credential)
        }
    }
    fun resendCode(phoneNumber: String){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
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
            toastLong("Birozdan so'ng harakat qilib ko'ring")
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent:$verificationId")

            storedVerificationId = verificationId
            resendToken = token
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("@@@", "signInWithCredential:success")

                    val user = task.result?.user?.phoneNumber
                    Log.d("@@@", "signInWithCredential:success $user")
                    hideProgress()
                    checkAndSingInUp()
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("@@@", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    hideProgress()
                    toastLong(getString(R.string.str_incorrect_code))
                    // Update UI
                }
            }
    }

}