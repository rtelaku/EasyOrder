package com.telakuR.easyorder.home.repository

import com.telakuR.easyorder.home.models.UserPaymentModelResponse
import com.telakuR.easyorder.room_db.enitites.EmployeeMenuItem
import com.telakuR.easyorder.room_db.enitites.MyOrder
import com.telakuR.easyorder.room_db.enitites.MyOrderWithDetails
import kotlinx.coroutines.flow.Flow

interface MyOrdersRepository {

    fun getMyOrderDetails(companyId: String, orderId: String): Flow<List<com.telakuR.easyorder.room_db.enitites.EmployeeMenuItem>>

    fun removeMenuItemPaymentDetails(orderId: String, menuItem: EmployeeMenuItem)

    fun getMyOrdersFromDB(): Flow<List<MyOrderWithDetails>>

    fun getMyOrdersFromAPI(companyId: String): Flow<List<MyOrder>>

    fun getPaymentDetails(companyId: String, orderId: String): Flow<List<UserPaymentModelResponse>>

    suspend fun removeMenuItemFromOrder(orderId: String, companyId: String, menuItem: EmployeeMenuItem?)

    suspend fun completeOrder(orderId: String, companyId: String)

    suspend fun removeOrder(orderId: String, companyId: String)

    suspend fun setPaidValuesToPayments(employeeId: String, paid: String, orderId: String)

    suspend fun saveOrderDetailsOnDB(ord: Any)

    suspend fun saveMyOrders(myCustomizedOrderList: MutableList<MyOrder>)

    suspend fun removeMenuItemFromOrder(
        orderId: String,
        companyId: String,
        menuItem: com.telakuR.easyorder.home.models.EmployeeMenuItem?
    )
}