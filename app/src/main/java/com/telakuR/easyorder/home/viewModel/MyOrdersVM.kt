package com.telakuR.easyorder.home.viewModel

import android.util.Log
import com.telakuR.easyorder.home.models.UserPaymentModelResponse
import com.telakuR.easyorder.home.repository.MyOrdersRepository
import com.telakuR.easyorder.main.repository.UserDataRepository
import com.telakuR.easyorder.main.services.AccountService
import com.telakuR.easyorder.main.services.LogService
import com.telakuR.easyorder.main.viewmodel.EasyOrderViewModel
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.room_db.enitites.EmployeeMenuItem
import com.telakuR.easyorder.room_db.enitites.MyOrder
import com.telakuR.easyorder.room_db.enitites.MyOrderWithDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyOrdersVM @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val myOrdersRepository: MyOrdersRepository,
    private val userDataRepository: UserDataRepository,
    private val accountService: AccountService,
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _myOrderList = MutableStateFlow<List<MyOrder>>(emptyList())
    val myOrderList: StateFlow<List<MyOrder>> get() = _myOrderList

    private val _myOrderDetails = MutableStateFlow<MyOrderWithDetails?>(null)
    val myOrderDetails: StateFlow<MyOrderWithDetails?> get() = _myOrderDetails

    private val _paymentDetailsList = MutableStateFlow<List<UserPaymentModelResponse>>(emptyList())
    val paymentDetailsList: StateFlow<List<UserPaymentModelResponse>> get() = _paymentDetailsList

    fun removeMenuItem(orderId: String, menuItem: EmployeeMenuItem?) = launchCatching {
        val companyId = userDataRepository.getCompanyId() ?: ""
        myOrdersRepository.removeMenuItemFromOrder(
            orderId = orderId,
            companyId = companyId,
            menuItem = menuItem)
    }

    fun completeOrder(orderId: String) = launchCatching {
        val companyId = userDataRepository.getCompanyId() ?: ""
        myOrdersRepository.completeOrder(orderId = orderId, companyId = companyId)
    }

    fun getListOfMyOrdersFromAPI() = launchCatching {
        val companyId = userDataRepository.getCompanyId() ?: ""

        myOrdersRepository.getMyOrdersFromAPI(companyId = companyId).collect { ordersList ->
            val myCustomizedOrderList = ordersList.toMutableList()
            val myOrder = ordersList.firstOrNull { it.employeeId == accountService.currentUserId }
            myCustomizedOrderList.remove(myOrder)
            myOrder?.let { myCustomizedOrderList.add(0, it) }

            myOrdersRepository.saveMyOrders(myCustomizedOrderList)
            getOrderDetails(companyId = companyId, myOrderList = myCustomizedOrderList)
        }
    }

    private suspend fun getOrderDetails(companyId: String, myOrderList: MutableList<MyOrder>) {
        withContext(ioDispatcher) {
            myOrderList.forEach { order ->
                myOrdersRepository.getMyOrderDetails(companyId = companyId, orderId = order.id)
                    .collect { orders ->
                        myOrdersRepository.saveOrderDetailsOnDB(orderId = order.id, orders = orders)
                    }
            }
        }
    }

    fun getListOfMyOrdersFromDB() = launchCatching {
        myOrdersRepository.getMyOrdersFromDB().collect { ordersList ->
            _myOrderList.value = ordersList.map { it.myOrder }
        }
    }

    fun removeOrder(orderId: String) = launchCatching {
        val companyId = userDataRepository.getCompanyId() ?: ""
        myOrdersRepository.removeOrder(orderId = orderId, companyId = companyId)
    }

    fun getPaymentDetails(orderId: String) = launchCatching {
        val companyId = userDataRepository.getCompanyId() ?: ""
        myOrdersRepository.getPaymentDetails(companyId = companyId, orderId = orderId).collect {
            _paymentDetailsList.value = it
        }
    }

    fun setPaidValue(id: String, paid: String, orderId: String) = launchCatching {
        myOrdersRepository.setPaidValuesToPayments(employeeId = id, paid = paid, orderId = orderId)
    }

    fun getMyOrderFromDB(orderId: String) = launchCatching {
        withContext(ioDispatcher) {
            myOrdersRepository.getMyOrderDetailsFromDB(orderId = orderId).collect { orders ->
                _myOrderDetails.value = orders
            }
        }
    }
}