package com.footzone.footzone.ui.fragments.location

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentStadiumLocationBinding
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.LocationHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker


class StadiumLocationFragment : BaseFragment(R.layout.fragment_stadium_location),
    OnMapReadyCallback,
    GoogleMap.CancelableCallback, GoogleMap.OnMarkerDragListener {

    lateinit var binding: FragmentStadiumLocationBinding
    private var lastLocation: Location? = null
    private var cameraCurrentLatLng: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private val myLocationZoom = 16.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFusedLocationClient()
    }

    private fun initFusedLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStadiumLocationBinding.bind(view)
        initViews()
    }


    private fun initViews() {
        val locationCur = setUpMap()
        binding.apply {
            icClose.setOnClickListener { requireActivity().onBackPressed() }
            tvCancel.setOnClickListener { requireActivity().onBackPressed() }
            tvSelection.setOnClickListener {
                Log.d("TAG", "initViews: ${locationCur.latitude} ${locationCur.longitude} ")
                setFragmentResult(
                    KeyValues.TYPE_LOCATION,
                    bundleOf(
                        "latitude" to locationCur.latitude,
                        "longitude" to locationCur.longitude
                    )
                )
                findNavController().popBackStack()
            }
        }

        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        setUpMap()
        mMap = p0
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(41.2795325, 69.2143852),
                myLocationZoom
            )
        )
        val target = LatLng(41.2795325, 69.2143852)
        val cameraUpdate = CameraPosition.Builder().target(target).zoom(myLocationZoom).build()
        mMap.moveCamera(
            CameraUpdateFactory.newCameraPosition(cameraUpdate)
        )

        mMap.setOnCameraMoveStartedListener {
            //todo start animate marker
            binding.mapIcon.animate().setDuration(300).translationY(-binding.mapIcon.height / 2.0f)
                .start()
        }

        mMap.setOnCameraIdleListener {
            binding.mapIcon.animate().translationY(0f).setDuration(300).start()
        }
    }

    private fun setUpMap(): LatLng {
        var currentLatLng: LatLng? = null
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        currentLatLng!!,
                        myLocationZoom
                    )
                )
            }
        }
        return currentLatLng!!
    }


    override fun onCancel() {

    }

    override fun onFinish() {

    }

    override fun onMarkerDrag(p0: Marker) {

    }

    override fun onMarkerDragEnd(p0: Marker) {
        Log.d("TAG", "onMarkerDragEnd: ${p0.position.latitude} ${p0.position.longitude}")
    }

    override fun onMarkerDragStart(p0: Marker) {

    }
}