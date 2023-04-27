package com.telakuR.easyorder.main.models

data class User(
    val token: String = "",
    var id: String = "",
    val name: String,
    val email: String,
    val role: String,
    val profilePic: String
)