package com.telakuR.easyorder.authentication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.telakuR.easyorder.authentication.utils.Constants
import com.telakuR.easyorder.authentication.ui.views.*
import com.telakuR.easyorder.ui.theme.Route

@Composable
fun AuthenticationNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.Login.route) {
        composable(route = Route.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(
            Route.SignUp.route+"/{${Constants.CHOSEN_ROLE}}",
            arguments = listOf(navArgument(Constants.CHOSEN_ROLE) { type = NavType.StringType })
        ) { backStackEntry ->
            SignUpScreen(navController, backStackEntry.arguments?.getString(Constants.CHOSEN_ROLE))
        }

        composable(route = Route.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }

        composable(route = Route.ResetPassword.route) {
            ResetPasswordScreen(navController)
        }

        composable(route = Route.ChooseRole.route) {
            ChooseRole(navController)
        }
    }
}

