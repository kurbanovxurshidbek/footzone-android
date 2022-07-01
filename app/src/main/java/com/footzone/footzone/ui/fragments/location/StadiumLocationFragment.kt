package com.footzone.footzone.ui.fragments.location

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentStadiumLocationBinding
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng


class StadiumLocationFragment : BaseFragment(R.layout.fragment_stadium_location) {

    lateinit var binding: FragmentStadiumLocationBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null
    private val myLocationZoom = 10f
    private var cameraCurrentLatLng: LatLng? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStadiumLocationBinding.bind(view)
        initViews()
        installLocation()
    }

    private val callback = OnMapReadyCallback { googleMap ->
        //  googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = false
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        map = googleMap
        cameraMoveStartedListener(googleMap)
        setUpMap()
    }

    private fun initViews() {
        binding.apply {
            ivBack.setOnClickListener { requireActivity().onBackPressed() }
            tvCancel.setOnClickListener { requireActivity().onBackPressed() }
            tvSelection.setOnClickListener {
                setFragmentResult(
                    KeyValues.TYPE_LOCATION,
                    bundleOf(
                        "latitude" to cameraCurrentLatLng!!.latitude,
                        "longitude" to cameraCurrentLatLng!!.longitude
                    )
                )
                findNavController().popBackStack()
            }
        }
    }

    private fun installLocation() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun cameraMoveStartedListener(googleMap: GoogleMap) {
        googleMap.setOnCameraMoveStartedListener {
            binding.mapIcon.animate()
        }
        googleMap.setOnCameraIdleListener {
            cameraCurrentLatLng = googleMap.cameraPosition.target
        }
    }

    private fun setUpMap() {
        setupMe()
        btnMyLocationClickManager()
    }

    private fun setupMe() {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        currentLatLng,
                        myLocationZoom
                    )
                )
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(requireContext())
                    toast("ERROR loading location")
                }, 400)
            }
        }
        fusedLocationClient.lastLocation.addOnFailureListener {
            toast("$it")
        }
    }

    private fun btnMyLocationClickManager() {
        binding.findMyLocation.setOnClickListener {
            updateLastLocation()
        }
    }

    private fun updateLastLocation() {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    lastLocation!!.latitude,
                    lastLocation!!.longitude
                ), myLocationZoom
            )
        )
    }
}