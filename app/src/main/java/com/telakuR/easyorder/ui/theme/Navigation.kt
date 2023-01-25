//package com.telakuR.easyorder.ui.theme
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.material.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.telakuR.easyorder.BottomNavigationBar
//import com.telakuR.easyorder.authentication.models.AuthenticationRoute
//import com.telakuR.easyorder.models.routes.UserRoute
//
//@Composable
//fun Navigation() {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = UserRoute.Home.route,
//        modifier = Modifier
//            .fillMaxWidth()
//            .fillMaxHeight()
//    ) {
//        composable(route = UserRoute.Home.route) {
//            HomeScreen(navController = navController)
//        }
//
//        composable(route = AuthenticationRoute.ForgotPassword.route) {
//            OrdersScreen(navController)
//        }
//
//
//    }
//}
//
//@Composable
//fun MyProfileScreen(navController: NavHostController) {
//    Text(text = "PROFILE")
//}
//
//@Composable
//fun OrdersScreen(navController: NavHostController) {
//    Text(text = "ORDERS")
//}
//
//@Composable
//fun HomeScreen(navController: NavHostController) {
//    BottomNavigationBar(navController = navController)
//}
