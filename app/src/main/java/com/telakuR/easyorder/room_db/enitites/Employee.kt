package com.telakuR.easyorder.room_db.enitites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "company_employees_table")
data class Employee(
    @PrimaryKey var id: String = "",
    val name: String,
    val profilePic: String
)