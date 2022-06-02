package com.footzone.footzone.ui.fragments.addstadium

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.adapter.AddImageAdapter
import com.footzone.footzone.adapter.PitchImageEditAdapter
import com.footzone.footzone.databinding.FragmentAddStadiumBinding
import com.footzone.footzone.model.Image
import com.footzone.footzone.model.Pitch
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = arguments?.get(KeyValues.TYPE_DETAIL).toString().toInt()
        if (type == 1) {
            pitch = arguments?.get(KeyValues.PITCH_DETAIL) as Pitch
            images = pitch!!.images
        }

        setFragmentResultListener(KeyValues.TYPE_LOCATION) { requestKey, bundle ->
            val result = bundle.getString("bundleKey")
            binding.tvPichLocation.text = result.toString()
        }

        setFragmentResultListener(KeyValues.TYPE_WORK_TIME) { requestKey, bundle ->
            val result = bundle.getString("bundleKey")
            binding.tvPitchWorkTime.text = result.toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddStadiumBinding.bind(view)

        binding.apply {
            icClose.setOnClickListener { requireActivity().onBackPressed() }
            tvCancel.setOnClickListener { requireActivity().onBackPressed() }
            tvOccupancy.setOnClickListener { requireActivity().onBackPressed() }
            ivChooseLocation.setOnClickListener { openStadiumLocation() }
            ivChooseWorkTime.setOnClickListener { openChooseWorkTime() }
        }

        if (type == 1) {
            initViewsEdit()
        } else {
            initViewsAdd()
        }
    }

    private fun initViewsEdit() {
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
    }

    private fun selectImage(type: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) intent.action = Intent.ACTION_GET_CONTENT
        val chooserIntent = Intent.createChooser(intent, "Complete action using")

        val captured_image = System.currentTimeMillis().toString() + ".jpg"
        val file = File(Environment.getExternalStorageDirectory(), captured_image)
        //val outputFileUri = Uri.fromFile(file)
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, "")
        captureIntent.putExtra("return-data", true)
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(captureIntent))

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



