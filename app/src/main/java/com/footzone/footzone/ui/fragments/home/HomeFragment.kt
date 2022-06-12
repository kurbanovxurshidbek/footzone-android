package com.footzone.footzone.ui.fragments.home

import android.app.Activity
import android.content.IntentSender
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.androidbolts.topsheet.TopSheetBehavior
import com.footzone.footzone.R
import com.footzone.footzone.adapter.PitchAdapter
import com.footzone.footzone.databinding.FragmentHomeBinding
import com.footzone.footzone.helper.OnClickEvent
import com.footzone.footzone.model.*
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.*
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues.IS_OWNER
import com.footzone.footzone.utils.KeyValues.STADIUM_ID
import com.footzone.footzone.utils.KeyValues.USER_ID
import com.footzone.footzone.utils.commonfunction.Functions.resRating
import com.footzone.footzone.utils.commonfunction.Functions.setFavouriteBackground
import com.footzone.footzone.utils.commonfunction.Functions.setUnFavouriteBackground
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home), OnMapReadyCallback,
    GoogleMap.CancelableCallback {

    private val viewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var sharedPref: SharedPref

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentHomeBinding
    private var isFirstTime = true
    private lateinit var bottomSheet: View
    private lateinit var topSheet: View
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetBehaviorType: BottomSheetBehavior<View>
    private lateinit var topSheetBehavior: TopSheetBehavior<View>
    private var stadiumsList = ArrayList<ShortStadiumDetail>()
    private var stadiumsFilteredList = ArrayList<ShortStadiumDetail>()
    private var favouriteStadiums = ArrayList<String>()

    private var lastLocation: Location? = null
    private val myLocationZoom = 10.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sendRequestToGetAllStadiums()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        initViews(view)
        observeAllStadiums()
        sendRequestToGetFavouriteStadiumsList()
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(41.2795325, 69.2143852),
                myLocationZoom
            )
        )
        mMap.setPadding(0, 0, 0, 500)

        mMap.setOnCameraMoveStartedListener {
            //todo start animate marker
            binding.mapIcon.animate().setDuration(300).translationY(-binding.mapIcon.height / 2.0f)
                .start()
            hideBottomSheet(bottomSheetBehaviorType)
            hideBottomSheet(bottomSheetBehavior)
        }

        mMap.setOnCameraIdleListener {
            binding.mapIcon.animate().translationY(0f).setDuration(300).start()

            val target = mMap.cameraPosition.target
            showBottomSheet(bottomSheetBehaviorType)
            bottomSheetBehaviorType.isHideable = false
            //todo send request for nearby stadions
        }

        fun updateMyCurrentLocation() {
            try {
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            lastLocation!!.latitude,
                            lastLocation!!.longitude
                        ), myLocationZoom
                    )
                )
            } catch (e: Exception) {
            }
        }

        binding.findMyLocation.setOnClickListener {
            showLocationOn()
            updateMyCurrentLocation()
        }
    }

    override fun onCancel() {
        TODO("Not yet implemented")
    }

    override fun onFinish() {
        TODO("Not yet implemented")
    }

    private fun initViews(view: View) {
        showLocationOn()
        controlOwnerOption()
        val bottomSheetTypes = view.findViewById<View>(R.id.bottomSheetTypes)
        bottomSheet = view.findViewById(R.id.bottomSheetPitchList)
        topSheet = view.findViewById(R.id.topSheet)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        topSheetBehavior = TopSheetBehavior.from(topSheet)
        bottomSheetBehaviorType = BottomSheetBehavior.from(bottomSheetTypes)

        showBottomSheet(bottomSheetBehaviorType)

        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

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
            findNavController().navigate(R.id.userNotificationFragment)
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

    private fun controlOwnerOption() {
        if (sharedPref.getIsOwner(IS_OWNER)) {
            binding.bottomSheetTypes.linearMyStadium.visibility = View.VISIBLE
        } else {
            binding.bottomSheetTypes.linearMyStadium.visibility = View.GONE
        }
    }

    private fun sendRequestToGetAllStadiums() {
        viewModel.getAllStadiums();
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
        val userId = sharedPref.getUserID(USER_ID, "")
        if (userId.isNotEmpty()) {
            viewModel.getPreviouslyBookedStadiums(userId)
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
                41.4577,
                69.3477
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

    private fun observeAllStadiums() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.allStadiums.collect {
                when (it) {
                    UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        Log.d("TAG", "observeNearByStadiums: $it.data")

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
            override fun setOnBookClickListener(stadiumId: String) {
                openPitchDetailFragment(stadiumId)
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

    private fun openPitchDetailFragment(stadiumId: String) {
        findNavController().navigate(
            R.id.action_homeFragment_to_pitchDetailFragment,
            bundleOf(STADIUM_ID to stadiumId)
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

    private fun setUpCamera() {
        val locationHelper = LocationHelper()
        locationHelper.startLocationUpdates(requireContext())
        locationHelper.addLocationlistener(object : LocationHelper.LocationListener {
            override fun onLocationChanged(location: Location) {
                if (isFirstTime) {
                    val target = LatLng(location.latitude, location.longitude)
                    val cameraUpdate = CameraPosition.Builder().target(target).zoom(16f).build()
                    mMap.moveCamera(
                        CameraUpdateFactory.newCameraPosition(cameraUpdate)
                    )
                    lastLocation = location
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

    private fun showLocationOn() {
        val locationRequest = LocationRequest.create()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 30 * 1000.toLong()
            fastestInterval = 5 * 1000.toLong()
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result =
            LocationServices.getSettingsClient(requireActivity())
                .checkLocationSettings(builder.build())
        result.addOnCompleteListener {
            try {
                val response: LocationSettingsResponse = it.getResult(ApiException::class.java)
                if (response.locationSettingsStates!!.isGpsPresent)
                    Log.d("@@@", "ERROR")
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(e.status.resolution!!).build()
                        launcher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                    }
                }
            }
        }
    }

    private var launcher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setUpCamera()
            } else {
                showLocationOn()
            }
        }

    private fun findMultipleLocation(stadiumLocationList: ArrayList<StadiumLocationName>) {
        for (i in stadiumLocationList) {
//            mMap.addMarker(MarkerOptions().position(LatLng(i.latitude, i.longitude)).title(i.name))
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f))
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(i.latitude, i.longitude)))
        }
    }
}