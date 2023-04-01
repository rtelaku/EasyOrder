package com.telakuR.easyorder.home.viewModel

import com.telakuR.easyorder.home.models.EmployeeMenuItem
import com.telakuR.easyorder.home.models.FastFood
import com.telakuR.easyorder.home.models.MenuItem
import com.telakuR.easyorder.home.models.OrderDetails
import com.telakuR.easyorder.home.repository.HomeRepository
import com.telakuR.easyorder.mainRepository.UserDataRepository
import com.telakuR.easyorder.mainViewModel.EasyOrderViewModel
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.services.AccountService
import com.telakuR.easyorder.services.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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

    private val _continueOrder = MutableStateFlow<Boolean?>(null)
    val continueOrder: StateFlow<Boolean?> get() = _continueOrder

    private val _fastFoodMenu = MutableStateFlow<List<MenuItem>>(emptyList())
    val fastFoodMenu: StateFlow<List<MenuItem>> get() = _fastFoodMenu

    private val _myOrderList = MutableStateFlow<List<OrderDetails>>(emptyList())
    val myOrderList: StateFlow<List<OrderDetails>> get() = _myOrderList

    private val _myOrderMenu = MutableStateFlow<List<EmployeeMenuItem>>(emptyList())
    val myOrderMenu: StateFlow<List<EmployeeMenuItem>> get() = _myOrderMenu

    private val _fastFoodName = MutableStateFlow("")
    val fastFoodName: StateFlow<String> get() = _fastFoodName

    private val _toastMessageId = MutableStateFlow<Int?>(null)
    var toastMessageId: StateFlow<Int?> = _toastMessageId

    fun getFastFoods() {
        launchCatching {
            homeRepository.getFastFoods().collect {
                _fastFoods.value = it
            }
        }
    }

    fun getMenuItems(fastFoodName: String) {
        launchCatching {
            withContext(ioDispatcher) {
                return@withContext homeRepository.getFastFoodMenu(fastFoodName)
            }.collect {
                _fastFoodMenu.value = it
            }
        }
    }

    fun createOrder(fastFood: String, menuItem: MenuItem) {
        launchCatching {
            val continueWithOrder = withContext(ioDispatcher) {
                val companyId = userDataRepository.getCompanyId()
                homeRepository.createOrderWithFastFood(
                    companyId = companyId,
                    fastFood = fastFood,
                    menuItem = menuItem
                )
            }

            _continueOrder.value = continueWithOrder
        }
    }

    fun getMyOrderMenu() {


    }

    fun addMenuItem(menuItem: MenuItem, orderId: String) {
        launchCatching {
            val continueWithOrder = withContext(ioDispatcher) {
                val companyId = userDataRepository.getCompanyId()
                homeRepository.addMenuItemToOrder(
                    companyId = companyId,
                    menuItem = menuItem,
                    orderId = orderId
                )
            }

            _continueOrder.value = continueWithOrder
        }
    }

    fun getListOfMyOrders() {
        launchCatching {
            val companyId = userDataRepository.getCompanyId()
            homeRepository.getMyOrders(companyId = companyId).collect { ordersList ->
                val myList = ordersList.toMutableList()
                val myOrder = ordersList.firstOrNull { it.employeeId == accountService.currentUserId }
                myList.remove(myOrder)
                myOrder?.let { myList.add(0, it) }
                _myOrderList.value = myList
            }
        }
    }

    fun getMyId(): String {
        return accountService.currentUserId
    }

    fun removeMenuItem(orderId: String) {
       launchCatching {
           val companyId = userDataRepository.getCompanyId()
           homeRepository.removeMenuItemFromOrder(orderId = orderId, companyId = companyId)
       }
    }

    fun completeOrder(orderId: String) {
        launchCatching {
            val companyId = userDataRepository.getCompanyId()
            homeRepository.completeOrder(orderId = orderId, companyId = companyId)
        }
    }
}