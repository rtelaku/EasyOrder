package com.telakuR.easyorder.room_db.dao

import androidx.room.*
import com.telakuR.easyorder.room_db.enitites.CompanyOrderDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyOrdersDao {

    @Query("SELECT * FROM company_orders_table")
    fun getCompanyOrders(): Flow<List<CompanyOrderDetails>>

    @Transaction
    suspend fun deleteAndInsertOrders(companyOrders: List<CompanyOrderDetails>) {
        deleteOrders()
        insertCompanyOrders(companyOrders = companyOrders)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompanyOrders(companyOrders: List<CompanyOrderDetails>)

    @Query("DELETE FROM company_orders_table")
    suspend fun deleteOrders()

}