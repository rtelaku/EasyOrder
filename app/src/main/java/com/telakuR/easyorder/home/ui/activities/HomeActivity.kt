package com.telakuR.easyorder.home.ui.activities

import BaseHomeScreen
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.viewModel.HomeVM
import com.telakuR.easyorder.setupProfile.ui.activities.SetUpProfileActivity
import com.telakuR.easyorder.utils.NetworkCallback
import com.telakuR.easyorder.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity: ComponentActivity(), NetworkCallback {

    private val viewModel: HomeVM by viewModels()
    private lateinit var alertDialog: AlertDialog
    private lateinit var networkUtils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alertDialog = AlertDialog.Builder(this, R.style.NetworkStatusView)
            .setView(R.layout.no_internet_connection_view).create()

        alertDialog.window?.setGravity(Gravity.TOP)

        networkUtils = NetworkUtils(this)
        networkUtils.setNetworkCallback(this)
        networkUtils.registerConnectivityCallback()

        lifecycleScope.launch {
            viewModel.showSetupProfile.collect { show ->
                if (show) {
                    val intent = Intent(this@HomeActivity, SetUpProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

        setContent {
            BaseHomeScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        if(!networkUtils.isInternetConnected()) {
            alertDialog.show()
        }
    }

    override fun onInternetConnected() {
        setNetworkStatusViewVisibility(shouldShow = false)
    }

    override fun onInternetDisconnected() {
        setNetworkStatusViewVisibility(shouldShow = true)
    }

    private fun setNetworkStatusViewVisibility(shouldShow: Boolean) {
        if(shouldShow) alertDialog.show() else alertDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        networkUtils.removeNetworkCallback()
        networkUtils.unregisterConnectivityCallback()
    }
}
