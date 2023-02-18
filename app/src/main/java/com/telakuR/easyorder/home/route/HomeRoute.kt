package com.telakuR.easyorder.home.route

import com.telakuR.easyorder.R

sealed class HomeRoute(
val route: String,
val title: String = "",
val icon: Int = 0,
val icon_focused: Int = 0
) {

    object Home : HomeRoute(
        route = "home",
        title = "Home",
        icon = R.drawable.ic_home_navigation_bar,
        icon_focused = R.drawable.ic_home_navigation_bar
    )

    object Report : HomeRoute(
        route = "report",
        title = "Report",
        icon = R.drawable.ic_group_request,
        icon_focused = R.drawable.ic_group_request
    )

    object Order : HomeRoute(
        route = "order",
        title = "order",
        icon = R.drawable.ic_icon_order,
        icon_focused = R.drawable.ic_icon_order
    )

    object Profile : HomeRoute(
        route = "profile",
        title = "Profile",
        icon = R.drawable.ic_icon_profile,
        icon_focused = R.drawable.ic_icon_profile
    )

    object Notification : HomeRoute(route = "notification")

    object OrderDetails : HomeRoute(route = "orderDetails")

    object ChooseFastFood : HomeRoute(route = "chooseFastFood")

    object ChooseFood : HomeRoute(route = "chooseFood")

    object GroupedOrders : HomeRoute(route = "groupedOrders")

    object PaymentDetails : HomeRoute(route = "paymentDetails")
}