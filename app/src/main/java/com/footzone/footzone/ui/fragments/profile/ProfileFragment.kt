package com.footzone.footzone.ui.fragments.profile

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentProfileBinding
import com.footzone.footzone.model.profile.Data
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues.LOG_IN
import com.footzone.footzone.utils.KeyValues.USER_ID
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import com.squareup.picasso.Picasso
import java.io.File
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var sharedPref: SharedPref

    lateinit var image: File
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

        if (sharedPref.getUserID(USER_ID, "").isNotEmpty())
            viewModel.getUserData(sharedPref.getUserID(USER_ID, ""))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view)

        initViews()
    }

    private fun initViews() {

        val logIn = sharedPref.getLogIn(LOG_IN, false)

        setupObservers()

        binding.apply {
            if (!logIn) {
                linerProfile.visibility = View.GONE
                linearProfileNoSignIn.visibility = View.VISIBLE
            } else {
                linerProfile.visibility = View.VISIBLE
                linearProfileNoSignIn.visibility = View.GONE
            }
            ivAdd.setOnClickListener {
                getImageFromGallery()
            }

            tvEnterAccount.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
            }

            ivLogOut.setOnClickListener {
                showPopup(it)
            }

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
                        showUserData(it.data.data)
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

    private fun showUserData(userData: Data) {
        binding.apply {
            tvName.text = userData.fullName
            tvNumber.text = userData.phoneNumber

            Picasso.get()
                .load("https://footzone-server.herokuapp.com/images/user/${userData.photo.name}")
                .into(ivProfile)
        }
    }

    private fun showPopup(v: View) {
        PopupMenu(requireContext(), v).apply {
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {

                    R.id.logOut -> {
                        sharedPref.saveLogIn(LOG_IN, false)
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

    private val getImageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri ?: return@registerForActivityResult
            val ins = requireActivity().contentResolver.openInputStream(uri)
            image = File.createTempFile(
                "file",
                ".jpg",
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
            val fileOutputStream = FileOutputStream(image)
            ins?.copyTo(fileOutputStream)
            ins?.close()
            fileOutputStream.close()
            if (image.length() == 0L) return@registerForActivityResult
            Glide.with(requireActivity()).load(image).into(binding.ivProfile)

            val reqFile: RequestBody = RequestBody.create("image/jpg".toMediaTypeOrNull(), image)
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("file", image.name, reqFile)

            sendRequestToLoadImage(body)
        }

    private fun sendRequestToLoadImage(body: MultipartBody.Part) {
        viewModel.updateUserProfilePhoto(sharedPref.getUserID(USER_ID, ""), body)
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
