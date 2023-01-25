package com.telakuR.easyorder.models

sealed class UserRoute(val route: String) {
    object Home: UserRoute(route = "home")
}
