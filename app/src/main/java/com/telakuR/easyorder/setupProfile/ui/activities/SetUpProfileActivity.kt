package com.telakuR.easyorder.setupProfile.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.telakuR.easyorder.setupProfile.navigation.NavigateSetUpProfile
import com.telakuR.easyorder.setupProfile.viewModels.SetUpProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class SetUpProfileActivity : ComponentActivity() {

    val setUpProfileViewModel: SetUpProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigateSetUpProfile()
        }
    }
}

