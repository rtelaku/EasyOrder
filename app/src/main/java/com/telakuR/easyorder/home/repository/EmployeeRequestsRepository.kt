package com.telakuR.easyorder.home.repository

import com.telakuR.easyorder.room_db.enitites.EmployeeRequest
import kotlinx.coroutines.flow.Flow

interface EmployeeRequestsRepository {

    fun getEmployeeRequestsFromAPI(): Flow<List<EmployeeRequest>>

    fun getEmployeeRequestsFromDB(): Flow<List<EmployeeRequest>>

    suspend fun acceptRequest(id: String)

    suspend fun removeRequest(id: String)

    suspend fun saveEmployeeRequestOnDB(employeeRequests: List<EmployeeRequest>)
}