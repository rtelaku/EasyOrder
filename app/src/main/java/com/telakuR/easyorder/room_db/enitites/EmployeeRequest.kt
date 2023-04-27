package com.telakuR.easyorder.room_db.enitites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "company_requests_table")
data class EmployeeRequest(
    @PrimaryKey var id: String = "",
    val name: String,
    val profilePic: String
)