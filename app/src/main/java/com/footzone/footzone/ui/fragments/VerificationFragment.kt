package com.footzone.footzone.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentVerificationBinding
import com.footzone.footzone.utils.SharedPref

class VerificationFragment : Fragment() {
    lateinit var sharedPref: SharedPref
    lateinit var binding: FragmentVerificationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verification, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentVerificationBinding.bind(view)

        initViews()
    }

    private fun initViews() {
        sharedPref= SharedPref(requireContext())
        verificationCodeErrorControl()
        binding.backButton.setOnClickListener {
            closeSignInFragment()
        }
        binding.confirmationButton.setOnClickListener {
            sharedPref.saveLogIn("LogIn",true)
            returnHomeFragment()


        }
    }

    private fun returnHomeFragment() {
        findNavController().navigate(R.id.action_verificationFragment_to_homeFragment)

    }

    private fun verificationCodeErrorControl() {
        binding.editTextVerificationCode.doAfterTextChanged {
            if (it!!.toString().length != 4){
                binding.textInputLayoutVerificationCode.error = "Tekshirib ko'ring"
                registerButtonControl()
            } else {
                binding.textInputLayoutVerificationCode.error = null
                registerButtonControl()
            }
        }
    }

    private fun closeSignInFragment() {
        findNavController().popBackStack()
    }
    @SuppressLint("ResourceAsColor")
    private fun registerButtonControl() {
        if (checkData()) {
            binding.apply {
                confirmationButton.setBackgroundResource(R.drawable.button_register_filled_rounded_corner2)
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

        return binding.editTextVerificationCode.text!!.toString().length == 4
    }
}