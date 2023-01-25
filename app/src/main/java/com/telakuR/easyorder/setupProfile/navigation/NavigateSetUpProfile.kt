package com.telakuR.easyorder.setupProfile.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.telakuR.easyorder.setupProfile.models.SetUpProfileRoute
import com.telakuR.easyorder.setupProfile.ui.views.FindYourCompanyScreen
import com.telakuR.easyorder.setupProfile.ui.views.PicturePreviewScreen
import com.telakuR.easyorder.setupProfile.ui.views.SelectPictureScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigateSetUpProfile() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SetUpProfileRoute.SelectPicture.route,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
        composable(route = SetUpProfileRoute.SelectPicture.route) {
            SelectPictureScreen(navController = navController)
        }

        composable(route = SetUpProfileRoute.PicturePreview.route) {
            PicturePreviewScreen(navController = navController)
        }

        composable(route = SetUpProfileRoute.FindYourCompany.route) {
            FindYourCompanyScreen(navController)
        }
    }
}
