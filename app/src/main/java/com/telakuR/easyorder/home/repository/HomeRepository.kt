package com.telakuR.easyorder.home.repository

import com.telakuR.easyorder.home.models.FastFood
import com.telakuR.easyorder.home.models.MenuItem
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

    suspend fun addMenuItemToOrder(companyId: String, menuItem: MenuItem, orderId: String): Boolean

    fun getEmployeesDetails(employees: List<String>): Flow<List<User>>

    fun getEmployeesRequestsDetails(requestsEmails: List<String>): Flow<List<User>>

    fun getOrders(userCompanyId: String): Flow<List<OrderDetails>>

    fun getFastFoods(): Flow<List<FastFood>>

    fun getFastFoodMenu(fastFoodName: String): Flow<List<MenuItem>>

    fun getMyOrder(): Flow<OrderDetails>

    fun getMyOrders(companyId: String): Flow<List<OrderDetails>>

    fun completeOrder(orderId: String, companyId: String)

    fun removeMenuItemFromOrder(orderId: String, companyId: String)

}