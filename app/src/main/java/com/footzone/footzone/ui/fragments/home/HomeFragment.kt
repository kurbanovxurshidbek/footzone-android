package com.footzone.footzone.ui.fragments.home

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.androidbolts.topsheet.TopSheetBehavior
import com.directions.route.*
import com.footzone.footzone.R
import com.footzone.footzone.adapter.PitchAdapter
import com.footzone.footzone.databinding.FragmentHomeBinding
import com.footzone.footzone.databinding.LayoutAcceptBinding
import com.footzone.footzone.databinding.LayoutEnterDialogBinding
import com.footzone.footzone.helper.OnClickEvent
import com.footzone.footzone.model.*
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.EnterAccountDialog
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.IS_FAVOURITE_STADIUM
import com.footzone.footzone.utils.KeyValues.IS_OWNER
import com.footzone.footzone.utils.KeyValues.STADIUM_ID
import com.footzone.footzone.utils.KeyValues.USER_ID
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import com.footzone.footzone.utils.commonfunction.Functions.resRating
import com.footzone.footzone.utils.commonfunction.Functions.setFavouriteBackground
import com.footzone.footzone.utils.commonfunction.Functions.setUnFavouriteBackground
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home), RoutingListener,
    GoogleMap.OnMarkerClickListener {

    private val viewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var sharedPref: SharedPref

    private lateinit var binding: FragmentHomeBinding
    private lateinit var bottomSheet: View
    private lateinit var topSheet: View
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetBehaviorType: BottomSheetBehavior<View>
    private lateinit var topSheetBehavior: TopSheetBehavior<View>
    private var stadiumsList = ArrayList<ShortStadiumDetail>()
    private var stadiumsFilteredList = ArrayList<ShortStadiumDetail>()
    private var favouriteStadiums = ArrayList<String>()
    private lateinit var enterAccountDialog: EnterAccountDialog

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null
    private val myLocationZoom = 10f
    private var markerList = ArrayList<Marker>()
    private var cameraCurrentLatLng: LatLng? = null
    private var polyLines: MutableList<Polyline>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        initViews(view)
        installLocation()
        sendRequestToGetFavouriteStadiumsList()
    }

    private val callback = OnMapReadyCallback { googleMap ->
        //  googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = false
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        map = googleMap
        cameraMoveStartedListener(googleMap)
        map.setOnMarkerClickListener(this)
        setUpMap()
    }

    override fun onRoutingFailure(p0: RouteException?) {

    }

    override fun onRoutingStart() {

    }

    override fun onRoutingSuccess(route: java.util.ArrayList<Route>?, shortestRouteIndex: Int) {
        if (polyLines != null) {
            polyLines?.clear()
        }
        val polyOptions = PolylineOptions()
        var polylineStartLatLng: LatLng? = null
        var polylineEndLatLng: LatLng? = null
        polyLines = ArrayList()
        for (i in route!!.indices) {
            if (i == shortestRouteIndex) {
                polyOptions.color(
                    Color.rgb(
                        (0..255).random(),
                        (0..255).random(),
                        (0..255).random()
                    )
                )
                polyOptions.width(10f)
                polyOptions.addAll(route[shortestRouteIndex].points)
                val polyline = map.addPolyline(polyOptions)
                polylineStartLatLng = polyline.points[0]
                val k = polyline.points.size
                polylineEndLatLng = polyline.points[k - 1]
                (polyLines as ArrayList<Polyline>).add(polyline)
            }
        }
    }

    override fun onRoutingCancelled() {

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        routeClear()
        if (cameraCurrentLatLng != null)
            findRoutes(
                LatLng(cameraCurrentLatLng!!.latitude, cameraCurrentLatLng!!.longitude),
                marker.position
            ) else
            findRoutes(LatLng(lastLocation!!.latitude, lastLocation!!.longitude), marker.position)
        return true
    }

    private fun routeClear() {
        if (polyLines != null)
            polyLines!![0].remove()
    }

    private fun findRoutes(start: LatLng?, end: LatLng?) {
        if (start == null || end == null) {
            toast("Unable to get location")
            updateLastLocation()
        } else {
            val routing = Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(start, end)
                .key("AIzaSyCVwdU3slouglv7TBDh3juGegafJVnKx8U")
                .build()
            routing.execute()
            if (cameraCurrentLatLng != null)
                16f.animateCamera(
                    LatLng(
                        cameraCurrentLatLng!!.latitude,
                        cameraCurrentLatLng!!.longitude
                    )
                ) else
                16f.animateCamera(LatLng(lastLocation!!.latitude, lastLocation!!.longitude))
        }
    }

    private fun Float.animateCamera(toLatLong: LatLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(toLatLong, this))
    }

    private fun cameraMoveStartedListener(googleMap: GoogleMap) {
        googleMap.setOnCameraMoveStartedListener {
            binding.mapIcon.animate()
            hideBottomSheet(bottomSheetBehaviorType)
        }
        googleMap.setOnCameraIdleListener {
            cameraCurrentLatLng = googleMap.cameraPosition.target
            showBottomSheet(bottomSheetBehaviorType)
        }
    }

    private fun installLocation() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setUpMap() {
        setupMe()
        btnMyLocationClickManager()
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
                sendRequestToGetAllStadiums()
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

    private fun initViews(view: View) {
        controlOwnerOption()
        val bottomSheetTypes = view.findViewById<View>(R.id.bottomSheetTypes)
        bottomSheet = view.findViewById(R.id.bottomSheetPitchList)
        topSheet = view.findViewById(R.id.topSheet)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        topSheetBehavior = TopSheetBehavior.from(topSheet)
        bottomSheetBehaviorType = BottomSheetBehavior.from(bottomSheetTypes)

        showBottomSheet(bottomSheetBehaviorType)

        binding.bottomSheetTypes.apply {
            linearMyStadium.setOnClickListener {
                openMyStadiumFragment()
            }
            linearNearPitch.setOnClickListener {
                hideBottomSheet(bottomSheetBehaviorType)
                sendRequestToGetNearbyStadiums()
            }
            linearSelectedPitch.setOnClickListener {
                hideBottomSheet(bottomSheetBehaviorType)
                sendRequestToGetFavouriteStadiums()
            }
            linearBookedPitch.setOnClickListener {
                hideBottomSheet(bottomSheetBehaviorType)
                sendRequestToGetPreviouslyBookedStadiums()
            }
        }

        binding.topSheet.apply {
            tvCurrentlyOpen.setOnClickListener {
                stadiumsFilteredList.clear()
                stadiumsList.forEach {
                    if (it.isOpen.open)
                        stadiumsFilteredList.add(it)
                }
                refreshAdapter(favouriteStadiums, stadiumsFilteredList)
            }
            tvWellCommented.setOnClickListener {
                stadiumsFilteredList.clear()
                stadiumsList.forEach {
                    if (resRating(it.comments) > 2) {
                        stadiumsFilteredList.add(it)
                    }
                }
                refreshAdapter(favouriteStadiums, stadiumsFilteredList)
            }
        }

        hideBottomSheet(bottomSheetBehavior)
        hideTopSheet()

        binding.bottomSheetTypes.edtPitchSearch.setOnTouchListener { p0, p1 ->
            bottomSheetBehaviorType.state = BottomSheetBehavior.STATE_EXPANDED
            binding.bottomSheetTypes.edtPitchSearch.isEnabled = true
            false
        }

        binding.notificationButton.setOnClickListener {
            openNotificationFragment()
        }

        binding.bottomSheetTypes.edtPitchSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(requireActivity())
                if (binding.bottomSheetTypes.edtPitchSearch.text.isNotEmpty()) {
                    hideBottomSheet(bottomSheetBehaviorType)
                    sendRequestToGetSearchedStadiums(binding.bottomSheetTypes.edtPitchSearch.text.toString())
                } else {
                    bottomSheetBehaviorType.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                true
            } else false
        }
    }

    private fun openNotificationFragment() {
        if (sharedPref.getIsOwner(IS_OWNER)) {
            findNavController().navigate(R.id.action_homeFragment_to_adminNotificationFragment)
        } else {
            findNavController().navigate(R.id.action_homeFragment_to_userNotificationFragment)
        }
    }

    private fun controlOwnerOption() {
        if (sharedPref.getIsOwner(IS_OWNER)) {
            binding.bottomSheetTypes.linearMyStadium.visibility = View.VISIBLE
        } else {
            binding.bottomSheetTypes.linearMyStadium.visibility = View.GONE
        }
    }

    private fun sendRequestToGetAllStadiums() {
        viewModel.getAllStadiums()
        observeAllStadiums()
    }

    private fun sendRequestToGetFavouriteStadiumsList() {
        val userId = sharedPref.getUserID(USER_ID, "")
        if (userId.isNotEmpty()) {
            viewModel.getFavouriteStadiumsList(userId)
            observeFavouriteStadiumsList()
        }
    }

    private fun sendRequestToGetSearchedStadiums(stadiumToSearch: String) {
        viewModel.getSearchedStadiums(stadiumToSearch)
        observeSearchedStadium()
    }

    private fun sendRequestToGetPreviouslyBookedStadiums() {
        if (sharedPref.getUserID(USER_ID, "").isNotEmpty()) {
            viewModel.getPreviouslyBookedStadiums()
            observePreviouslyBookedStadiums()
        } else {
            toast(
                "Siz hali ro'yxatdan o'tmagansiz.\n" +
                        "Sahifam bo'limidan ro'yxatdan o'tishingiz mumkin"
            )
        }
    }

    private fun sendRequestToAddFavouriteStadiums(stadiumId: String) {
        val userID = sharedPref.getUserID(USER_ID, "")
        if (userID.isNotEmpty()) {
            val favouriteStadiumRequest =
                FavouriteStadiumRequest(stadiumId, userID)
            viewModel.addToFavouriteStadiums(favouriteStadiumRequest)
        } else {
            toast(
                "Siz hali ro'yxatdan o'tmagansiz.\n" +
                        "Sahifam bo'limidan ro'yxatdan o'tishingiz mumkin"
            )
        }
    }

    private fun sendRequestToGetFavouriteStadiums() {
        val userId = sharedPref.getUserID(USER_ID, "")
        if (userId.isNotEmpty()) {
            // to get from server
            viewModel.getFavouriteStadiums(userId)
            observeFavouriteStadiums()
        } else {
            toast(
                "Siz hali ro'yxatdan o'tmagansiz.\n" +
                        "Sahifam bo'limidan ro'yxatdan o'tishingiz mumkin"
            )
        }
    }

    private fun sendRequestToGetNearbyStadiums() {
        viewModel.getNearByStadiums(
            Location(
                lastLocation!!.latitude,
                lastLocation!!.longitude
            )
        )
        observeNearByStadiums()
    }

    private fun observePreviouslyBookedStadiums() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.previouslyBookedStadiums.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        stadiumsList = it.data.data
                        refreshAdapter(favouriteStadiums, stadiumsList)
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

    private fun observeAllStadiums() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.allStadiums.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        findMultipleLocation(it.data.data)
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupUI: ${it.message}")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeFavouriteStadiumsList() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.favouriteStadiumsList.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        favouriteStadiums = it.data.data
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupUI: ${it.message}")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeNearByStadiums() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.nearByStadiums.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        stadiumsList = it.data.data
                        refreshAdapter(favouriteStadiums, stadiumsList)
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

    private fun observeSearchedStadium() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.searchedStadiums.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        refreshAdapter(favouriteStadiums, it.data.data)
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("TAG", "setupUI: ${it.message}")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeFavouriteStadiums() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.favouriteStadiums.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "observeNearByStadiums: $it.data")
                        stadiumsList = it.data.data
                        refreshAdapter(favouriteStadiums, stadiumsList)
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

    private fun observeAddFavouriteStadiums(
        stadiumId: String,
        ivBookmark: ImageView
    ) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.addToFavouriteStadiums.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        if (it.data.message == "add success") {
                            ivBookmark.setFavouriteBackground()
                            sendRequestToGetFavouriteStadiumsList()
                        }

                        if (it.data.message == "delete success") {
                            ivBookmark.setUnFavouriteBackground()
                            sendRequestToGetFavouriteStadiumsList()
                        }
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


    private fun openMyStadiumFragment() {
        findNavController().navigate(R.id.action_homeFragment_to_myStadiumFragment)
    }

    private fun refreshAdapter(
        favouriteStadiums: List<String>,
        stadiums: ArrayList<ShortStadiumDetail>
    ) {
        if (stadiums.isEmpty()) {
            binding.bottomSheetPitchList.tvNoStadiumAlert.visibility = View.VISIBLE
            binding.bottomSheetPitchList.rvPitch.visibility = View.GONE
            openPitchListBottomSheet()
            return
        } else {
            binding.bottomSheetPitchList.tvNoStadiumAlert.visibility = View.GONE
            binding.bottomSheetPitchList.rvPitch.visibility = View.VISIBLE
        }

        val adapter = PitchAdapter(favouriteStadiums, stadiums, object : OnClickEvent {
            override fun setOnBookClickListener(stadiumId: String, isFavourite: Boolean) {
                if (sharedPref.getLogIn(KeyValues.LOG_IN, false)) {
                    openPitchDetailFragment(stadiumId, isFavourite)
                } else {
                    showSignUpDialog()
                }
            }

            override fun setOnNavigateClickListener(latitude: Double, longitude: Double) {
                requireActivity().shareLocationToGoogleMap(latitude, longitude)
            }

            override fun setOnBookMarkClickListener(
                stadiumId: String,
                ivBookmark: ImageView,
            ) {
                sendRequestToAddFavouriteStadiums(stadiumId)
                observeAddFavouriteStadiums(stadiumId, ivBookmark)
            }
        })
        binding.bottomSheetPitchList.rvPitch.adapter = adapter
        showStadiumList()
    }

    private fun showSignUpDialog() {
        enterAccountDialog = EnterAccountDialog(requireContext()) {
            openSignInFragment()
            enterAccountDialog.dismiss()
        }.instance(
            LayoutEnterDialogBinding.inflate(
                LayoutInflater.from(requireContext())
            ).root
        )
        enterAccountDialog.show()
    }

    private fun openSignInFragment() {
        findNavController().navigate(R.id.action_homeFragment_to_signInFragment)
    }

    private fun openPitchDetailFragment(stadiumId: String, isFavourite: Boolean) {
        findNavController().navigate(
            R.id.action_homeFragment_to_pitchDetailFragment,
            bundleOf(STADIUM_ID to stadiumId, IS_FAVOURITE_STADIUM to isFavourite)
        )
    }

    private fun showStadiumList() {
        openPitchListBottomSheet()
        openTopSheet()
    }

    private fun openTopSheet() {
        showTopSheet()

        topSheetBehavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float, isOpening: Boolean?) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == TopSheetBehavior.STATE_DRAGGING) {
                    topSheetBehavior.state = TopSheetBehavior.STATE_EXPANDED
                }
            }
        })

        binding.topSheet.ivBack.setOnClickListener {
            topSheetBehavior.isHideable = true
            hideTopSheet()
            hideBottomSheet(bottomSheetBehavior)
            showBottomSheet(bottomSheetBehaviorType)
        }
    }

    private fun openPitchListBottomSheet() {
        setLightStatusBar()
        showBottomSheet(bottomSheetBehavior)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.bottomSheetPitchList.bottomSheetPitchList.setBackgroundResource(R.drawable.linear_top_smooth_background)
                } else {
                    binding.bottomSheetPitchList.bottomSheetPitchList.setBackgroundResource(R.drawable.linear_top_rounded_background)
                }

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    showTopSheet()
                }

                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    showBottomSheet(bottomSheetBehaviorType)
                    bottomSheetBehaviorType.isHideable = false
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun hideBottomSheet(bottomSheetBehavior: BottomSheetBehavior<View>) {
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showBottomSheet(bottomSheetBehavior: BottomSheetBehavior<View>) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideTopSheet() {
        topSheetBehavior.state = TopSheetBehavior.STATE_HIDDEN
    }

    private fun showTopSheet() {
        topSheetBehavior.state = TopSheetBehavior.STATE_EXPANDED
    }

    private fun setLightStatusBar() {
        //setting light mode status bar
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val view = View(requireContext())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags: Int = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            requireActivity().window.statusBarColor = Color.WHITE
        }
    }

    private fun findMultipleLocation(stadiumLocationList: ArrayList<StadiumLocationName>) {
        for (i in stadiumLocationList) {
            val myMarker = map.addMarker(
                MarkerOptions().position(LatLng(i.latitude, i.longitude))
                    .title(i.name)
                    .icon(bitmapFromVector(R.drawable.ic_locate_stadium))
            )
            markerList.add(myMarker!!)
        }
    }

    private fun bitmapFromVector(vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId)
        vectorDrawable!!.setBounds(
            0, 0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}