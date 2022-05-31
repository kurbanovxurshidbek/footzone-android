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
                number!!.isNotEmpty()
    }

    private fun openSignInFragment() {
        findNavController().navigate(R.id.signInFragment)
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