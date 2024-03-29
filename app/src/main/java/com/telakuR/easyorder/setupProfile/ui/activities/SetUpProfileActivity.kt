package com.telakuR.easyorder.setupProfile.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.telakuR.easyorder.setupProfile.navigation.NavigateSetUpProfile
import com.telakuR.easyorder.setupProfile.route.SetUpProfileRoute
import com.telakuR.easyorder.setupProfile.viewModel.SetUpPictureVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetUpProfileActivity : ComponentActivity() {

    private val setUpPictureVM: SetUpPictureVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val shouldShowCompanyScreen = setUpPictureVM.shouldShowFindCompanyScreen.collectAsStateWithLifecycle().value

            val startDestination = if(shouldShowCompanyScreen == true) {
                SetUpProfileRoute.FindYourCompany.route
            } else {
                SetUpProfileRoute.SelectPicture.route
            }

            NavigateSetUpProfile(startDestination, true)
        }
    }
}

