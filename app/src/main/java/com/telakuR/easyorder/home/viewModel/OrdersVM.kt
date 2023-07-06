package com.telakuR.easyorder.home.viewModel

import com.telakuR.easyorder.home.models.FastFood
import com.telakuR.easyorder.home.models.MenuItem
import com.telakuR.easyorder.home.repository.HomeRepository
import com.telakuR.easyorder.main.repository.UserDataRepository
import com.telakuR.easyorder.main.services.LogService
import com.telakuR.easyorder.main.viewmodel.EasyOrderViewModel
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.room_db.enitites.MyOrder
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
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _fastFoods = MutableStateFlow<List<FastFood>>(emptyList())
    val fastFoods: StateFlow<List<FastFood>> get() = _fastFoods

    private val _continueWithOrder = MutableStateFlow<MyOrder?>(null)
    val continueWithOrder: StateFlow<MyOrder?> get() = _continueWithOrder

    private val _fastFoodMenu = MutableStateFlow<List<MenuItem>>(emptyList())
    val fastFoodMenu: StateFlow<List<MenuItem>> get() = _fastFoodMenu

    private val _fastFoodId = MutableStateFlow("")
    val fastFoodId: StateFlow<String> get() = _fastFoodId

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
            val companyId = userDataRepository.getCompanyId() ?: ""

            val orderId = withContext(ioDispatcher) {
                homeRepository.createOrderWithFastFood(
                    companyId = companyId,
                    fastFood = fastFood,
                    menuItem = menuItem
                )
            }

            val order = homeRepository.getOrder(orderId = orderId, companyId = companyId)
            if(order != null) {
                order.isMyOrder = true
                homeRepository.saveOrderOnDB(order = order)
                _continueWithOrder.value = order
            }
        }
    }

    fun addMenuItem(menuItem: MenuItem, orderId: String) {
        launchCatching {
            val companyId = userDataRepository.getCompanyId() ?: ""
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

    fun getFastFoodByOrderId(orderId: String?) = launchCatching {
        val fastFoodId = withContext(ioDispatcher) {
            val companyId = userDataRepository.getCompanyId() ?: ""
            return@withContext homeRepository.getFastFoodId(orderId = orderId ?: "", companyId = companyId)
        }

        _fastFoodId.value = fastFoodId
    }
}