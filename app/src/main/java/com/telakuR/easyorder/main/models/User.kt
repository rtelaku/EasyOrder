package com.telakuR.easyorder.main.models

import com.telakuR.easyorder.room_db.enitites.Profile

data class User(
    var id: String = "",
    val token: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val profilePic: String = ""
)

fun User.mapUserToProfile(): Profile {
    return Profile(name = name, email = email, role = role, profilePic = profilePic, companyId = "")
}
