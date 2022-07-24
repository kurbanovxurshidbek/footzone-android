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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.androidbolts.topsheet.TopSheetBehavior
import com.directions.route.*
import com.footzone.footzone.R
import com.footzone.footzone.adapter.PitchAdapter
import com.footzone.footzone.databinding.FragmentHomeBinding
import com.footzone.footzone.databinding.ItemSingleStadiumDataBinding
import com.footzone.footzone.databinding.LayoutAcceptBinding
import com.footzone.footzone.databinding.LayoutEnterDialogBinding
import com.footzone.footzone.helper.OnClickEvent
import com.footzone.footzone.model.*
import com.footzone.footzone.ui.activity.MainActivity
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.*
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues.ADD_SUCCESS
import com.footzone.footzone.utils.KeyValues.DELETE_SUCCESS
import com.footzone.footzone.utils.KeyValues.IS_FAVOURITE_STADIUM
import com.footzone.footzone.utils.KeyValues.IS_OWNER
import com.footzone.footzone.utils.KeyValues.KEY
import com.footzone.footzone.utils.KeyValues.LOG_IN
import com.footzone.footzone.utils.KeyValues.STADIUM_ID
import com.footzone.footzone.utils.KeyValues.USER_ID
import com.footzone.footzone.utils.commonfunction.Functions.resRating
import com.footzone.footzone.utils.commonfunction.Functions.setFavouriteBackground
import com.footzone.footzone.utils.commonfunction.Functions.setUnFavouriteBackground
import com.footzone.footzone.utils.extensions.hide
import com.footzone.footzone.utils.extensions.show
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.lang.Exception
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
    private var favouriteStadiums = ArrayList<String>()
    private lateinit var enterAccountDialog: EnterAccountDialog
    protected lateinit var singleStadiumDialog: SingleStadiumDialog

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null
    private val myLocationZoom = 12.4f
    private var markerList = ArrayList<Marker>()
    private var cameraCurrentLatLng: LatLng? = null
    private var polyLines: MutableList<Polyline>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        observeAllStadiums()
        initViews(view)
        installLocation()
        sendRequestToGetFavouriteStadiumsList()
        sendRequestToDetectNotification()

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
        sendRequestToGetAllStadiums()
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
        marker.showInfoWindow()
        sendRequestToGetSingleStadiumData(marker.tag.toString())
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
                .key(KEY)
                .build()
            routing.execute()
            if (cameraCurrentLatLng != null)
                12.4f.animateCamera(
                    LatLng(
                        cameraCurrentLatLng!!.latitude,
                        cameraCurrentLatLng!!.longitude
                    )
                ) else
                12.4f.animateCamera(LatLng(lastLocation!!.latitude, lastLocation!!.longitude))
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
            try {
                updateLastLocation()
            } catch (e: Exception) {

            }
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
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(requireContext())
                    toast(getString(R.string.str_error_get_location))
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

        binding.bottomSheetPitchList.rvPitch.edgeEffectFactory = BounceEdgeEffectFactory()

        showBottomSheet(bottomSheetBehaviorType)

        if (sharedPref.getLogIn(LOG_IN, false)) {
            viewModel.detectIsNotificationAvailable()
            observeNotificationAvailability()
        }

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

        var isOpenFilter = false
        var isWellRated = false
        binding.topSheet.apply {
            tvCurrentlyOpen.setOnClickListener {
                isOpenFilter = !isOpenFilter
                changeBackground(isOpenFilter, binding.topSheet.tvCurrentlyOpen)
                controlFilter(isOpenFilter, isWellRated)
            }
            tvWellCommented.setOnClickListener {
                isWellRated = !isWellRated
                changeBackground(isWellRated, binding.topSheet.tvWellCommented)
                controlFilter(isOpenFilter, isWellRated)
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
                    sendRequestToGetSearchedStadiums(
                        binding.bottomSheetTypes.edtPitchSearch.text.toString().trim()
                    )
                } else {
                    bottomSheetBehaviorType.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                true
            } else false
        }
    }

    private fun controlFilter(openFilter: Boolean, wellRated: Boolean) {
        if (openFilter && wellRated) {
            refreshAdapter(favouriteStadiums, filterWellRated(filterOpen(stadiumsList)))
        }
        if (openFilter && !wellRated) {
            refreshAdapter(favouriteStadiums, filterOpen(stadiumsList))
        }
        if (!openFilter && wellRated) {
            refreshAdapter(favouriteStadiums, filterWellRated(stadiumsList))
        }
        if (!openFilter && !wellRated) {
            refreshAdapter(favouriteStadiums, stadiumsList)
        }
    }

    private fun filterWellRated(stadiumsList: ArrayList<ShortStadiumDetail>): ArrayList<ShortStadiumDetail> {
        return ArrayList<ShortStadiumDetail>().apply {
            stadiumsList.forEach {
                if (resRating(it.comments) > 2) {
                    this.add(it)
                }
            }
        }
    }

    private fun filterOpen(stadiumsList: ArrayList<ShortStadiumDetail>): ArrayList<ShortStadiumDetail> {
        return ArrayList<ShortStadiumDetail>().apply {
            stadiumsList.forEach {
                if (it.isOpen.open) {
                    this.add(it)
                }
            }
        }
    }

    private fun changeBackground(isFiltered: Boolean, tv: TextView) {
        val checked = requireContext().resources.getDrawable(R.drawable.ic_checked)
        if (isFiltered) {
            tv.apply {
                setBackgroundResource(R.drawable.textview_rounded_background_blue)
                setCompoundDrawablesWithIntrinsicBounds(checked, null, null, null)
            }
        } else {
            tv.apply {
                setBackgroundResource(R.drawable.textview_rounded_background)
                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
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
            binding.bottomSheetTypes.linearMyStadium.show()
        } else {
            binding.bottomSheetTypes.linearMyStadium.hide()
        }
    }

    private fun sendRequestToDetectNotification() {
        viewModel.detectIsNotificationAvailable()
    }

    private fun sendRequestToGetAllStadiums() {
        viewModel.getAllStadiums()
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

    private fun sendRequestToGetSingleStadiumData(stadiumId: String) {
        viewModel.getSingleStadiumData(stadiumId)
        observeSingleStadium()
    }

    private fun sendRequestToGetPreviouslyBookedStadiums() {
        if (sharedPref.getUserID(USER_ID, "").isNotEmpty()) {
            viewModel.getPreviouslyBookedStadiums()
            observePreviouslyBookedStadiums()
        } else {
            notRegisteredToast()
        }
    }

    private fun notRegisteredToast() {
        toast(
            getString(R.string.str_not_registered_yet)
        )
    }

    private fun sendRequestToAddFavouriteStadiums(stadiumId: String) {
        val userID = sharedPref.getUserID(USER_ID, "")
        if (userID.isNotEmpty()) {
            val favouriteStadiumRequest =
                FavouriteStadiumRequest(stadiumId, userID)
            viewModel.addToFavouriteStadiums(favouriteStadiumRequest)
        } else {
            notRegisteredToast()
        }
    }

    private fun sendRequestToGetFavouriteStadiums() {
        val userId = sharedPref.getUserID(USER_ID, "")
        if (userId.isNotEmpty()) {
            // to get from server
            viewModel.getFavouriteStadiums(userId)
            observeFavouriteStadiums()
        } else {
            notRegisteredToast()
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.previouslyBookedStadiums.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            stadiumsList = it.data.data
                            refreshAdapter(favouriteStadiums, stadiumsList)
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

    private fun observeAllStadiums() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allStadiums.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            //show progress
                        }

                        is UiStateObject.SUCCESS -> {
                            findMultipleLocation(it.data.data)
                        }
                        is UiStateObject.ERROR -> {
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun observeSingleStadium() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.singleStadiumData.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            showSingleStadiumData(it.data.data)
                        }
                        is UiStateObject.ERROR -> {
                            hideProgress()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun observeFavouriteStadiumsList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favouriteStadiumsList.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            //show progress
                        }

                        is UiStateObject.SUCCESS -> {
                            favouriteStadiums = it.data.data
                        }
                        is UiStateObject.ERROR -> {
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun observeNearByStadiums() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nearByStadiums.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            stadiumsList = it.data.data
                            refreshAdapter(favouriteStadiums, stadiumsList)
                            viewModel.reset()
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

    private fun observeSearchedStadium() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchedStadiums.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            stadiumsList = it.data.data
                            refreshAdapter(favouriteStadiums, stadiumsList)
                        }
                        is UiStateObject.ERROR -> {
                            hideProgress()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun observeFavouriteStadiums() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favouriteStadiums.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            showProgress()
                        }

                        is UiStateObject.SUCCESS -> {
                            hideProgress()
                            stadiumsList = it.data.data
                            refreshAdapter(favouriteStadiums, stadiumsList)
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

    private fun observeNotificationAvailability() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notification.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            //show progress
                        }

                        is UiStateObject.SUCCESS -> {
                            if (it.data.data) {
                                binding.ivNewNotification.show()
                            } else {
                                binding.ivNewNotification.hide()
                            }
                        }
                        is UiStateObject.ERROR -> {
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun observeAddFavouriteStadiums(
        stadiumId: String,
        ivBookmark: ImageView
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addToFavouriteStadiums.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            //show progress
                        }

                        is UiStateObject.SUCCESS -> {
                            if (it.data.message == ADD_SUCCESS) {
                                ivBookmark.setFavouriteBackground()
                                sendRequestToGetFavouriteStadiumsList()
                            }

                            if (it.data.message == DELETE_SUCCESS) {
                                ivBookmark.setUnFavouriteBackground()
                                sendRequestToGetFavouriteStadiumsList()
                            }
                        }
                        is UiStateObject.ERROR -> {
                        }
                        else -> {
                        }
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
            binding.bottomSheetPitchList.tvNoStadiumAlert.show()
            binding.bottomSheetPitchList.rvPitch.hide()
            openPitchListBottomSheet()
            return
        } else {
            binding.bottomSheetPitchList.tvNoStadiumAlert.hide()
            binding.bottomSheetPitchList.rvPitch.show()
        }

        val adapter = PitchAdapter(favouriteStadiums, stadiums, object : OnClickEvent {
            override fun setOnBookClickListener(stadiumId: String, isFavourite: Boolean) {
                checkIsLogIn(stadiumId, isFavourite)
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

    private fun checkIsLogIn(stadiumId: String, isFavourite: Boolean) {
        if (sharedPref.getLogIn(LOG_IN, false)) {
            try {
                singleStadiumDialog.dismiss()
            } catch (e: Exception) {
            }
            openPitchDetailFragment(stadiumId, isFavourite)
        } else {
            showSignUpDialog()
        }
    }

    private fun showSignUpDialog() {
        enterAccountDialog = EnterAccountDialog(requireContext()) {
            openSignInFragment()
            enterAccountDialog.dismiss()
            try {
                singleStadiumDialog.dismiss()
            } catch (e: Exception) {
            }
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

                if (newState == BottomSheetBehavior.STATE_COLLAPSED && stadiumsList.isNotEmpty()) {
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
            myMarker!!.tag = i.stadiumId
            markerList.add(myMarker)
        }
    }

    private fun showSingleStadiumData(stadiumDetail: ShortStadiumDetail) {
        singleStadiumDialog = SingleStadiumDialog(
            favouriteStadiums.contains(stadiumDetail.stadiumId),
            stadiumDetail,
            requireContext(),
            object : OnClickEvent {
                override fun setOnBookClickListener(stadiumId: String, isFavourite: Boolean) {
                    checkIsLogIn(stadiumId, isFavourite)
                }

                override fun setOnNavigateClickListener(latitude: Double, longitude: Double) {
                    requireActivity().shareLocationToGoogleMap(latitude, longitude)
                }

                override fun setOnBookMarkClickListener(stadiumId: String, ivBookmark: ImageView) {
                    sendRequestToAddFavouriteStadiums(stadiumId)
                    observeAddFavouriteStadiums(stadiumId, ivBookmark)
                }
            }).instance(ItemSingleStadiumDataBinding.inflate(LayoutInflater.from(requireContext())))
        singleStadiumDialog.show()
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