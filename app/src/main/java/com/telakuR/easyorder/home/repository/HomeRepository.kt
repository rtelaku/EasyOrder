package com.telakuR.easyorder.home.repository

import com.telakuR.easyorder.home.models.*
import com.telakuR.easyorder.models.User
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    fun getEmployeesList(): Flow<List<User>>

    fun getRequestsList(): Flow<List<User>>

    fun getOrders(userCompanyId: String): Flow<List<OrderDetails>>

    fun getFastFoods(): Flow<List<FastFood>>

    fun getFastFoodMenu(fastFoodName: String): Flow<List<MenuItem>>

    fun getMyOrder(companyId: String, orderId: String): Flow<List<EmployeeMenuItem>>

    fun getMyOrders(companyId: String): Flow<List<OrderDetails>>

    fun completeOrder(orderId: String, companyId: String)

    fun removeMenuItemFromOrder(orderId: String, companyId: String, menuItem: MenuItem)

    fun getOtherOrder(companyId: String, orderId: String): Flow<List<EmployeeMenuItem>>

    fun removeOrder(orderId: String, companyId: String)

    fun getPaymentDetails(companyId: String, orderId: String): Flow<List<UserPaymentModelResponse>>

    fun removeMenuItemPaymentDetails(orderId: String, menuItem: MenuItem)

    fun setPaidValuesToPayments(employeeId: String, paid: String, orderId: String)

    suspend fun getOrder(orderId: String, companyId: String): OrderDetails

    suspend fun getFastFoodId(orderId: String, companyId: String): String

    suspend fun removeEmployee(id: String)

    suspend fun acceptRequest(id: String)

    suspend fun removeRequest(id: String)

    suspend fun createOrderWithFastFood(companyId: String, fastFood: String, menuItem: MenuItem): String

    suspend fun checkIfEmployeeHasAnOrder(companyId: String): Boolean

    suspend fun addMenuItemToOrder(companyId: String, menuItem: MenuItem, orderId: String): Boolean

}