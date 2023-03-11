package com.telakuR.easyorder.models

data class User(
    var id: String = "",
    val name: String,
    val email: String,
    val role: String,
    val profilePic: String
)