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
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.adapter.AddImageAdapter
import com.footzone.footzone.adapter.PitchImageEditAdapterTest
import com.footzone.footzone.databinding.FragmentAddStadiumBinding
import com.footzone.footzone.databinding.ToastChooseTimeBinding
import com.footzone.footzone.helper.OnClickEditEvent
import com.footzone.footzone.model.*
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.PICK_FROM_FILE_ADD
import com.footzone.footzone.utils.KeyValues.PICK_FROM_FILE_EDIT
import com.footzone.footzone.utils.KeyValues.WORK_TIME
import com.footzone.footzone.utils.KeyValues.WORK_TIMES
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
open class AddStadiumFragment : BaseFragment(R.layout.fragment_add_stadium) {
    lateinit var binding: FragmentAddStadiumBinding
    var items = ArrayList<Image>()
    lateinit var adapterAdd: AddImageAdapter
    lateinit var adapterEdit: PitchImageEditAdapterTest
    var isEdit: Boolean = false
    var position = 0
    var positionImage = 0
    var latitude = 0.0
    var longitude = 0.0
    var workTimes = ArrayList<WorkingDay>()
    private val viewModel by viewModels<AddStadiumViewModel>()
    lateinit var stadiumId: String
    var files = ArrayList<MultipartBody.Part>()
    var photos: LinkedList<EditPhoto> = LinkedList();
    var uris = ArrayList<Uri>()
    val filesPhoto = ArrayList<EditStadiumPhotoRequest>()
    var jonibek = false
    var stadiumNumber: String? = null

