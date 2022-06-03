package com.footzone.footzone.ui.fragments.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentProfileBinding
import com.footzone.footzone.model.profile.UserData
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import java.io.File
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.FileOutputStream

@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile) {
    private val PICK_FROM_FILE_ADD: Int = 1001
    private lateinit var binding: FragmentProfileBinding
    lateinit var sharedPref: SharedPref
    lateinit var image: File
    private val viewModel by viewModels<ProfileViewModel>()

    lateinit var fileUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view)

        viewModel.getUserData("b98f1843-b09d-48a6-93a9-b370a78689fb")
        setupObservers()
        initViews()
    }

    private fun initViews() {
        sharedPref = SharedPref(requireContext())

        val logIn = sharedPref.getLogIn("LogIn", false)
        Log.d("TAG", "initViews: $logIn")
        if (!logIn) {
            binding.linerProfile.visibility = View.GONE
            binding.linearProfileNoSignIn.visibility = View.VISIBLE
        } else {
            binding.linerProfile.visibility = View.VISIBLE
            binding.linearProfileNoSignIn.visibility = View.GONE
        }
        binding.ivAdd.setOnClickListener {
            getImageFromGallery()
        }

        binding.tvEnterAccount.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
        }

        binding.ivLogOut.setOnClickListener {
            showPopup(it)
        }


    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.userData.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }
                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "setupObservers: ${it.data}")
                        showUserData(it.data)
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupUI: ${it.message}")
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun showUserData(userData: UserData) {
        binding.tvName.text = userData.data.fullName
        binding.tvNumber.text = userData.data.phoneNumber
        Glide.with(requireActivity())
            .load("http://10.10.2.18:8081/images/user/${userData.data.photo.name}")
            .into(binding.ivProfile)
    }

    private fun showPopup(v: View) {
        PopupMenu(requireContext(), v).apply {
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {

                    R.id.logOut -> {
                        sharedPref.saveLogIn("LogIn", false)
                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), "log out", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            inflate(R.menu.action_bar_menu)
            show()
        }

        binding.tvEnterAccount.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
        }
    }

    private fun getImageFromGallery() {
        getImageFromGallery.launch("image/*")
    }

    private val getImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        val ins = requireActivity().contentResolver.openInputStream(uri)
        image = File.createTempFile("file", ".jpg", requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        val fileOutputStream = FileOutputStream(image)
        ins?.copyTo(fileOutputStream)
        ins?.close()
        fileOutputStream.close()
        if (image.length() == 0L) return@registerForActivityResult
        Glide.with(requireActivity()).load(image).into(binding.ivProfile)

        val reqFile: RequestBody = RequestBody.create("image/jpg".toMediaTypeOrNull(), image)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", image.name, reqFile)
        viewModel.updateUserProfilePhoto("b98f1843-b09d-48a6-93a9-b370a78689fb",body)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.userProfile.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }
                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "setupObservers: ${it.data}")

                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupUI: ${it.message}")
                    }
                    else -> {
                    }
                }
            }
        }
    }
}