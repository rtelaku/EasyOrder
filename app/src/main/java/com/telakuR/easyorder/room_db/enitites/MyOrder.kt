package com.telakuR.easyorder.room_db.enitites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_orders_table")
data class MyOrder(
    @PrimaryKey var id: String = "",
    val employeeId: String = "",
    var owner: String = "",
    var fastFood: String = "",
    var isMyOrder: Boolean = false
)
