package com.telakuR.easyorder.setupProfile.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
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
            val shouldShowCompanyScreen = setUpPictureVM.shouldShowFindCompanyScreen.collectAsState().value

            val startDestination = if(shouldShowCompanyScreen) {
                SetUpProfileRoute.FindYourCompany.route
            } else {
                SetUpProfileRoute.SelectPicture.route
            }

            NavigateSetUpProfile(startDestination, true)
        }
    }
}

