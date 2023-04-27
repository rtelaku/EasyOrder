package com.telakuR.easyorder.main.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class User(
    var id: String = "",
    val token: String = "",
    val name: String,
    val email: String,
    val role: String,
    val profilePic: String
)
