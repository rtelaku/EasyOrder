package com.telakuR.easyorder.room_db.dao

import androidx.room.*
import com.telakuR.easyorder.room_db.enitites.Employee
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyEmployeesDao {

    @Query("SELECT * FROM company_employees_table")
    fun getCompanyEmployees(): Flow<List<Employee>>

    @Transaction
    suspend fun deleteAndInsertEmployees(companyEmployees: List<Employee>) {
        deleteEmployees()
        insertCompanyEmployees(companyEmployees = companyEmployees)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompanyEmployees(companyEmployees: List<Employee>)

    @Query("DELETE FROM company_employees_table")
    suspend fun deleteEmployees()

}