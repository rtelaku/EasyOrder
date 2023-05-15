package com.telakuR.easyorder.room_db.dao

import androidx.room.*
import com.telakuR.easyorder.room_db.enitites.EmployeeMenuItem
import com.telakuR.easyorder.room_db.enitites.EmployeeRequest
import com.telakuR.easyorder.room_db.enitites.MyOrder
import com.telakuR.easyorder.room_db.enitites.MyOrderWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface MyOrdersDao {

    @Query("SELECT * FROM my_orders_table WHERE id = :id")
    fun getOrderDetailsById(id: String): Flow<MyOrderWithDetails>

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

    @Query("DELETE FROM my_orders_table WHERE id = :id ")
    fun deleteOrder(id: String)
}