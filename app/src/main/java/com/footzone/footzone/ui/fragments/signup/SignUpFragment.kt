package com.footzone.footzone.ui.fragments.signup

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import java.util.concurrent.TimeUnit
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentSignUpBinding
import com.footzone.footzone.model.User
import com.footzone.footzone.security.Symmetric.decrypt
import com.footzone.footzone.security.Symmetric.encrypt
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.FIREBASE_TOKEN
import com.footzone.footzone.utils.KeyValues.USER_DETAIL
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SignUpFragment : BaseFragment(R.layout.fragment_sign_up) {

    lateinit var binding: FragmentSignUpBinding
    lateinit var auth: FirebaseAuth
    var storedVerificationId: String = ""
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private val viewModel by viewModels<SignUpViewModel>()

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)

        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("uz")

        initViews()
    }

    private fun sendRequestToSendSms() {
        viewModel.signUp(encrypt(phoneNumber())!!)
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userPhoneNumber.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            Log.d("@@@", "setupObservers: Loading")
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            // toastLong(it.data.data)
                            toastLong(decrypt(it.data.data)!!)
                            Log.d("@@@", "setupObservers: Succes")
                            val fullname = fullName()
                            val phoneNumber = phoneNumber()
                            val isStadiumHolder = isStadiumHolder()

                            val user = User(
                                getDeviceName(),
                                sharedPref.getFirebaseToken(FIREBASE_TOKEN),
                                "Mobile",
                                fullname,
                                "UZ",
                                phoneNumber,
                                null,
                                isStadiumHolder
                            )

                            openVerificationFragment(user)
                        }
                        is UiStateObject.ERROR -> {
                            Log.d("@@@", "setupObservers: ${it.message}")
                            hideProgress()
                            toastLong("Siz avval ro'yxatdan o'tgansiz.\nIltimos kirish uchun raqamingizni kiriting.")
                            findNavController().popBackStack()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun isStadiumHolder(): Boolean =
        binding.filledExposedDropdown.text.toString() == "Maydon egasi"

    private fun phoneNumber(): String =
        "+998${binding.editTextNumber.text.toString().replace("\\s".toRegex(), "")}"

    private fun fullName(): String = "${
        binding.editTextSurname.text.toString().trim()
    } ${binding.editTextName.text.toString().trim()}"

    private fun openVerificationFragment(user: User) {
        findNavController().navigate(
            R.id.action_signUpFragment_to_verificationFragment,
            bundleOf(USER_DETAIL to user, KeyValues.STORED_VERIFICATION_ID to storedVerificationId,"RESEND_TOKEN" to resendToken)
        )
    }

    private fun initViews() {

        roleSpinner()
        registerButtonControl()

        binding.apply {
            textViewSignIn.setOnClickListener {
                openSignInFragment()
            }

            checkAllFields()

            registerButton.setOnClickListener {
                sendCodeIfAllFieldsFilled()
            }

            ivBack.setOnClickListener {
                back()
            }

            editTextNumber.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendCodeIfAllFieldsFilled()
                    true
                } else false
            }
        }
    }

    private fun sendCodeIfAllFieldsFilled() {
        if (checkData()) {
            sendRequestToSendSms()
            sendVerificationCode(phoneNumber())
        } else {
            toast("Ma'lumotlar to'liq kiritilmadi!")
        }
    }

    private fun checkAllFields() {
        binding.editTextName.doAfterTextChanged {
            registerButtonControl()
        }
        binding.editTextSurname.doAfterTextChanged {
            registerButtonControl()
        }
        binding.editTextNumber.doAfterTextChanged {
            registerButtonControl()
        }

        binding.filledExposedDropdown.doAfterTextChanged {
            registerButtonControl()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun registerButtonControl() {
        if (checkData()) {
            binding.apply {
                registerButton.setBackgroundResource(R.drawable.linear_rounded_background)
                registerButton.setTextColor(Color.WHITE)
            }
        } else {
            binding.apply {
                registerButton.setBackgroundResource(R.drawable.button_register_filled_rounded_corner1)
                registerButton.setTextColor(R.color.buttonDisabledTextColor)

            }
        }
    }

    private fun checkData(): Boolean {
        val role = binding.filledExposedDropdown.text.toString()
        val name = binding.editTextName.text.toString()
        val surname = binding.editTextSurname.text.toString()
        val number = binding.editTextNumber.text.toString()


        return role.isNotEmpty() && name.isNotEmpty() && surname.isNotEmpty() &&
                number.length == 12
    }

    private fun openSignInFragment() {
        requireActivity().onBackPressed()
    }

    //this function for get role exp: Stadium owner or User
    private fun roleSpinner() {
        val type: Array<String> = arrayOf("Oddiy foydalanuvchi", "Maydon egasi")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, type)
        val editTextFilledExposedDropdown = binding.filledExposedDropdown

        editTextFilledExposedDropdown.setAdapter(adapter)
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
            signInWithPhoneAuthCredential(credential)
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
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("@@@", "signInWithCredential:success")

                    val user = task.result?.user?.phoneNumber
                    Log.d("@@@", "signInWithCredential:success $user")
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("@@@", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
}