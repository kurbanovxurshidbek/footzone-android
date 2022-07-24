package com.footzone.footzone.ui.fragments.verification

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VerificationFragment : BaseFragment(R.layout.fragment_verification) {

    @Inject
    lateinit var sharedPref: SharedPref
    lateinit var binding: FragmentVerificationBinding
    private val viewModel by viewModels<VerificationViewModel>()
    private var user: User? = null
    private var phoneNumber: String? = null
    private var isToSignUp = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentVerificationBinding.bind(view)
        if (arguments?.containsKey(USER_DETAIL)!!) {
            user = arguments?.get(USER_DETAIL) as User
            isToSignUp = true
        }
        if (arguments?.containsKey(PHONE_NUMBER)!!) {
            phoneNumber = arguments?.get(PHONE_NUMBER).toString()
            isToSignUp = false
        }

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
                checkAndSingInUp()
            }

            tvResendCode.setOnClickListener {
                //code resent
            }

            editTextVerificationCode.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkAndSingInUp()
                    true
                } else false
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
        viewModel.signIn(
            SignInVerification(
                encrypt(binding.editTextVerificationCode.text.toString())!!,
                getDeviceName()!!,
                sharedPref.getFirebaseToken(KeyValues.FIREBASE_TOKEN),
                "Mobile",
                encrypt(phoneNumber!!)!!
            )
        )
        setupObserversSignIn()
    }
    private fun sendRequestToSignUp() {
        viewModel.signUp(
            SmsVerification(
                encrypt(binding.editTextVerificationCode.text.toString())!!,
                encrypt(user?.phoneNumber.toString())!!
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
                            if (it.data.success) {
                                hideProgress()
                                val data = it.data.data
                                saveToSharedPref(data.user_id, data.token, data.stadiumHolder)
                                //returnHomeFragment()
                            }
                        }
                        is UiStateObject.ERROR -> {
                            hideProgress()
                            toastLong("Kod xato kiritildi.")
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
                        toastLong("Kod xato kiritildi.")
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
                            Log.d("TAG", "setupObserversRegister: ${it.data.data}")
                            val userPriority = it.data.data
                            saveToSharedPref(
                                userPriority.user_id,
                                userPriority.token,
                                user!!.stadiumHolder
                            )
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
}