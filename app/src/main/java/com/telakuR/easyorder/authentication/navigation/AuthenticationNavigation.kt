package com.telakuR.easyorder.authentication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.telakuR.easyorder.authentication.models.AuthenticationRoute
import com.telakuR.easyorder.authentication.ui.views.ForgotPasswordScreen
import com.telakuR.easyorder.authentication.ui.views.LoginScreen
import com.telakuR.easyorder.authentication.ui.views.SignUpScreen

@Composable
fun AuthenticationNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthenticationRoute.Login.route,
    ) {
        composable(route = AuthenticationRoute.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(route = AuthenticationRoute.SignUp.route) {
            SignUpScreen(navController)
        }

        composable(route = AuthenticationRoute.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }
    }
}

