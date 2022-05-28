package com.footzone.footzone.ui.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentSignInBinding
import com.footzone.footzone.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    lateinit var binding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)

        initViews()
    }

    private fun initViews() {
        roleSpinner()
        passwordErrorControl()
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
        binding.editTextPassword.doAfterTextChanged {
            registerButtonControl()
        }
        binding.editTextConfirmPassword.doAfterTextChanged {
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
        val password = binding.editTextPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()

        return role.isNotEmpty() && name!!.isNotEmpty() && surname!!.isNotEmpty() &&
                number!!.isNotEmpty() && password!!.isNotEmpty() && confirmPassword == password
    }

    private fun openSignInFragment() {
        findNavController().navigate(R.id.signInFragment)
    }




    private fun passwordErrorControl() {
        binding.editTextPassword.doAfterTextChanged {
            if (it!!.toString().length<6){
                binding.textInputLayoutPassword.error = "Parol 6 ta belgidan iborat bo'lishi kerak"
            } else {
                binding.textInputLayoutPassword.error = null
            }
        }

        binding.editTextConfirmPassword.doAfterTextChanged {
            if (it.toString() != binding.editTextPassword.text.toString()) {
                binding.textInputLayoutConfirmPassword.error = "Parolingiz bir xil emas. Tekshirib qayta kiriting"
            } else {
                binding.textInputLayoutConfirmPassword.error = null
            }
        }
    }

    //this function for get role exp: Stadium owner or User
    private fun roleSpinner() {
        val type: Array<String> = arrayOf("Oddiy foydalnuvchi", "Maydon egasi")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, type)
        val editTextFilledExposedDropdown = binding.filledExposedDropdown

        editTextFilledExposedDropdown.setAdapter(adapter)
    }
}