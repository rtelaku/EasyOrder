package com.telakuR.easyorder.home.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.ui.screens.NotificationsScreen
import com.telakuR.easyorder.home.ui.screens.ProfileScreen
import com.telakuR.easyorder.home.ui.screens.UserHomeScreen
import com.telakuR.easyorder.home.ui.screens.companyView.RequestsScreen
import com.telakuR.easyorder.home.ui.screens.employeeView.*
import com.telakuR.easyorder.utils.Constants.EMPLOYEE_ID
import com.telakuR.easyorder.utils.Constants.FAST_FOOD_NAME
import com.telakuR.easyorder.utils.Constants.ORDER_ID

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

        composable(route = HomeRoute.OrderDetails.route + "/{$ORDER_ID}/{$EMPLOYEE_ID}?") {
            OrderDetailsScreen(
                navController = navController,
                orderId = it.arguments?.getString(ORDER_ID) ?: "",
                employeeId = it.arguments?.getString(EMPLOYEE_ID) ?: ""
            )
        }

        composable(route = HomeRoute.Orders.route) {
            MyOrdersScreen(navController = navController)
        }

        composable(route = HomeRoute.Notification.route) {
            NotificationsScreen()
        }

        composable(route = HomeRoute.ChooseFastFood.route) {
            ChooseFastFoodScreen(navController = navController)
        }

        composable(
            route = HomeRoute.ChooseFood.route + "/?$FAST_FOOD_NAME={${FAST_FOOD_NAME}}&$ORDER_ID={${ORDER_ID}}",
            arguments = listOf(
                navArgument(FAST_FOOD_NAME) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(ORDER_ID) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            ChooseFoodScreen(
                navController = navController,
                fastFoodName = it.arguments?.getString(FAST_FOOD_NAME) ?: "",
                orderId = it.arguments?.getString(ORDER_ID) ?: ""
            )
        }

        composable(route = HomeRoute.GroupedOrders.route + "/{$ORDER_ID}") {
            GroupedOrdersScreen(orderId = it.arguments?.getString(ORDER_ID) ?: "")
        }

        composable(route = HomeRoute.PaymentDetails.route + "/{$ORDER_ID}") {
            PaymentDetailsScreen(orderId = it.arguments?.getString(ORDER_ID) ?: "")
        }
    }
}



