package com.telakuR.easyorder.home.viewModel

import com.telakuR.easyorder.home.models.*
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
class OrdersVM @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val homeRepository: HomeRepository,
    private val userDataRepository: UserDataRepository,
    private val accountService: AccountService,
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _fastFoods = MutableStateFlow<List<FastFood>>(emptyList())
    val fastFoods: StateFlow<List<FastFood>> get() = _fastFoods

    private val _continueWithOrder = MutableStateFlow<OrderDetails?>(null)
    val continueWithOrder: StateFlow<OrderDetails?> get() = _continueWithOrder

    private val _fastFoodMenu = MutableStateFlow<List<MenuItem>>(emptyList())
    val fastFoodMenu: StateFlow<List<MenuItem>> get() = _fastFoodMenu

    private val _myOrderList = MutableStateFlow<List<OrderDetails>>(emptyList())
    val myOrderList: StateFlow<List<OrderDetails>> get() = _myOrderList

    private val _myOrderMenu = MutableStateFlow<List<EmployeeMenuItem>>(emptyList())
    val myOrderMenu: StateFlow<List<EmployeeMenuItem>> get() = _myOrderMenu

    private val _fastFoodId = MutableStateFlow("")
    val fastFoodId: StateFlow<String> get() = _fastFoodId

    private val _paymentDetailsList = MutableStateFlow<List<UserPaymentModelResponse>>(emptyList())
    val paymentDetailsList: StateFlow<List<UserPaymentModelResponse>> get() = _paymentDetailsList

    fun getFastFoods() {
        launchCatching {
            homeRepository.getFastFoods().collect {
                _fastFoods.value = it
            }
        }
    }

    fun getMenuItems(fastFoodId: String) {
        launchCatching {
            withContext(ioDispatcher) {
                return@withContext homeRepository.getFastFoodMenu(fastFoodId)
            }.collect {
                _fastFoodMenu.value = it
            }
        }
    }

    fun createOrder(fastFood: String, menuItem: MenuItem) {
        launchCatching {
            val order = withContext(ioDispatcher) {
                val companyId = userDataRepository.getCompanyId()
                val orderId = homeRepository.createOrderWithFastFood(
                    companyId = companyId,
                    fastFood = fastFood,
                    menuItem = menuItem
                )

                homeRepository.getOrder(orderId, companyId)
            }

            if(order.id.isNotEmpty()) {
                _continueWithOrder.value = order
            }
        }
    }

    fun getMyOrderMenu(orderId: String) = launchCatching {
        val companyId = userDataRepository.getCompanyId()
        homeRepository.getMyOrder(companyId = companyId, orderId = orderId).collect {
            _myOrderMenu.value = it
        }
    }

    fun addMenuItem(menuItem: MenuItem, orderId: String) {
        launchCatching {
            val companyId = userDataRepository.getCompanyId()
            val continueWithOrder = withContext(ioDispatcher) {
                homeRepository.addMenuItemToOrder(
                    companyId = companyId,
                    menuItem = menuItem,
                    orderId = orderId
                )
            }

            if(continueWithOrder) {
                val order = homeRepository.getOrder(orderId, companyId)
                _continueWithOrder.value = order
            }
        }
    }

    fun getMyId(): String {
        return accountService.currentUserId
    }

    fun removeMenuItem(orderId: String, menuItem: MenuItem) = launchCatching {
        val companyId = userDataRepository.getCompanyId()
        homeRepository.removeMenuItemFromOrder(orderId = orderId, companyId = companyId, menuItem = menuItem)
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

    fun getFastFoodByOrderId(orderId: String?) = launchCatching {
        val fastFoodId = withContext(ioDispatcher) {
            val companyId = userDataRepository.getCompanyId()
            return@withContext homeRepository.getFastFoodId(orderId = orderId ?: "", companyId = companyId)
        }

        _fastFoodId.value = fastFoodId
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

    fun getOtherOrder(orderId: String) = launchCatching {
        val companyId = userDataRepository.getCompanyId()
        homeRepository.getOtherOrder(companyId = companyId, orderId = orderId).collect {
            _myOrderMenu.value = it
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