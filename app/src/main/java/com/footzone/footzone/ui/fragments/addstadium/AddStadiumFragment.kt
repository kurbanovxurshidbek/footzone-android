package com.footzone.footzone.ui.fragments.addstadium

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.adapter.AddImageAdapter
import com.footzone.footzone.adapter.PitchImageEditAdapter
import com.footzone.footzone.databinding.FragmentAddStadiumBinding
import com.footzone.footzone.model.Image
import com.footzone.footzone.model.addstadium.Stadium
import com.footzone.footzone.model.addstadium.WorkingDay
import com.footzone.footzone.model.holderstadium.Data
import com.footzone.footzone.model.holderstadium.Photo
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    var isEdit: Boolean = false
    var position = 0
    var latitude = 0.0
    var longitude = 0.0
    var workTimes = ArrayList<WorkingDay>()
    private val viewModel by viewModels<AddStadiumViewModel>()
    lateinit var stadiumId: String
    var files = ArrayList<MultipartBody.Part>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        items.add(Image())
        isEdit = arguments?.get(KeyValues.TYPE_DETAIL) as Boolean
        if (isEdit) {
            stadiumId = arguments?.get(KeyValues.STADIUM_ID) as String
        }

       setFragmentResultListener(KeyValues.TYPE_LOCATION) { _, bundle ->
           latitude = bundle.get("latitude").toString().toDouble()
           longitude = bundle.get("longitude").toString().toDouble()
        }

        setFragmentResultListener(KeyValues.TYPE_WORK_TIME) { requestKey, bundle ->
            workTimes.addAll(bundle.get("workTimes") as ArrayList<WorkingDay>)
            binding.tvPitchWorkTime.text = bundle.get("wortTime").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (isEdit) {
            initViewsEdit()
        }
        return inflater.inflate(R.layout.fragment_add_stadium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddStadiumBinding.bind(view)
        if (!isEdit){
            initViewsAdd()
        }
    }

    private fun initViewsEdit() {

        viewModel.getHolderStadiums(stadiumId)
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getHolderStadium.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "setupObservers: ${it}")
                        refreshData(it.data.data)
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupObservers:${it}")
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun refreshData(data: Data) {
        binding.apply {
            icClose.setOnClickListener { requireActivity().onBackPressed() }
            tvCancel.setOnClickListener { requireActivity().onBackPressed() }
            tvOccupancy.setOnClickListener { requireActivity().onBackPressed() }
            ivChooseLocation.setOnClickListener { openStadiumLocation() }
            ivChooseWorkTime.setOnClickListener { openChooseWorkTime() }
        }
        binding.apply {
            tvTitle.text = getText(R.string.str_edit_stadium)
            etPitchName.setText(data.name);
            etPitchAddress.setText(data.address)
            etPitchPhoneNumber.setText(data.number)
           // tvPitchWorkTime.text = "Du, SHe, Cho, Pa, Ju, Sha, Ya"
            etPitchPrice.setText(data.hourlyPrice.toString())

            adapterEdit = PitchImageEditAdapter(requireContext(), data.photos as ArrayList<Photo>) {
                position = it
                selectImage(PICK_FROM_FILE_EDIT)
            }
            recyclerView.adapter = adapterEdit

        }
    }

    private fun initViewsAdd() {
        registerButtonControl()
        checkAllFields()

        binding.tvOccupancy.setOnClickListener {
            if (checkData()) {
                val stadiumAddress = binding.etPitchAddress.text.toString()
                val stadiumName = binding.etPitchName.text.toString()
                val stadiumNumber =  "+998${binding.etPitchPhoneNumber.text.toString().replace("\\s".toRegex(), "")}"
                val stadiumPrice = binding.etPitchPrice.text.toString()
                val stadium =
                    Stadium(stadiumAddress, stadiumNumber, stadiumPrice.toInt(), latitude, longitude, stadiumName, workTimes)
                viewModel.postHolderStadium(stadium, files)
                observeViewModel()
            }
        }

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

    }

    private fun checkAllFields() {
        binding.etPitchName.doAfterTextChanged {
            registerButtonControl()
        }
        binding.etPitchAddress.doAfterTextChanged {
            registerButtonControl()
        }
        binding.etPitchPhoneNumber.doAfterTextChanged {
            registerButtonControl()
        }

        binding.etPitchPrice.doAfterTextChanged {
            registerButtonControl()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun registerButtonControl() {
        if (checkData()) {
            binding.apply {
                tvOccupancy.setBackgroundResource(R.drawable.button_register_filled_rounded_corner2)
                tvOccupancy.isClickable = true
                tvOccupancy.setTextColor(Color.WHITE)
            }
        } else {
            binding.apply {
                tvOccupancy.setBackgroundResource(R.drawable.button_register_filled_rounded_corner1)
                tvOccupancy.isClickable = false
                tvOccupancy.setTextColor(R.color.buttonDisabledTextColor)

            }
        }
    }

    private fun checkData(): Boolean {
        val etPitchName = binding.etPitchName.text.toString()
        val etPitchAddress = binding.etPitchAddress.text.toString()
        val etPitchPhoneNumber = binding.etPitchPhoneNumber.text.toString()
        val etPitchPrice = binding.etPitchPrice.text.toString()


        return etPitchName.isNotEmpty() && etPitchAddress.isNotEmpty() && etPitchPhoneNumber.length == 12 &&
                etPitchPrice.isNotEmpty()
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
                    val image = File.createTempFile("file", ".jpg",
                        requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                    val fileOutputStream = FileOutputStream(image)
                    ins?.copyTo(fileOutputStream)
                    ins?.close()
                    fileOutputStream.close()
                    val reqFile: RequestBody =
                        RequestBody.create("image/jpg".toMediaTypeOrNull(), image)
                    val body: MultipartBody.Part =
                        MultipartBody.Part.createFormData("files", image.name, reqFile)

                    Log.d("TAG", "onActivityResult: ${body}")
                    files.add(body)
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
              //  images!![position] = selectedImageUri.toString()
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



