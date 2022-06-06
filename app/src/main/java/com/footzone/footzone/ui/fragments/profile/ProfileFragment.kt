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
import java.io.FileOutputStream

@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile) {
    private val PICK_FROM_FILE_ADD: Int = 1001
    private lateinit var binding: FragmentProfileBinding
    lateinit var sharedPref: SharedPref
    private val viewModel by viewModels<ProfileViewModel>()

    lateinit var fileUri: Uri
    lateinit var file: File

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
            openLocalStorage()
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
//        Glide.with(requireActivity())
//            .load("http://10.10.2.18:8081/images/${userData.data.photo.name}")
//            .into(binding.ivProfile)
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

    private fun openLocalStorage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) intent.action = Intent.ACTION_GET_CONTENT
        val chooserIntent = Intent.createChooser(intent, "Complete action using")

        val capturedImage = System.currentTimeMillis().toString() + ".jpg"
        val file = File(Environment.getExternalStorageDirectory(), capturedImage)
        //val outputFileUri = Uri.fromFile(file)

        startActivityForResult(chooserIntent, PICK_FROM_FILE_ADD)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FROM_FILE_ADD && resultCode == RESULT_OK) {
            try {
                val ins = requireActivity().contentResolver.openInputStream(fileUri)
                file = File.createTempFile(
                    "file",
                    ".jpg",
                    requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                )

                val fileOutputStream = FileOutputStream(file)

                ins?.copyTo(fileOutputStream)
                ins?.close()
                fileOutputStream.close()

                if (file.length() != 0L) {
                    Glide.with(requireContext())
                        .load(file)
                        .into(binding.ivProfile)

                    loadPhoto()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadPhoto() {

    }
}