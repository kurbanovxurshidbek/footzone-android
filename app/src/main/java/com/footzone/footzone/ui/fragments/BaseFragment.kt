package com.footzone.footzone.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.IntentFilter
import android.content.IntentSender
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.footzone.footzone.broadcast.InternetBroadcastReceiver
import com.footzone.footzone.databinding.ToastChooseTimeBinding
import com.footzone.footzone.model.Comment
import com.footzone.footzone.ui.activity.MainActivity
import com.footzone.footzone.utils.ProgressBarDialog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import java.lang.Exception

open class BaseFragment(private val layoutResID: Int) : Fragment() {

    lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingDialog = ProgressBarDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutResID, container, false)
    }

    open fun hideKeyboard(activity: FragmentActivity) {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = requireActivity().currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    open fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model
        } else "$manufacturer $model"
    }

    open fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    open fun toastLong(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    open fun showProgress() {
        loadingDialog.show()
    }

    open fun hideProgress() {
        loadingDialog.dismiss()
    }

    open fun back() {
        requireActivity().onBackPressed()
    }
}