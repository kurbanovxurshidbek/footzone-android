package com.footzone.footzone.ui.fragments


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.adapter.AddImageAdapter
import com.footzone.footzone.adapter.PitchImageEditAdapter
import com.footzone.footzone.adapter.PitchImagesAdapter
import com.footzone.footzone.adapter.PlayedPitchAdapter
import com.footzone.footzone.databinding.FragmentAddStadiumBinding
import com.footzone.footzone.model.Image
import com.footzone.footzone.model.Pitch
import com.footzone.footzone.utils.KeyValues
import kotlin.properties.Delegates

open class AddStadiumFragment : Fragment() {
    lateinit var binding: FragmentAddStadiumBinding
    var items = ArrayList<Image>()
    val PICK_FROM_FILE = 1001
    lateinit var adapter: AddImageAdapter
    var pitch: Pitch? = null
    var type: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = arguments?.get(KeyValues.TYPE_DETAIL).toString().toInt()
        if (type == 1){
            pitch = arguments?.get(KeyValues.PITCH_DETAIL) as Pitch
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

        binding.apply {
            icClose.setOnClickListener { requireActivity().onBackPressed() }
            tvCancel.setOnClickListener { requireActivity().onBackPressed() }
            tvOccupancy.setOnClickListener { requireActivity().onBackPressed() }
        }

        if (type == 1){
            initViewsEdit()
        }else{
            initViewsAdd()
        }
    }

    private fun initViewsEdit() {
        binding.apply {
            etPitchName.setText(pitch!!.name);
            tvPichLocation.text = "Toshkent , Furqat ko'cha"
            etPitchPhoneNumber.setText("+998 97 775 17 79")
            tvPitchWorkTime.text = "Du, SHe, Cho, Pa, Ju, Sha, Ya"
            etPitchPrice.setText(pitch!!.price.toString())

            val pitchImageEditAdapter = PitchImageEditAdapter(requireContext(), pitch!!.images){
                selectImage()
            }
            recyclerView.adapter = pitchImageEditAdapter


        }
    }

    private fun initViewsAdd() {
        items.add(Image())
        binding.apply {
            ivChooseLocation.setOnClickListener {
                openStadiumLocation()
            }

            ivChooseWorkTime.setOnClickListener {
                openChooseWorkTime()
            }
        }

        adapter = AddImageAdapter(this, items) {
           selectImage()
        }
        binding.recyclerView.adapter = adapter
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) intent.action = Intent.ACTION_GET_CONTENT
        val chooserIntent = Intent.createChooser(intent, "Complete action using")

        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, "")
        captureIntent.putExtra("return-data", true)
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(captureIntent))
        startActivityForResult(chooserIntent, PICK_FROM_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FROM_FILE) {
            try {
                val selectedImage: Uri = data?.getData()!!
                items.add(Image(selectedImage))
                adapter.notifyDataSetChanged()
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



