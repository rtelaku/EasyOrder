package com.telakuR.easyorder.home.viewModel

import com.telakuR.easyorder.home.models.OrderDetails
import com.telakuR.easyorder.home.repository.HomeRepository
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.mainRepository.UserDataRepository
import com.telakuR.easyorder.services.LogService
import com.telakuR.easyorder.mainViewModel.EasyOrderViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val homeRepository: HomeRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _showSetupProfile = MutableStateFlow(false)
    var showSetupProfile: StateFlow<Boolean> = _showSetupProfile

    private val _orders = MutableStateFlow<List<OrderDetails>>(emptyList())
    var orders: StateFlow<List<OrderDetails>> = _orders

    private val _employees = MutableStateFlow<List<User>>(emptyList())
    var employees: StateFlow<List<User>> = _employees

    private val _isUserOnACompany = MutableStateFlow<Boolean?>(null)
    var isUserOnACompany: StateFlow<Boolean?> = _isUserOnACompany

    init {
        shouldShowSetupProfile()
    }

    private fun shouldShowSetupProfile() = launchCatching {
        val profilePic = userDataRepository.getUserProfilePicture()
        if (profilePic.isNullOrEmpty()) {
            _showSetupProfile.value = true
        }
    }

    fun getListOfEmployees() {
        launchCatching {
            withContext(ioDispatcher) {
                val employeesList = async {
                    return@async homeRepository.getEmployeesList()
                }.await()

                homeRepository.getEmployees(employeesList)
            }.collect {
                _employees.value = it
            }
        }
    }

    fun removeEmployee(id: String) {
        launchCatching {
            withContext(ioDispatcher) {
                homeRepository.removeEmployee(id)
                delay(1000)
                getListOfEmployees()
            }
        }
    }

    fun getListOfOrders() {
        launchCatching {
            withContext(ioDispatcher) {
                val userCompanyId = userDataRepository.getCompanyId()
                homeRepository.getOrders(userCompanyId = userCompanyId)
            }.collect {
                _orders.value = it
            }
        }
    }

    fun isUserInACompany() {
        launchCatching {
            val userCompany = withContext(ioDispatcher) {
                userDataRepository.isUserInACompany()
            }

            _isUserOnACompany.value = userCompany
        }
    }
}