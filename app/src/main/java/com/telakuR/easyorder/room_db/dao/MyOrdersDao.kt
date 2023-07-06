package com.telakuR.easyorder.room_db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.telakuR.easyorder.room_db.enitites.MyOrder

@Dao
interface MyOrdersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<MyOrder>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: MyOrder)

    @Query("DELETE FROM my_orders_table")
    suspend fun clearAll()

}