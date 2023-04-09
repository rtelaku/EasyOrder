package com.telakuR.easyorder.models

data class User(
    val token: String = "",
    var id: String = "",
    val name: String,
    val email: String,
    val role: String,
    val profilePic: String
)