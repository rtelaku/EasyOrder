package com.telakuR.easyorder.room_db.dao

import androidx.room.*
import com.telakuR.easyorder.room_db.enitites.EmployeeRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyEmployeeRequestsDao {

    @Query("SELECT * FROM company_requests_table")
    fun getCompanyEmployeeRequests(): Flow<List<EmployeeRequest>>

    @Transaction
    suspend fun deleteAndInsertEmployeeRequests(employeeRequests: List<EmployeeRequest>) {
        deleteEmployeeRequests()
        insertCompanyEmployeeRequests(employeeRequests = employeeRequests)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompanyEmployeeRequests(employeeRequests: List<EmployeeRequest>)

    @Query("DELETE FROM company_requests_table")
    suspend fun deleteEmployeeRequests()

}