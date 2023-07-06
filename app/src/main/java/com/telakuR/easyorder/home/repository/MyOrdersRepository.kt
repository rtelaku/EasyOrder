package com.telakuR.easyorder.home.repository

import com.telakuR.easyorder.home.models.EmployeeMenuItem
import com.telakuR.easyorder.home.models.OrderDetails
import com.telakuR.easyorder.home.models.UserPaymentModelResponse
import kotlinx.coroutines.flow.Flow

interface MyOrdersRepository {

    fun getMyOrders(companyId: String): Flow<List<OrderDetails>>

    fun getPaymentDetails(companyId: String, orderId: String): Flow<List<UserPaymentModelResponse>>

    fun getMyOrderDetails(
        companyId: String,
        orderId: String,
        isMyOrder: Boolean
    ): Flow<List<EmployeeMenuItem>>

    fun setPaidValuesToPayments(employeeId: String, paid: String, orderId: String)

    fun removeMenuItemPaymentDetails(orderId: String, menuItem: EmployeeMenuItem)

    fun removeMenuItemFromOrder(orderId: String, companyId: String, menuItem: EmployeeMenuItem)

    fun removeOrder(companyId: String, orderId: String)

    suspend fun completeOrder(orderId: String, companyId: String)

}