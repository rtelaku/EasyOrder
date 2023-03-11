package com.telakuR.easyorder.home.ui

import HomeView
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.telakuR.easyorder.home.viewModel.HomeVM
import com.telakuR.easyorder.setupProfile.ui.activities.SetUpProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity: ComponentActivity() {

    private val viewModel: HomeVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            HomeView()
        }
    }
}
