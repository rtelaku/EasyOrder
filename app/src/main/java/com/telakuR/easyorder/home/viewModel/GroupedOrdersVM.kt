package com.telakuR.easyorder.home.viewModel

import androidx.lifecycle.viewModelScope
import com.telakuR.easyorder.home.models.EmployeeMenuItem
import com.telakuR.easyorder.home.repository.MyOrdersRepository
import com.telakuR.easyorder.main.repository.UserDataRepository
import com.telakuR.easyorder.main.services.LogService
import com.telakuR.easyorder.main.viewmodel.EasyOrderViewModel
import com.telakuR.easyorder.modules.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GroupedOrdersVM @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val myOrdersRepository: MyOrdersRepository,
    private val userDataRepository: UserDataRepository,
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _myOrder = MutableStateFlow<List<EmployeeMenuItem>>(emptyList())
    val myOrder: StateFlow<List<EmployeeMenuItem>> = _myOrder

    private var companyId: String = ""

    init {
        viewModelScope.launch {
            companyId = userDataRepository.getCompanyId() ?: ""
        }
    }

    fun getMyOrderMenu(orderId: String, isMyOrder: Boolean) = launchCatching {
        withContext(ioDispatcher) {
            myOrdersRepository.getMyOrderDetails(
                companyId = companyId,
                orderId = orderId,
                isMyOrder = isMyOrder
            ).collect {
                _myOrder.emit(it)
            }
        }
    }
}