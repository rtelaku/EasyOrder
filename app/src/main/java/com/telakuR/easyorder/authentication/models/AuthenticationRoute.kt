package com.telakuR.easyorder.authentication.models

sealed class AuthenticationRoute(val route: String) {
    object Login: AuthenticationRoute("login")
    object SignUp: AuthenticationRoute("signup")
    object ForgotPassword: AuthenticationRoute("forgotPassword")
}