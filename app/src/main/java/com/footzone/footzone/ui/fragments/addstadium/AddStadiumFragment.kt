package com.footzone.footzone.ui.fragments.addstadium

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.adapter.AddImageAdapter
import com.footzone.footzone.adapter.PitchImageEditAdapter
import com.footzone.footzone.databinding.FragmentAddStadiumBinding
import com.footzone.footzone.databinding.ToastChooseTimeBinding
import com.footzone.footzone.helper.OnClickEditEvent
import com.footzone.footzone.model.*
import com.footzone.footzone.ui.activity.MainActivity
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.LATITUDE
import com.footzone.footzone.utils.KeyValues.LONGITUDE
import com.footzone.footzone.utils.KeyValues.PICK_FROM_FILE_ADD
import com.footzone.footzone.utils.KeyValues.PICK_FROM_FILE_EDIT
import com.footzone.footzone.utils.KeyValues.WORK_TIME
import com.footzone.footzone.utils.KeyValues.WORK_TIMES
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
open class AddStadiumFragment : BaseFragment(R.layout.fragment_add_stadium) {
    lateinit var binding: FragmentAddStadiumBinding
    var items = ArrayList<Image>()
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
    var photos: LinkedList<EditPhoto> = LinkedList();
    var uris = ArrayList<Uri>()
    var isStart = false
    var stadiumNumber: String? = null
    var isViewCreated = true

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().getWindow()
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        items.add(Image())
        isEdit = arguments?.get(KeyValues.TYPE_DETAIL) as Boolean
        if (isEdit) {
            stadiumId = arguments?.get(KeyValues.STADIUM_ID) as String
        }

        setFragmentResultListener(KeyValues.TYPE_LOCATION) { _, bundle ->
            latitude = bundle.get(LATITUDE).toString().toDouble()
            longitude = bundle.get(LONGITUDE).toString().toDouble()
        }

        setFragmentResultListener(KeyValues.TYPE_WORK_TIME) { requestKey, bundle ->
            val works = bundle.get(WORK_TIMES) as ArrayList<WorkingDay>
            if (works.isNotEmpty()) {
                workTimes.clear()
                workTimes.addAll(works)
                binding.tvPitchWorkTime.text = bundle.get(WORK_TIME).toString()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        photos.add(EditPhoto("id", "name"))
        return inflater.inflate(R.layout.fragment_add_stadium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddStadiumBinding.bind(view)
        if (isEdit) {
            initViewsEdit()
        } else {
            initViewsAdd()
        }
    }

    private fun initViewsEdit() {
        viewModel.getHolderStadiums(stadiumId)
        if (!isStart) {
            setupObservers()
        }

        refreshAdapter()

        binding.apply {

            icClose.setOnClickListener {
                back()
            }

            tvCancel.setOnClickListener {
                back()
            }

            ivChooseLocation.setOnClickListener {
                isStart = true
                openStadiumLocation()
            }

            ivChooseWorkTime.setOnClickListener {
                isStart = true
                openChooseWorkTime()
            }
        }

        binding.tvBook.setOnClickListener {
            val stadiumAddress = binding.etPitchAddress.text.toString()
            val stadiumName = binding.etPitchName.text.toString()
            val stadiumPrice = binding.etPitchPrice.text.toString()
            val userId = sharedPref.getUserID(KeyValues.USER_ID, "")

            if (binding.etPitchPhoneNumber.text!!.length > 5) {
                stadiumNumber =
                    "+998${binding.etPitchPhoneNumber.text.toString().replace("\\s".toRegex(), "")}"
            }

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

                    for (photo in photos) {
                        if (photo.id == null) {
                            viewModel.addPhotoToStadium(
                                stadiumId,
                                convertUriMultipart(photo.name as Uri, "file")
                            )
                            observeViewModelAdd()
                        }
                    }

                    viewModel.editHolderStadium(stadiumId, stadium)
                    Log.d("TAG", "observeViewModelEdit: ${stadium}")
                    observeViewModelEdit()
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.str_error_entering_price),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                toast(getText(R.string.str_error_phone_number) as String)
            }
        }
    }

