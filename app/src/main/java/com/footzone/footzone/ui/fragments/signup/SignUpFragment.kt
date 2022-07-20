package com.footzone.footzone.ui.fragments.signup

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
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
import com.footzone.footzone.databinding.FragmentSignUpBinding
import com.footzone.footzone.model.User
import com.footzone.footzone.security.Symmetric.decrypt
import com.footzone.footzone.security.Symmetric.encrypt
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues.FIREBASE_TOKEN
import com.footzone.footzone.utils.KeyValues.STADIUM_OWNER
import com.footzone.footzone.utils.KeyValues.USER
import com.footzone.footzone.utils.KeyValues.USER_DETAIL
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
class SignUpFragment : BaseFragment(R.layout.fragment_sign_up) {

    lateinit var binding: FragmentSignUpBinding
    lateinit var auth: FirebaseAuth
    var storedVerificationId: String = ""
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    var code: String = ""
    var user: User = User(null, null, null, null, null, null, null, false)

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendRequestToSendSms() {
        viewModel.signUp(encrypt(phoneNumber())!!)
        setupObservers()
    }

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
                            val fullname = fullName()
                            val phoneNumber = phoneNumber()
                            val isStadiumHolder = isStadiumHolder()

                            user = User(
                                getDeviceName(),
                                sharedPref.getFirebaseToken(FIREBASE_TOKEN),
                                "Mobile",
                                fullname,
                                "UZ",
                                phoneNumber,
                                null,
                                isStadiumHolder
                            )
                            sendVerificationCode(phoneNumber())
                            //     openVerificationFragment(user)
                        }
                        is UiStateObject.ERROR -> {
                            hideProgress()
                            toastLong(getString(R.string.str_already_reg_log_in))
                            findNavController().popBackStack()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun isStadiumHolder(): Boolean =
        binding.filledExposedDropdown.text.toString() == STADIUM_OWNER

    private fun phoneNumber(): String =
        "+998${binding.editTextNumber.text.toString().replace("\\s".toRegex(), "")}"

    private fun fullName(): String = "${
        binding.editTextSurname.text.toString().trim()
    } ${binding.editTextName.text.toString().trim()}"

    private fun openVerificationFragment(
        user: User,
        storedVerificationId: String,
        resendToken: PhoneAuthProvider.ForceResendingToken,
        code: String,
        phoneNumber: String
    ) {
        Log.d("@@@", "phoneNumber: $phoneNumber")
        findNavController().navigate(
            R.id.action_signUpFragment_to_verificationFragment,
            bundleOf(
                USER_DETAIL to user,
                "STORED_VERIFICATION_ID" to storedVerificationId,
                "RESEND_TOKEN" to resendToken,
                "CODE" to code,
                "PHONE_NUMBER" to phoneNumber
            )
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
            Log.d("@@@", "number: ${phoneNumber()}")
        } else {
            toast(getString(R.string.str_not_complete_data))
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
        val type: Array<String> = arrayOf(USER, STADIUM_OWNER)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, type)
        val editTextFilledExposedDropdown = binding.filledExposedDropdown

        editTextFilledExposedDropdown.setAdapter(adapter)
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
            openVerificationFragment(user, storedVerificationId, resendToken, code, phoneNumber())
            hideProgress()
        }
    }

}