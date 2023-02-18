package com.telakuR.easyorder.home.ui

import HomeView
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.telakuR.easyorder.setupProfile.ui.activities.SetUpProfileActivity
import com.telakuR.easyorder.viewModels.HomeVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity: ComponentActivity() {

    private val viewModel: HomeVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.shouldLaunchSetupScreen.collect { shouldLaunch ->
                if (shouldLaunch)
                    startActivity(Intent(this@HomeActivity, SetUpProfileActivity::class.java))
            }
        }

        setContent {
            HomeView()
        }
    }
}
