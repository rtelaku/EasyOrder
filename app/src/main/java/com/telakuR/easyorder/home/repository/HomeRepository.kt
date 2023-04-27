package com.telakuR.easyorder.home.repository

import com.telakuR.easyorder.home.models.*
import com.telakuR.easyorder.main.models.User
import com.telakuR.easyorder.room_db.enitites.CompanyOrderDetails
import com.telakuR.easyorder.room_db.enitites.Employee
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    fun getFastFoods(): Flow<List<FastFood>>

    fun getFastFoodMenu(fastFoodId: String): Flow<List<MenuItem>>

    fun getMyOrderDetails(companyId: String, orderId: String, isMyOrder: Boolean): Flow<List<EmployeeMenuItem>>

    fun getMyOrders(companyId: String): Flow<List<OrderDetails>>

    fun getEmployeesListFromDB(): Flow<List<Employee>>

    fun completeOrder(orderId: String, companyId: String)

    fun removeMenuItemFromOrder(orderId: String, companyId: String, menuItem: EmployeeMenuItem?)

    fun removeOrder(orderId: String, companyId: String)

    fun getPaymentDetails(companyId: String, orderId: String): Flow<List<UserPaymentModelResponse>>

    fun removeMenuItemPaymentDetails(orderId: String, menuItem: EmployeeMenuItem)

    fun setPaidValuesToPayments(employeeId: String, paid: String, orderId: String)

    fun getOrdersFromDB(): Flow<List<CompanyOrderDetails>>

    fun getEmployeesFromAPI(): Flow<List<Employee>>

    fun getOrdersFromAPI(userCompanyId: String): Flow<List<CompanyOrderDetails>>

    suspend fun getOrder(orderId: String, companyId: String): OrderDetails

    suspend fun getFastFoodId(orderId: String, companyId: String): String

    suspend fun removeEmployee(id: String)

    suspend fun createOrderWithFastFood(companyId: String, fastFood: String, menuItem: MenuItem): String

    suspend fun checkIfEmployeeHasAnOrder(companyId: String): Boolean

    suspend fun addMenuItemToOrder(companyId: String, menuItem: MenuItem, orderId: String): Boolean

    suspend fun saveEmployeesOnDB(employees: List<Employee>)

    suspend fun saveOrdersOnDB(companyOrders: List<CompanyOrderDetails>)
}