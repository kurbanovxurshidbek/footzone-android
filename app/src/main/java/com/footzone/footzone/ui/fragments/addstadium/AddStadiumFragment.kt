package com.footzone.footzone.ui.fragments.addstadium

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.adapter.AddImageAdapter
import com.footzone.footzone.adapter.PitchImageEditAdapter
import com.footzone.footzone.databinding.FragmentAddStadiumBinding
import com.footzone.footzone.model.Image
import com.footzone.footzone.model.Pitch
import com.footzone.footzone.model.addstadium.Stadium
import com.footzone.footzone.model.addstadium.WorkingDay
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.UiStateObject
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
open class AddStadiumFragment : BaseFragment(R.layout.fragment_add_stadium) {
    lateinit var binding: FragmentAddStadiumBinding
    var items = ArrayList<Image>()
    val PICK_FROM_FILE_ADD = 1001
    val PICK_FROM_FILE_EDIT = 1002
    lateinit var adapterAdd: AddImageAdapter
    lateinit var adapterEdit: PitchImageEditAdapter
    var images: ArrayList<String>? = null
    var pitch: Pitch? = null
    var type: Int = 2
    var position = 0
    var latitude = 0.0
    var longitude = 0.0
    var result = ArrayList<WorkingDay>()
    private val viewModel by viewModels<AddStadiumViewModel>()

    var files = ArrayList<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        items.add(Image())
        type = arguments?.get(KeyValues.TYPE_DETAIL).toString().toInt()
        if (type == 1) {
            pitch = arguments?.get(KeyValues.PITCH_DETAIL) as Pitch
            images = pitch!!.images
        }

        setFragmentResultListener(KeyValues.TYPE_LOCATION) { requestKey, bundle ->
            latitude = 41.3248628798667
            longitude = 69.23367757896234
            // binding.tvPichLocation.text = result.toString()
        }

        setFragmentResultListener(KeyValues.TYPE_WORK_TIME) { requestKey, bundle ->
            result.addAll(bundle.getString("bundleKey") as ArrayList<WorkingDay>)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_add_stadium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddStadiumBinding.bind(view)

        if (type == 1) {
            initViewsEdit()
        } else {
            initViewsAdd()
        }
    }

    private fun initViewsEdit() {
        binding.apply {
            icClose.setOnClickListener { requireActivity().onBackPressed() }
            tvCancel.setOnClickListener { requireActivity().onBackPressed() }
            tvOccupancy.setOnClickListener { requireActivity().onBackPressed() }
            ivChooseLocation.setOnClickListener { openStadiumLocation() }
            ivChooseWorkTime.setOnClickListener { openChooseWorkTime() }
        }
        binding.apply {
            tvTitle.text = getText(R.string.str_edit_stadium)
            etPitchName.setText(pitch!!.name);
            tvPichLocation.text = "Toshkent , Furqat ko'cha"
            etPitchPhoneNumber.setText("+998 97 775 17 79")
            tvPitchWorkTime.text = "Du, SHe, Cho, Pa, Ju, Sha, Ya"
            etPitchPrice.setText(pitch!!.price.toString())

            adapterEdit = PitchImageEditAdapter(requireContext(), images!!) {
                position = it
                selectImage(PICK_FROM_FILE_EDIT)
            }
            recyclerView.adapter = adapterEdit

        }
    }

    private fun initViewsAdd() {
        adapterAdd = AddImageAdapter(this, items) {
            position = it
            selectImage(PICK_FROM_FILE_ADD)
        }
        binding.recyclerView.adapter = adapterAdd

        binding.apply {
            icClose.setOnClickListener { requireActivity().onBackPressed() }
            tvCancel.setOnClickListener { requireActivity().onBackPressed() }
            ivChooseLocation.setOnClickListener { openStadiumLocation() }
            ivChooseWorkTime.setOnClickListener { openChooseWorkTime() }
        }

        binding.tvOccupancy.setOnClickListener {
            val stadium =
                Stadium("", "", 20, 63.387784, 49.78327684, "Odil", ArrayList<WorkingDay>())
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)
            for (i in files) {
                builder.addFormDataPart("files",
                    i.name,
                    i.asRequestBody("image/jpg".toMediaTypeOrNull()))
            }

            builder.addFormDataPart("stadium", Gson().toJson(stadium))
            val body = builder.build()
            viewModel.postHolderStadium(body)
            observeViewModel()

        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.postStadium.collect {
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

    private fun selectImage(type: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) intent.action = Intent.ACTION_GET_CONTENT
        val chooserIntent = Intent.createChooser(intent, "Complete action using")

        if (type == PICK_FROM_FILE_ADD) {
            startActivityForResult(chooserIntent, PICK_FROM_FILE_ADD)
        } else {
            startActivityForResult(chooserIntent, PICK_FROM_FILE_EDIT)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FROM_FILE_ADD && resultCode == RESULT_OK) {
            try {
                val selectedImageUri: Uri = data?.data!!
                if (position == 0) {
                    items.add(Image(selectedImageUri))
                    val ins = requireActivity().contentResolver.openInputStream(selectedImageUri)
                    var image = File.createTempFile("file",
                        ".jpg",
                        requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                    val fileOutputStream = FileOutputStream(image)
                    ins?.copyTo(fileOutputStream)
                    ins?.close()
                    fileOutputStream.close()
//                    val reqFile: RequestBody =
//                        RequestBody.create("image/jpg".toMediaTypeOrNull(), image)
//                    val body: MultipartBody.Part =
//                        MultipartBody.Part.createFormData("files", image.name, reqFile)

                    files.add(image)

                    Log.d("TAG", "onActivityResult: ${files.size}")
                } else {

                    items[position].imageUri = selectedImageUri

                }
                adapterAdd.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (requestCode == PICK_FROM_FILE_EDIT && resultCode == RESULT_OK) {
            try {
                val selectedImageUri: Uri = data?.data!!
                images!![position] = selectedImageUri.toString()
                adapterEdit.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openStadiumLocation() {
        findNavController().navigate(R.id.action_addStadiumFragment_to_stadiumLocationFragment)
    }

    private fun openChooseWorkTime() {
        findNavController().navigate(R.id.action_addStadiumFragment_to_chooseWorkTimeFragment)
    }
}



