package com.telakuR.easyorder.main.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.telakuR.easyorder.authentication.models.AuthenticationRoute
import com.telakuR.easyorder.authentication.ui.activities.AuthenticationActivity
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.ui.activities.HomeActivity
import com.telakuR.easyorder.main.viewmodel.MainVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            viewModel.screenToLaunch.collect { screenToLaunch ->
                if(screenToLaunch == HomeRoute.Home.route) {
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                } else if(screenToLaunch == AuthenticationRoute.Login.route) {
                    startActivity(Intent(this@MainActivity, AuthenticationActivity::class.java))
                }
            }
        }
    }
}
