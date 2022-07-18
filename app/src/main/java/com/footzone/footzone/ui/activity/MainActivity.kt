package com.footzone.footzone.ui.activity

import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.footzone.footzone.R
import com.footzone.footzone.broadcast.InternetBroadcastReceiver
import com.footzone.footzone.databinding.ActivityMainBinding
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.IS_OWNER
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        binding.bottomNavigationView.setupWithNavController(navController)
        setupUI()
        if (intent.getStringExtra(KeyValues.NOTIFICATION_TITLE) != null)
            navigate(intent)

        checkInternet()
    }

    private fun navigate(intent: Intent?) {
        if (intent != null) {
            if (sharedPref.getIsOwner(IS_OWNER)) {
                navController.navigate(R.id.adminNotificationFragment)
            } else {
                navController.navigate(R.id.userNotificationFragment)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigate(intent)
    }

    private fun setupUI() {
        showAndHideBottomNavView()
        binding.bottomNavigationView.itemIconTintList = null
        binding.bottomNavigationView.itemRippleColor =
            ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
        loadFCMToken()
    }

    private fun showContentBehindStatusBar() {
        //show content behind status bar
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //make status bar light
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun showAndHideBottomNavView() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    showContentBehindStatusBar()
                    showBottomNav()
                }
                R.id.tableFragment -> {
                    showContentBehindStatusBar()
                    showBottomNav()
                }
                R.id.profileFragment -> {
                    showBottomNav()
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                else -> hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        binding.bottomNavigationView.visibility = View.VISIBLE

    }

    private fun hideBottomNav() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    private fun checkInternet() {
        val internetBroadcastReceiver = InternetBroadcastReceiver()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

        internetBroadcastReceiver.onInternetOff = {
            showToast(getString(R.string.str_internet_off), Toast.LENGTH_LONG)
        }

        internetBroadcastReceiver.onInternetOn = {
            showToast(getString(R.string.str_internet_on), Toast.LENGTH_LONG)
        }

        registerReceiver(
            internetBroadcastReceiver,
            intentFilter
        )
    }
}