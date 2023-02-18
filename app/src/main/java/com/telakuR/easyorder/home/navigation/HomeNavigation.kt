package com.telakuR.easyorder.home.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.ui.screens.HomeScreen
import com.telakuR.easyorder.home.ui.screens.NotificationScreen
import com.telakuR.easyorder.home.ui.screens.ProfileScreen
import com.telakuR.easyorder.home.ui.screens.ReportScreen

@Composable
fun HomeNavigation(navController: NavHostController) {
    NavHost(
    navController = navController,
    startDestination = HomeRoute.Home.route,
    ) {
        composable(route = HomeRoute.Home.route) {
            HomeScreen()
        }

        composable(route = HomeRoute.Profile.route) {
            ProfileScreen()
        }

        composable(route = HomeRoute.Report.route) {
            ReportScreen()
        }

        composable(route = HomeRoute.Order.route) {
            OrderScreen()
        }

        composable(route = HomeRoute.Notification.route) {
            NotificationScreen()
        }

        composable(route = HomeRoute.OrderDetails.route) {
            OrderDetails()
        }

        composable(route = HomeRoute.ChooseFastFood.route) {
            ChooseFastFood()
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
fun ChooseFastFood() {

}

@Composable
fun OrderDetails() {

}

@Composable
fun OrderScreen() {

}