    @Inject
    lateinit var sharedPref: SharedPref

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
            Log.d("TAG", "onCreate: $latitude")
            Log.d("TAG", "onCreate: $longitude")
        }

        setFragmentResultListener(KeyValues.TYPE_WORK_TIME) { requestKey, bundle ->
            workTimes.addAll(bundle.get(WORK_TIMES) as ArrayList<WorkingDay>)
            binding.tvPitchWorkTime.text = bundle.get(WORK_TIME).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        photos.add(EditPhoto("emferf", "rnfwernfwe"))
        return inflater.inflate(R.layout.fragment_add_stadium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddStadiumBinding.bind(view)
        if (isEdit) {
            initViewsEdit()
        }else{
            initViewsAdd()
        }
    }

    private fun initViewsEdit() {
        viewModel.getHolderStadiums(stadiumId)
        if (!jonibek) {
            setupObservers()
        }

        refreshAdapter()

        binding.apply {
            icClose.setOnClickListener { requireActivity().onBackPressed() }
            tvCancel.setOnClickListener { requireActivity().onBackPressed() }
            ivChooseLocation.setOnClickListener {
                jonibek = true
                openStadiumLocation()
            }
            ivChooseWorkTime.setOnClickListener {
                jonibek = true
                openChooseWorkTime()
            }
        }

        binding.tvOccupancy.setOnClickListener {
            val stadiumAddress = binding.etPitchAddress.text.toString()
            val stadiumName = binding.etPitchName.text.toString()
            val stadiumPrice = binding.etPitchPrice.text.toString()
            val userId = sharedPref.getUserID(KeyValues.USER_ID, "")

            if (binding.etPitchPhoneNumber.text!!.length > 5){
                stadiumNumber =   "+998${binding.etPitchPhoneNumber.text.toString().replace("\\s".toRegex(), "")}"
            }

//            val workDat =
//                if (workTimes.isNotEmpty()){
//                    workTimes
//                }else{
//
//                }

            Log.d("TAG", "refreshData: ${photos}")
            for (pos in 1..photos.size-1){
                if (photos[pos].isExist == true && photos[pos].name is Uri){
                    Log.d("TAG", "refreshData: ${photos[pos].name}")
                    val body = convertUriMultipart(photos[pos].name as Uri)
                    filesPhoto.add(EditStadiumPhotoRequest(photos[pos].id!!, body, true))
                }else if (photos[pos].name is Uri){
                    val body = convertUriMultipart(photos[pos].name as Uri)
                    filesPhoto.add(EditStadiumPhotoRequest(photos[pos].id!!, body, true))
                }
            }
            Log.d("TAG", "refreshData: ${filesPhoto}")

            if (stadiumNumber!!.length == 13) {
                try {
                    val stadium =
                        AddStadiumRequest(
                            stadiumAddress,
                            stadiumNumber!!,
                            stadiumPrice.toInt(),
                            latitude,
                            longitude,
                            stadiumName,
                            userId,
                            workTimes
                        )

                    viewModel.editHolderStadium(stadiumId, stadium)
                    viewModel.editHolderStadiumPhoto(stadiumId, filesPhoto)
                    observeViewModelEdit()
                    observeViewModelEditPhoto()
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Narxni kiritishda xatolik",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                toast("Telefon nomer kiritishda xatolik bor.")
            }
        }
    }

    private fun refreshAdapter() {
        adapterEdit = PitchImageEditAdapterTest(photos, object : OnClickEditEvent{
            override fun setOnAddClickListener() {
                positionImage = 0
                selectImage(PICK_FROM_FILE_EDIT)
            //filesPhoto.add(EditStadiumPhotoRequest(UUID.randomUUID().toString(), body, true))
            }

            override fun setOnEditClickListener(position: Int, id: String) {
                Log.d("TAG", "setOnEditClickListener: ${position}")
                positionImage = position
                selectImage(PICK_FROM_FILE_EDIT)
//                    val body = convertUriMultipart(selectedImageUri!!)
//                    //filesPhoto.add(EditStadiumPhotoRequest(id, body, true))
            }

            override fun setOnDeleteClickListener(position: Int, id: String) {
                Toast.makeText(requireContext(), "Delete Image", Toast.LENGTH_SHORT).show()
                filesPhoto.add(EditStadiumPhotoRequest(id, null, false))
                photos.removeAt(position)
                positionImage = 0
                adapterEdit.notifyDataSetChanged()
                Log.d("TAG", "setOnDeleteClickListener set: ${photos}")
            }
        })
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = adapterEdit
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
                        it.data.data.photos.forEach { it->
                            photos.add(EditPhoto(it.id, it.name, true))
                            Log.d("TAG", "setOnAddClickListener: ${photos}")
                        }
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

    private fun refreshData(data: StadiumData) {
        binding.apply {
            tvTitle.text = getText(R.string.str_edit_stadium)
            etPitchName.setText(data.stadiumName);
            etPitchAddress.setText(data.address)
            tvPitchWorkTime.text = "O'yin kunlarini o'zgartirish"
            etPitchPrice.setText(data.hourlyPrice.toString())
            recyclerView.adapter = adapterEdit
            stadiumNumber = data.number
        }
    }

    private fun observeViewModelEditPhoto() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.editHolderStadiumPhoto.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }
                    is UiStateObject.SUCCESS -> {
                        val binding =
                            ToastChooseTimeBinding.inflate(LayoutInflater.from(requireActivity()))

                        binding.tvToast.text = "Maydon muvofaqiyatli qo'shildi."
                        val custToast = Toast(requireContext())
                        custToast.setView(binding.root)
                        custToast.show()
                        requireActivity().onBackPressed()
                        Log.d("TAG", "observeViewModelEditPhoto: ")

                    }
                    is UiStateObject.ERROR -> {
                        val binding =
                            ToastChooseTimeBinding.inflate(LayoutInflater.from(requireActivity()))

                        binding.tvToast.text = "Maydon muvofaqiyatli qo'shildi."
                        val custToast = Toast(requireContext())
                        custToast.setView(binding.root)
                        custToast.show()
                        requireActivity().onBackPressed()
                        Log.d("TAG", "observeViewModelEditPhoto: error")
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun observeViewModelEdit() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.editHolderStadium.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }
                    is UiStateObject.SUCCESS -> {
                        val binding =
                            ToastChooseTimeBinding.inflate(LayoutInflater.from(requireActivity()))

                        binding.tvToast.text = "Maydon muvofaqiyatli tahrirlandi."
                        val custToast = Toast(requireContext())
                        custToast.setView(binding.root)
                        custToast.show()
                        requireActivity().onBackPressed()
                        Log.d("TAG", "observeViewModelEdit: ${it}")

                    }
                    is UiStateObject.ERROR -> {
                        val binding =
                            ToastChooseTimeBinding.inflate(LayoutInflater.from(requireActivity()))

                        binding.tvToast.text = "Maydon muvofaqiyatli tahrirlanmadi, qayta urinib ko'ring."
                        val custToast = Toast(requireContext())
                        custToast.setView(binding.root)
                        custToast.show()
                        requireActivity().onBackPressed()
                        Log.d("TAG", "observeViewModelEdit error: ${it}")
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun initViewsAdd() {
        registerButtonControl()
        checkAllFields()

        binding.tvOccupancy.setOnClickListener {

            uris.forEach { selectedImageUri ->
                files.add(convertUriMultipart(selectedImageUri))
            }

            if (checkData()) {
                val stadiumAddress = binding.etPitchAddress.text.toString()
                val stadiumName = binding.etPitchName.text.toString()
                val stadiumNumber =
                    "+998${binding.etPitchPhoneNumber.text.toString().replace("\\s".toRegex(), "")}"
                val stadiumPrice = binding.etPitchPrice.text.toString()
                val userId = sharedPref.getUserID(KeyValues.USER_ID, "")
                try {
                    val stadium =
                        AddStadiumRequest(
                            stadiumAddress,
                            stadiumNumber,
                            stadiumPrice.toInt(),
                            latitude,
                            longitude,
                            stadiumName,
                            userId,
                            workTimes
                        )
                    viewModel.postHolderStadium(stadium, files)
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Narxni kiritishda xatolik",
                        Toast.LENGTH_SHORT
                    ).show()
                }

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
                        val binding =
                            ToastChooseTimeBinding.inflate(LayoutInflater.from(requireActivity()))

                        binding.tvToast.text = "Maydon muvofaqiyatli qo'shildi."
                        val custToast = Toast(requireContext())
                        custToast.setView(binding.root)
                        custToast.show()
                        requireActivity().onBackPressed()

                    }
                    is UiStateObject.ERROR -> {
                        val binding =
                            ToastChooseTimeBinding.inflate(LayoutInflater.from(requireActivity()))

                        binding.tvToast.text = "Maydon muvofaqiyatli qo'shildi."
                        val custToast = Toast(requireContext())
                        custToast.setView(binding.root)
                        custToast.show()
                        requireActivity().onBackPressed()
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
            startActivityForResult(chooserIntent, type)
        } else {
            startActivityForResult(chooserIntent, type)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FROM_FILE_ADD && resultCode == RESULT_OK) {
            try {
                val selectedImageUri: Uri = data?.data!!
                if (position == 0) {
                    uris.add(selectedImageUri)
                    items.add(Image(selectedImageUri))
                } else {
                    items[position].imageUri = selectedImageUri
                    uris[position - 1] = selectedImageUri
                }
                adapterAdd.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (requestCode == PICK_FROM_FILE_EDIT && resultCode == RESULT_OK) {
            try {
                val selectedImageUri = data!!.data!!
                if (positionImage != 0){
                    photos[positionImage].name = selectedImageUri
                    positionImage = 0
                }else {
                    photos.add(EditPhoto(UUID.randomUUID().toString(),selectedImageUri, false))
                    positionImage = 0
                }
                adapterEdit.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun convertUriMultipart(selectedImageUri: Uri): MultipartBody.Part{
        val ins = requireActivity().contentResolver.openInputStream(selectedImageUri!!)
        val image = File.createTempFile(
            "file", ".jpg",
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        val fileOutputStream = FileOutputStream(image)
        ins?.copyTo(fileOutputStream)
        ins?.close()
        fileOutputStream.close()
        val reqFile: RequestBody =
            RequestBody.create("image/jpg".toMediaTypeOrNull(), image)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("files", image.name, reqFile)

        return body
    }

    private fun openStadiumLocation() {
        findNavController().navigate(R.id.action_addStadiumFragment_to_stadiumLocationFragment)
    }

    private fun openChooseWorkTime() {
        findNavController().navigate(R.id.action_addStadiumFragment_to_chooseWorkTimeFragment)
    }
}



