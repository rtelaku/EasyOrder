package com.telakuR.easyorder.room_db.enitites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile_table")
data class Profile(
    @PrimaryKey var id: Int,
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val profilePic: String = "",
    val companyId : String = ""
)