    private fun observeViewModelAdd() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addPhotoToStadium.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }
                        is UiStateObject.SUCCESS -> {
                            hideProgress()
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

    private fun refreshAdapter() {
        adapterEdit = PitchImageEditAdapter(photos, object : OnClickEditEvent {
            override fun setOnAddClickListener() {
                selectImage(PICK_FROM_FILE_EDIT)
            }

            override fun setOnDeleteClickListener(position: Int, id: String?) {
                Toast.makeText(
                    requireContext(),
                    getText(R.string.str_delete_image),
                    Toast.LENGTH_SHORT
                ).show()
                photos.removeAt(position)
                adapterEdit.notifyDataSetChanged()
                if (id != null) {
                    viewModel.deleteStadiumPhoto(stadiumId, id)
                    setupObserversPhoto()
                }
            }
        })
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = adapterEdit
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getHolderStadium.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            refreshData(it.data.data)
                            workTimes.addAll(it.data.data.workingDays)
                            if (isViewCreated) {
                                it.data.data.photos.forEach { it ->
                                    photos.add(EditPhoto(it.id, it.name))
                                }
                                isViewCreated = false
                            }

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

    private fun setupObserversPhoto() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteStadiumPhoto.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            //show progress
                        }

                        is UiStateObject.SUCCESS -> {
                            Log.d("TAG", "setupObserversPhoto: ${it}")
                        }
                        is UiStateObject.ERROR -> {
                            Log.d("TAG", "setupObserversPhoto: ${it}")
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun refreshData(data: StadiumData) {
        Log.d("TAG", "refreshData: '${data.workingDays}")
        workTimes(data.workingDays)
        binding.apply {
            tvTitle.text = getText(R.string.str_edit_stadium)
            etPitchName.setText(data.stadiumName);
            etPitchAddress.setText(data.address)
           // tvPitchWorkTime.text = getText(R.string.str_changing_game_days)
            etPitchPrice.setText(data.hourlyPrice.toString())
            recyclerView.adapter = adapterEdit
            stadiumNumber = data.number
            longitude = data.longitude
            latitude = data.latitude
        }
    }

    private fun workTimes(workingDays: List<WorkingDay>) {
        var string = ""
        workingDays.forEach{
            if (it.dayName == "MONDAY") {
                string += "Du, "
            }

            if (it.dayName == "TUESDAY") {
                string += "Se, "
            }

            if (it.dayName == "WEDNESDAY") {
                string += "Cho, "
            }

            if (it.dayName == "THURSDAY") {
                string += "Pa, "
            }

            if (it.dayName == "FRIDAY") {
                string += "Ju, "
            }

            if (it.dayName == "SATURDAY") {
                string += "Sha, "
            }

            if(it.dayName == "SUNDAY" ){
                string += "Ya"
            }
        }
        binding.tvPitchWorkTime.text = string
    }

    private fun observeViewModelEdit() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editHolderStadium.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }
                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            (requireActivity() as MainActivity).showToast(
                                getString(R.string.str_successfully_edited),
                                Toast.LENGTH_SHORT
                            )
                            back()
                        }
                        is UiStateObject.ERROR -> {
                            hideProgress()
                            (requireActivity() as MainActivity).showToast(
                                getString(R.string.str_error_edited),
                                Toast.LENGTH_SHORT
                            )
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun initViewsAdd() {
        registerButtonControl()
        checkAllFields()

        binding.tvBook.setOnClickListener {

            uris.forEach { selectedImageUri ->
                files.add(convertUriMultipart(selectedImageUri, "files"))
            }
            if (checkData()) {
                val stadiumAddress = binding.etPitchAddress.text.toString()
                val stadiumName = binding.etPitchName.text.toString()
                val stadiumNumber =
                    "+998${
                        binding.etPitchPhoneNumber.text.toString().replace("\\s".toRegex(), "")
                    }"
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

                    observeViewModel()
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        getText(R.string.str_error_entering_price),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        adapterAdd = AddImageAdapter(this, items) {
            position = it
            selectImage(PICK_FROM_FILE_ADD)
        }
        binding.recyclerView.adapter = adapterAdd

        binding.apply {
            icClose.setOnClickListener {
                back()
            }
            tvCancel.setOnClickListener {
                back()
            }
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
                tvBook.setBackgroundResource(R.drawable.linear_rounded_background)
                tvBook.isClickable = true
                tvBook.setTextColor(Color.WHITE)
            }
        } else {
            binding.apply {
                tvBook.setBackgroundResource(R.drawable.button_register_filled_rounded_corner1)
                tvBook.isClickable = false
                tvBook.setTextColor(R.color.buttonDisabledTextColor)

            }
        }
    }

    private fun checkData(): Boolean {
        val etPitchName = binding.etPitchName.text.toString()
        val etPitchAddress = binding.etPitchAddress.text.toString()
        val etPitchPhoneNumber = binding.etPitchPhoneNumber.text.toString()
        val etPitchPrice = binding.etPitchPrice.text.toString()
        return etPitchName.isNotEmpty() && etPitchAddress.isNotEmpty() && etPitchPhoneNumber.length == 12 &&
                etPitchPrice.isNotEmpty() && workTimes.isNotEmpty()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postStadium.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }
                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            Log.d("TAG", "observeViewModel: ")
                            (requireActivity() as MainActivity).showToast(
                                getString(R.string.str_successfully_edded),
                                Toast.LENGTH_SHORT
                            )
                        }
                        is UiStateObject.ERROR -> {
                            hideProgress()
                            Log.d("TAG", "observeViewModel: ${it.message}")
                            (requireActivity() as MainActivity).showToast(
                                getString(R.string.str_not_succesfull),
                                Toast.LENGTH_SHORT
                            )
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun selectImage(type: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) intent.action =
            Intent.ACTION_GET_CONTENT
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

                photos.add(EditPhoto(null, selectedImageUri))

                adapterEdit.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * This is function, to upgrade from Uri to MultipartBody.Part
     */
    private fun convertUriMultipart(selectedImageUri: Uri, name: String): MultipartBody.Part {
        val ins = requireActivity().contentResolver.openInputStream(selectedImageUri)
        val image = File.createTempFile(
            "file", ".jpg",
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        val fileOutputStream = FileOutputStream(image)
        ins?.copyTo(fileOutputStream)
        ins?.close()
        fileOutputStream.close()

        val cr: ContentResolver = requireContext().contentResolver
        val inputStream: InputStream? = cr.openInputStream(selectedImageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val data = baos.toByteArray()

        val reqFile: RequestBody =
            RequestBody.create("image/jpg".toMediaTypeOrNull(), compressCapture(data)!!)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData(name, image.name, reqFile)

        return body
    }


    open fun compressCapture(capture: ByteArray): ByteArray? {
        val compression = 50
        val bitmap = BitmapFactory.decodeByteArray(capture, 0, capture.size)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compression, outputStream)
        return outputStream.toByteArray()
    }

    private fun openStadiumLocation() {
        findNavController().navigate(R.id.action_addStadiumFragment_to_stadiumLocationFragment)
    }

    private fun openChooseWorkTime() {
        findNavController().navigate(R.id.action_addStadiumFragment_to_chooseWorkTimeFragment)
    }
}