
package com.footzone.footzone.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ActivityMapsBinding
import com.footzone.footzone.utils.LocationHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.CancelableCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var isFirstTime = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle())



        // Add a marker in Sydney and move the camera
        permissionRequest()
    }

    private fun permissionRequest() {
        if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            request()
        } else {
            setUpCamera()
        }
    }

    private var userLocationMarker: Marker? = null

    private fun setUpCamera() {
        val locationHelper = LocationHelper()
        locationHelper.startLocationUpdates(this)
        locationHelper.addLocationlistener(object : LocationHelper.LocationListener {
            override fun onLocationChanged(location: Location) {
                if (isFirstTime) {
                    val target = LatLng(location.latitude, location.longitude)
                    val cameraUpdate = CameraPosition.Builder().target(target).zoom(16f).build()
                    mMap.moveCamera(
                        CameraUpdateFactory.newCameraPosition(cameraUpdate)
                    )
                    isFirstTime = false
                }


//                if (userLocationMarker == null) {
////                    val bitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_location_svgrepo_com)
////                    val icon = BitmapDescriptorFactory.fromBitmap(bitmap)
//                    userLocationMarker = mMap.addMarker(
//                        MarkerOptions()
//                            .position(target)
//                    )
//                } else {
//                    userLocationMarker?.position = target
//                }


            }
        })
    }

    private fun request() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) { /* ... */
                    setUpCamera()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) { /* ... */
                    //open settings

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            }).check()
    }

    override fun onCancel() {

    }

    override fun onFinish() {

    }
}