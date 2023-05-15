package com.telakuR.easyorder.room_db.enitites

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.telakuR.easyorder.home.models.MenuItem
import com.telakuR.easyorder.home.models.UserInfo

data class MyOrderWithDetails(
    @Embedded val myOrder: MyOrder = MyOrder(),
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val employeeMenuItems: List<EmployeeMenuItem> = emptyList(),
)

@Entity(tableName = "my_orders_table")
data class MyOrder(
    @PrimaryKey var id: String = "",
    val employeeId: String = "",
    var owner: String = "",
    var fastFood: String = "",
    var isMyOrder: Boolean = false
)

@Entity(tableName = "order_employee_menu_items_table")
data class EmployeeMenuItem(
    @PrimaryKey var id: String = "",
    var orderId: String = "",
    @Embedded val userInfo: UserInfo,
    @Embedded val menuItem: MenuItem
)
