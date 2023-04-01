package com.telakuR.easyorder.home.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.ui.screens.NotificationsScreen
import com.telakuR.easyorder.home.ui.screens.ProfileScreen
import com.telakuR.easyorder.home.ui.screens.UserHomeScreen
import com.telakuR.easyorder.home.ui.screens.companyView.RequestsScreen
import com.telakuR.easyorder.home.ui.screens.employeeView.ChooseFastFoodScreen
import com.telakuR.easyorder.home.ui.screens.employeeView.ChooseFoodScreen
import com.telakuR.easyorder.home.ui.screens.employeeView.MyOrderScreen
import com.telakuR.easyorder.home.ui.screens.employeeView.MyOrdersScreen

const val FAST_FOOD_NAME: String = "fastFoodName"
const val ORDER_ID: String = "orderId"

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

        composable(route = HomeRoute.OrderDetails.route) {
            MyOrderScreen(navController = navController)
        }

        composable(route = HomeRoute.Orders.route) {
            MyOrdersScreen(navController = navController)
        }

        composable(route = HomeRoute.Notification.route) {
            NotificationsScreen()
        }

        composable(route = HomeRoute.OrderDetails.route) {
//            OrderDetails()
        }

        composable(route = HomeRoute.ChooseFastFood.route) {
            ChooseFastFoodScreen(navController = navController)
        }

        composable(route = HomeRoute.ChooseFood.route + "/{$FAST_FOOD_NAME}/{$ORDER_ID}?") {
            ChooseFoodScreen(
                navController = navController,
                fastFoodName = it.arguments?.getString(FAST_FOOD_NAME) ?: "",
                orderId = it.arguments?.getString(ORDER_ID) ?: ""
            )
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



