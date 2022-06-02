package com.footzone.footzone.ui.fragments.signup

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentSignUpBinding
import com.footzone.footzone.model.User
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues.USER_DETAIL
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment(R.layout.fragment_sign_up) {

    lateinit var binding: FragmentSignUpBinding
    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun sendRequest() {
        viewModel.signUp(
            "+998${
                binding.editTextNumber.text.toString().replace("\\s".toRegex(), "")
            }"
        )
        setupObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)

        initViews()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.userPhoneNumber.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        val fullname =
                            "${binding.editTextSurname.text.toString()} ${binding.editTextName.text.toString()}"
                        var number = "+998${binding.editTextNumber.text.toString()}"
                        number = number.replace("\\s".toRegex(), "")
                        val phoneNumber = number
                        val isStadiumHolder =
                            binding.filledExposedDropdown.text.toString() == "Maydon egasi"
                        val user = User(
                            "Android xiamsf",
                            "uaeghjhevujaguyf68aev7yua",
                            "mobile",
                            fullname,
                            "UZ",
                            phoneNumber,
                            null,
                            isStadiumHolder
                        )

                        findNavController().navigate(
                            R.id.action_signUpFragment_to_verificationFragment,
                            bundleOf(USER_DETAIL to user)
                        )
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupUI: ${it.message}")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun initViews() {
        roleSpinner()
        registerButtonControl()
        binding.textViewSignIn.setOnClickListener {
            openSignInFragment()
        }
        checkAllFields()
        binding.registerButton.setOnClickListener {
            if (checkData()) {
                openVerificationFragment()
            }
        }

        binding.registerButton.setOnClickListener {
            //don't open
            sendRequest()
        }
    }

    private fun openVerificationFragment() {
        findNavController().navigate(R.id.verificationFragment)
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
                registerButton.setBackgroundResource(R.drawable.button_register_filled_rounded_corner2)
                registerButton.isClickable = true
                registerButton.setTextColor(Color.WHITE)
            }
        } else {
            binding.apply {
                registerButton.setBackgroundResource(R.drawable.button_register_filled_rounded_corner1)
                registerButton.isClickable = false
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
        findNavController().navigate(R.id.signInFragment)
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