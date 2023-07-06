package com.telakuR.easyorder.home.repository

import com.telakuR.easyorder.home.models.*
import com.telakuR.easyorder.room_db.enitites.CompanyOrderDetails
import com.telakuR.easyorder.room_db.enitites.Employee
import com.telakuR.easyorder.room_db.enitites.MyOrder
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    fun getFastFoods(): Flow<List<FastFood>>

    fun getFastFoodMenu(fastFoodId: String): Flow<List<MenuItem>>

    fun getEmployeesListFromDB(): Flow<List<Employee>>

    fun getOrdersFromDB(): Flow<List<CompanyOrderDetails>>

    fun getEmployeesFromAPI(): Flow<List<Employee>>

    fun getOrdersFromAPI(userCompanyId: String): Flow<List<CompanyOrderDetails>>

    suspend fun getOrder(orderId: String, companyId: String): MyOrder?

    suspend fun getFastFoodId(orderId: String, companyId: String): String

    suspend fun removeEmployee(id: String)

    suspend fun createOrderWithFastFood(companyId: String, fastFood: String, menuItem: MenuItem): String

    suspend fun checkIfEmployeeHasAnOrder(companyId: String): Boolean

    suspend fun addMenuItemToOrder(companyId: String, menuItem: MenuItem, orderId: String): Boolean

    suspend fun saveEmployeesOnDB(employees: List<Employee>)

    suspend fun saveOrdersOnDB(companyOrders: List<CompanyOrderDetails>)

    suspend fun saveOrderOnDB(order: MyOrder)
}