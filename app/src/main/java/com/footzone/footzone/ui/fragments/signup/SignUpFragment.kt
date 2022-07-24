package com.footzone.footzone.ui.fragments.signup

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
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
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.FIREBASE_TOKEN
import com.footzone.footzone.utils.KeyValues.USER_DETAIL
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SignUpFragment : BaseFragment(R.layout.fragment_sign_up) {

    lateinit var binding: FragmentSignUpBinding
    private val viewModel by viewModels<SignUpViewModel>()

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)

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
                            hideProgress()
                            // toastLong(it.data.data)
                            toastLong(decrypt(it.data.data)!!)

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
                            Log.d("TAG", "setupObservers: ${it.message}")
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
            bundleOf(USER_DETAIL to user)
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
}