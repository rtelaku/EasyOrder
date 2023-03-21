package com.telakuR.easyorder.home.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.ui.screens.*

@Composable
fun HomeNavigation(navController: NavHostController, role: String) {
    NavHost(
    navController = navController,
    startDestination = HomeRoute.Home.route,
    ) {
        composable(route = HomeRoute.Home.route) {
            UserHomeScreen(navController = navController, role = role)
        }

        composable(route = HomeRoute.Profile.route) {
            ProfileScreen()
        }

        composable(route = HomeRoute.Requests.route) {
            RequestsScreen()
        }

        composable(route = HomeRoute.Order.route) {
            OrderScreen()
        }

        composable(route = HomeRoute.Notification.route) {
            NotificationsScreen()
        }

        composable(route = HomeRoute.OrderDetails.route) {
            OrderDetails()
        }

        composable(route = HomeRoute.ChooseFastFood.route) {
            ChooseFastFoodScreen(navController = navController)
        }

        composable(route = HomeRoute.ChooseFood.route) {
            ChooseFood()
        }

        composable(route = HomeRoute.GroupedOrders.route) {
            GroupedOrders()
        }

        composable(route = HomeRoute.PaymentDetails.route) {
            PaymentDetails()
        }
    }
}

@Composable
fun PaymentDetails() {

}

@Composable
fun GroupedOrders() {

}

@Composable
fun ChooseFood() {

}

@Composable
fun OrderDetails() {

}

@Composable
fun OrderScreen() {

}
