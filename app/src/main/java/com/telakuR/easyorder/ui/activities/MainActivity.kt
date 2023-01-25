package com.telakuR.easyorder.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.telakuR.easyorder.authentication.models.AuthenticationRoute
import com.telakuR.easyorder.authentication.ui.activities.AuthenticationActivity
import com.telakuR.easyorder.models.UserRoute
import com.telakuR.easyorder.viewModels.MainVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainVM by viewModels()

    @Inject
    lateinit var firestore:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            viewModel.screenToLaunch.collect { screenToLaunch ->
                if(screenToLaunch == UserRoute.Home.route) {
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                } else if(screenToLaunch == AuthenticationRoute.Login.route) {
                    startActivity(Intent(this@MainActivity, AuthenticationActivity::class.java))
                }
            }
        }
    }
}
