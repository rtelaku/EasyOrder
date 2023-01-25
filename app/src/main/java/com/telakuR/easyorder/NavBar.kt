//package com.telakuR.easyorder
//
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.Badge
//import androidx.compose.material.BadgedBox
//import androidx.compose.material.Icon
//import androidx.compose.material.Text
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavDestination
//import androidx.navigation.NavDestination.Companion.hierarchy
//import androidx.navigation.NavGraph.Companion.findStartDestination
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import com.telakuR.easyorder.models.UserRoute
//import com.telakuR.easyorder.models.routes.UserRoute
//
//@Composable
//fun BottomNavigationBar(navController: NavHostController) {
////    val screens = listOf(
////        UserRoute.Home
////    )
//
//    val navStackBackEntry by navController.currentBackStackEntryAsState()
//    val currentDestination = navStackBackEntry?.destination
//
//    Row(
//        modifier = Modifier
//            .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)
//            .background(Color.Transparent)
//            .fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        screens.forEach { screen ->
//            AddINavItem(
//                screen = screen,
//                currentDestination = currentDestination,
//                navController = navController
//            )
//        }
//    }
//}
//
//@Composable
//fun AddINavItem(
//    screen: UserRoute,
//    currentDestination: NavDestination?,
//    navController: NavHostController
//) {
//    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
//
//    val background =
//        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else Color.Transparent
//
//    val contentColor =
//        if (selected) Color.White else Color.Black
//
//    Box(
//        modifier = Modifier
//            .height(40.dp)
//            .clip(CircleShape)
//            .background(background)
//            .clickable(onClick = {
//                navController.navigate(screen.route) {
//                    popUpTo(navController.graph.findStartDestination().id)
//                    launchSingleTop = true
//                }
//            })
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(4.dp)
//        ) {
//
//
//            //* if menu title : Report means we will show badge
//            if (screen.title == "Report") { // with badge
//                BadgedBox(badge = { Badge { Text("6") } }) {
//                    Icon(
//                        painter = painterResource(id = if (selected) screen.icon else screen.icon),
//                        contentDescription = "icon",
//                        tint = contentColor
//                    )
//                }
//
//            } else {
//
//                Icon(
//                    painter = painterResource(id = if (selected) screen.icon else screen.icon),
//                    contentDescription = "icon",
//                    tint = contentColor
//                )
//
//            }
//
//            AnimatedVisibility(visible = selected) {
//                Text(
//                    text = screen.title,
//                    color = contentColor
//                )
//            }
//        }
//    }
//}
//
