package com.telakuR.easyorder.home.repository

import com.telakuR.easyorder.home.models.UserPaymentModelResponse
import com.telakuR.easyorder.room_db.enitites.EmployeeMenuItem
import com.telakuR.easyorder.room_db.enitites.MyOrder
import com.telakuR.easyorder.room_db.enitites.MyOrderWithDetails
import kotlinx.coroutines.flow.Flow

interface MyOrdersRepository {

    fun getMyOrderDetails(companyId: String, orderId: String): Flow<List<EmployeeMenuItem>>

    fun getMyOrdersFromDB(): Flow<List<MyOrderWithDetails>>

    fun getMyOrderDetailsFromDB(orderId: String): Flow<MyOrderWithDetails>

    fun getMyOrdersFromAPI(companyId: String): Flow<List<MyOrder>>

    fun getPaymentDetails(companyId: String, orderId: String): Flow<List<UserPaymentModelResponse>>

    fun removeMenuItemPaymentDetails(orderId: String, menuItem: EmployeeMenuItem)

    suspend fun removeMenuItemFromOrder(orderId: String, companyId: String, menuItem: EmployeeMenuItem?)

    suspend fun completeOrder(orderId: String, companyId: String)

    suspend fun removeOrder(orderId: String, companyId: String)

    suspend fun setPaidValuesToPayments(employeeId: String, paid: String, orderId: String)

    suspend fun saveOrderDetailsOnDB(orders: List<EmployeeMenuItem>, orderId: String)

    suspend fun saveMyOrders(myCustomizedOrderList: MutableList<MyOrder>)

    suspend fun removeMenuItemFromOrder(
        orderId: String,
        companyId: String,
        menuItem: com.telakuR.easyorder.home.models.EmployeeMenuItem?
    )
}