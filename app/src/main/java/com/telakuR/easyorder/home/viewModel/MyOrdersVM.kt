package com.telakuR.easyorder.home.viewModel

import com.telakuR.easyorder.home.models.EmployeeMenuItem
import com.telakuR.easyorder.home.models.OrderDetails
import com.telakuR.easyorder.home.models.UserPaymentModelResponse
import com.telakuR.easyorder.home.repository.HomeRepository
import com.telakuR.easyorder.mainRepository.UserDataRepository
import com.telakuR.easyorder.mainViewModel.EasyOrderViewModel
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.services.AccountService
import com.telakuR.easyorder.services.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyOrdersVM @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val homeRepository: HomeRepository,
    private val userDataRepository: UserDataRepository,
    private val accountService: AccountService,
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _myOrderList = MutableStateFlow<List<OrderDetails>>(emptyList())
    val myOrderList: StateFlow<List<OrderDetails>> get() = _myOrderList

    private val _myOrderMenu = MutableStateFlow<List<EmployeeMenuItem>>(emptyList())
    val myOrderMenu: StateFlow<List<EmployeeMenuItem>> get() = _myOrderMenu

    private val _paymentDetailsList = MutableStateFlow<List<UserPaymentModelResponse>>(emptyList())
    val paymentDetailsList: StateFlow<List<UserPaymentModelResponse>> get() = _paymentDetailsList

    fun getMyOrderMenu(orderId: String, isMyOrder: Boolean) = launchCatching {
        withContext(ioDispatcher) {
            val companyId = userDataRepository.getCompanyId()
            homeRepository.getMyOrderDetails(companyId = companyId, orderId = orderId, isMyOrder = isMyOrder).collect {
                _myOrderMenu.value = it
            }
        }
    }

    fun getMyId(): String {
        return accountService.currentUserId
    }

    fun removeMenuItem(orderId: String, menuItem: EmployeeMenuItem?) = launchCatching {
        val companyId = userDataRepository.getCompanyId()
        homeRepository.removeMenuItemFromOrder(
            orderId = orderId,
            companyId = companyId,
            menuItem = menuItem)
    }

    fun completeOrder(orderId: String) = launchCatching {
        val companyId = userDataRepository.getCompanyId()
        homeRepository.completeOrder(orderId = orderId, companyId = companyId)
        delay(1000)
        getListOfMyOrders()
    }

    fun isMyOrder(employeeId: String): Boolean {
        return accountService.currentUserId == employeeId
    }

    fun getListOfMyOrders() = launchCatching {
        val companyId = userDataRepository.getCompanyId()
        homeRepository.getMyOrders(companyId = companyId).collect { ordersList ->
            val myList = ordersList.toMutableList()
            val myOrder =
                ordersList.firstOrNull { it.employeeId == accountService.currentUserId }
            myList.remove(myOrder)
            myOrder?.let { myList.add(0, it) }
            _myOrderList.value = myList
        }
    }

    fun removeOrder(orderId: String) = launchCatching {
        val companyId = userDataRepository.getCompanyId()
        homeRepository.removeOrder(orderId = orderId, companyId = companyId)
        delay(1000)
        getListOfMyOrders()
    }

    fun getPaymentDetails(orderId: String) = launchCatching {
        val companyId = userDataRepository.getCompanyId()
        homeRepository.getPaymentDetails(companyId = companyId, orderId = orderId).collect {
            _paymentDetailsList.value = it
        }
    }

    fun setPaidValue(id: String, paid: String, orderId: String) {
        homeRepository.setPaidValuesToPayments(employeeId = id, paid = paid, orderId = orderId)
    }
}