package com.telakuR.easyorder.models

data class User(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val profilePic: String
)