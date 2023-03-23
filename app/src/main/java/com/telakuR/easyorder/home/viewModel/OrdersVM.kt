package com.telakuR.easyorder.home.viewModel

import android.util.Log
import com.telakuR.easyorder.home.models.FastFood
import com.telakuR.easyorder.home.models.MenuItem
import com.telakuR.easyorder.home.repository.HomeRepository
import com.telakuR.easyorder.mainRepository.UserDataRepository
import com.telakuR.easyorder.mainViewModel.EasyOrderViewModel
import com.telakuR.easyorder.modules.IoDispatcher
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
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _fastFoods = MutableStateFlow<List<FastFood>>(emptyList())
    val fastFoods: StateFlow<List<FastFood>> get() = _fastFoods

    private val _continueOrder = MutableStateFlow<Boolean?>(null)
    val continueOrder: StateFlow<Boolean?> get() = _continueOrder

    private val _menu = MutableStateFlow<List<MenuItem>>(emptyList())
    val menu: StateFlow<List<MenuItem>> get() = _menu

    private val _toastMessageId = MutableStateFlow<Int?>(null)
    var toastMessageId: StateFlow<Int?> = _toastMessageId

    fun getFastFoods() {
        launchCatching {
            withContext(ioDispatcher) {
                homeRepository.getFastFoods()
            }.collect {
                _fastFoods.value = it
            }
        }
    }

    fun getMenuItems(fastFoodName: String) {
        launchCatching {
            withContext(ioDispatcher) {
                Log.d("rigiii", "getMenuItems: $fastFoodName")
                return@withContext homeRepository.getFastFoodMenu(fastFoodName)
            }.collect {
                _menu.value = it
            }
        }
    }

    fun createOrder(fastFood: String, menuItem: MenuItem) {
        launchCatching {
            Log.d("rigiii", "createOrder: $")
            val continueWithOrder = withContext(ioDispatcher) {
                val companyId = userDataRepository.getCompanyId()

                homeRepository.createOrderWithFastFood(
                    companyId = companyId,
                    fastFood = fastFood,
                    menuItem = menuItem
                )
            }

            Log.d("rigiii", "createOrder: $continueWithOrder")
            _continueOrder.value = continueWithOrder
        }
    }
}