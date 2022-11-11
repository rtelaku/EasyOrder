package com.telakuR.easyorder.ui.theme

sealed class Route(val route: String) {
    object Login: Route("login")
    object SignUp: Route("signup")
    object Main: Route("main")
}