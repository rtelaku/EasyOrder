package com.telakuR.easyorder.home.repository

import com.telakuR.easyorder.home.models.Order
import com.telakuR.easyorder.home.models.OrderDetails
import com.telakuR.easyorder.models.User
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getEmployeesList(): List<String>

    suspend fun getEmployees(requests: List<String>): Flow<List<User>>

    suspend fun removeEmployee(id: String)

    suspend fun getRequestsList(): List<String>

    suspend fun getRequests(requestsEmails: List<String>): Flow<List<User>>

    suspend fun acceptRequest(id: String)

    suspend fun removeRequest(id: String)

    suspend fun getOrders(userCompanyId: String): Flow<List<OrderDetails>>

    suspend fun getMenuItems(companyName: String): Flow<List<Order>>
}