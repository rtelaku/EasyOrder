package com.telakuR.easyorder.authentication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.telakuR.easyorder.authentication.ui.LoginScreen
import com.telakuR.easyorder.ui.theme.Route

@Composable
fun AuthenticationNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.Login.route) {
        composable(route = Route.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(route = Route.SignUp.route) {
//            signupScreen()
        }
    }
}
