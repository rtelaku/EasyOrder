package com.telakuR.easyorder.home.repository

import com.telakuR.easyorder.home.models.FastFood
import com.telakuR.easyorder.home.models.MenuItem
import com.telakuR.easyorder.home.models.Order
import com.telakuR.easyorder.home.models.OrderDetails
import com.telakuR.easyorder.models.User
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getEmployeesList(): List<String>

    suspend fun removeEmployee(id: String)

    suspend fun getRequestsList(): List<String>

    suspend fun acceptRequest(id: String)

    suspend fun removeRequest(id: String)

    suspend fun createOrderWithFastFood(companyId: String, fastFood: String, menuItem: MenuItem): Boolean

    suspend fun checkIfEmployeeHasAnOrder(companyId: String): Boolean

    fun getEmployees(employees: List<String>): Flow<List<User>>

    fun getRequests(requestsEmails: List<String>): Flow<List<User>>

    fun getOrders(userCompanyId: String): Flow<List<OrderDetails>>

    fun getMenuItems(companyName: String): Flow<List<Order>>

    fun getFastFoods(): Flow<List<FastFood>>

    fun getFastFoodMenu(fastFoodName: String): Flow<List<MenuItem>>
}