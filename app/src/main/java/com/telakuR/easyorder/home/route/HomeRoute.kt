package com.telakuR.easyorder.home.route

import com.telakuR.easyorder.R
import com.telakuR.easyorder.utils.EasyOrder

sealed class HomeRoute(
    val route: String,
    var title: String = "",
    val icon: Int = 0,
    val icon_focused: Int = 0
) {

    object Home : HomeRoute(
        route = "home",
        title = EasyOrder.getInstance().getString(R.string.home),
        icon = R.drawable.ic_home_navigation_bar,
        icon_focused = R.drawable.ic_home_navigation_bar
    )

    object Requests : HomeRoute(
        route = "requests",
        title = EasyOrder.getInstance().getString(R.string.requests),
        icon = R.drawable.ic_group_request,
        icon_focused = R.drawable.ic_group_request
    )

    object Orders : HomeRoute(
        route = "orders",
        title = EasyOrder.getInstance().getString(R.string.my_orders),
        icon = R.drawable.ic_icon_order,
        icon_focused = R.drawable.ic_icon_order
    )

    object Profile : HomeRoute(
        route = "profile",
        title = EasyOrder.getInstance().getString(R.string.profile),
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