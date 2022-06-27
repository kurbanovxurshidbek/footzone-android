package com.footzone.footzone.ui.fragments.profile

import android.app.Dialog
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.footzone.footzone.utils.ChooseLanguageDialog
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentProfileBinding
import com.footzone.footzone.model.EditNameRequest
import com.footzone.footzone.model.profile.Data
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.IS_OWNER
import com.footzone.footzone.utils.KeyValues.LANGUAGE
import com.footzone.footzone.utils.KeyValues.LOG_IN
import com.footzone.footzone.utils.KeyValues.USER_ID
import com.footzone.footzone.utils.KeyValues.USER_TOKEN
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import com.footzone.footzone.utils.commonfunction.Functions.loadImageUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import me.shouheng.compress.Compress
import me.shouheng.compress.concrete
import me.shouheng.compress.strategy.config.ScaleMode
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var sharedPref: SharedPref
    lateinit var image: File
    lateinit var userData: Data
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
            ivEditName.setOnClickListener {
                changeUserNameDialog()
            }

        }

        binding.linearLanguage.setOnClickListener {
            val dialog = ChooseLanguageDialog { lang ->
                val sharedPref = SharedPref(requireContext())
                sharedPref.saveLanguage(LANGUAGE, lang)
                setLocale(lang)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dialog.showChooseLanguageDialog(requireActivity())
            }
        }
    }

    private fun changeUserNameDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.layout_edit_name_dialog)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.dialog_background)

        val name = dialog.findViewById<EditText>(R.id.etName)
        val tvYes = dialog.findViewById<TextView>(R.id.tvYes)
        val tvNo = dialog.findViewById<TextView>(R.id.tvNo)

        name.setText(binding.tvName.text)

        tvNo.setOnClickListener { dialog.dismiss() }
        tvYes.setOnClickListener {
            if (name.text.isNotEmpty()) {
                val body = EditNameRequest(name.text.toString(), userData.phoneNumber)
                viewModel.editUser(sharedPref.getUserID(USER_ID, ""), body)
                setupEditUsernameObservers(dialog, name.text.toString())
            }
        }
        dialog.show()
    }

    private fun setupEditUsernameObservers(dialog: Dialog, name: String) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.userChangeName.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "setupObservers: ${it.data}")
                        binding.tvName.text = name
                        dialog.dismiss()
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

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        requireActivity().baseContext.resources.updateConfiguration(
            config,
            requireActivity().baseContext.resources.displayMetrics
        )
        requireActivity().finish();
        startActivity(requireActivity().intent);
    }


    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userData.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            userData = it.data.data
                            showUserData(userData)
                        }
                        is UiStateObject.ERROR -> {
                            hideProgress()
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun showUserData(userData: Data) {
        binding.apply {
            tvName.text = userData.fullName
            tvNumber.text = userData.phoneNumber

            if (!userData.photo.name.startsWith("default")) {
                ivProfile.setPadding(0, 0, 0, 0)
                ivProfile.loadImageUrl("${KeyValues.USER_IMAGE_BASE_URL}${userData.photo.name}")
                ivAdd.setImageResource(R.drawable.ic_edit_button)
            } else {
                ivProfile.setPadding(90, 90, 90, 90)
                ivProfile.setImageResource(R.drawable.ic_person)
            }
        }
    }


    private fun showPopup(v: View) {
        PopupMenu(requireContext(), v).apply {
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {

                    R.id.logOut -> {
                        sharedPref.saveLogIn(LOG_IN, false)
                        sharedPref.saveIsOwner(IS_OWNER, false)
                        sharedPref.saveUserId(USER_ID, "")
                        sharedPref.saveUserToken(USER_TOKEN, "")
                        findNavController().popBackStack()
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

    @OptIn(DelicateCoroutinesApi::class)
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
            binding.ivProfile.setPadding(0, 0, 0, 0)
            Glide.with(requireActivity()).load(image).into(binding.ivProfile)
            binding.ivAdd.setImageResource(R.drawable.ic_edit_button)


            GlobalScope.launch {
                val result = Compress.with(requireContext(), image)
                    .setQuality(80)
                    .concrete {
                        withMaxWidth(480f)
                        withMaxHeight(480f)
                        withScaleMode(ScaleMode.SCALE_HEIGHT)
                        withIgnoreIfSmaller(true)
                    }
                    .get(Dispatchers.IO)
                withContext(Dispatchers.Main) {
                    val reqFile: RequestBody =
                        RequestBody.create("image/jpg".toMediaTypeOrNull(), result)
                    val body: MultipartBody.Part =
                        MultipartBody.Part.createFormData("file", result.name, reqFile)
                    sendRequestToLoadImage(body)
                }
            }
        }

    private fun sendRequestToLoadImage(body: MultipartBody.Part) {
        viewModel.updateUserProfilePhoto(sharedPref.getUserID(USER_ID, ""), body)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userProfile.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                        }
                        is UiStateObject.SUCCESS -> {
                        }
                        is UiStateObject.ERROR -> {
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }
}
