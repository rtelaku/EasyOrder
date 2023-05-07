package com.telakuR.easyorder.room_db.dao

import androidx.room.*
import com.telakuR.easyorder.room_db.enitites.EmployeeMenuItem
import com.telakuR.easyorder.room_db.enitites.MyOrder
import com.telakuR.easyorder.room_db.enitites.MyOrderWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface MyOrdersDao {

    @Transaction
    @Query("SELECT * FROM my_orders_table WHERE id = :id")
    fun getOrderById(id: String): Flow<MyOrderWithDetails>

    @Query("SELECT * FROM order_employee_menu_items_table WHERE orderId = :id")
    fun getEmployeeMenuItemsById(id: Int): Flow<EmployeeMenuItem>

    @Transaction
    @Query("SELECT * FROM my_orders_table")
    fun getAllOrders(): Flow<List<MyOrderWithDetails>>

    @Transaction
    suspend fun deleteAndInsertOrders(orders: List<MyOrder>) {
        clearAll()
        insertOrders(orders)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<MyOrder>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: MyOrder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployeeMenuItems(employeeMenuItems: List<EmployeeMenuItem>)

    @Query("DELETE FROM my_orders_table")
    suspend fun clearAll()
}