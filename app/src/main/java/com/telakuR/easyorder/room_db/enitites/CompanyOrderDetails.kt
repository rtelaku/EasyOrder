package com.telakuR.easyorder.room_db.enitites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "company_orders_table")
data class CompanyOrderDetails(@PrimaryKey var id: String = "", val employeeId: String = "", var owner: String = "", var fastFood: String = "")