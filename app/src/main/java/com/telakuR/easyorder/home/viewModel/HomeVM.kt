package com.telakuR.easyorder.home.viewModel

import com.telakuR.easyorder.enums.RolesEnum
import com.telakuR.easyorder.home.models.OrderDetails
import com.telakuR.easyorder.home.repository.HomeRepository
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.mainRepository.UserDataRepository
import com.telakuR.easyorder.mainViewModel.EasyOrderViewModel
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.services.LogService
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

    private val _hasAlreadyAnOrder = MutableStateFlow<Boolean?>(null)
    var hasAlreadyAnOrder: StateFlow<Boolean?> = _hasAlreadyAnOrder

    private val _currentUserRole = MutableStateFlow("")
    var currentUserRole: StateFlow<String> = _currentUserRole

    private val _toastMessageId = MutableStateFlow<Int?>(null)
    var toastMessageId: StateFlow<Int?> = _toastMessageId

    init {
        getUserRole()
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

                homeRepository.getEmployeesDetails(employees = employeesList)
            }.collect {
                _employees.value = it
            }
        }
    }

    fun removeEmployee(id: String) {
        launchCatching {
            withContext(ioDispatcher) {
                homeRepository.removeEmployee(id = id)
                delay(1000)
                getListOfEmployees()
            }
        }
    }

    fun getListOfOrders() {
        launchCatching {
            val userCompanyId = userDataRepository.getCompanyId()
            homeRepository.getOrders(userCompanyId = userCompanyId).collect {
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

    fun checkIfUserHasAnOrder() {
        launchCatching {
            val hasOrder = withContext(ioDispatcher) {
                val companyId = userDataRepository.getCompanyId()
                return@withContext homeRepository.checkIfEmployeeHasAnOrder(companyId = companyId)
            }

            _hasAlreadyAnOrder.value = hasOrder

            delay(1000)
            _hasAlreadyAnOrder.value = null
        }
    }

    private fun getUserRole() = launchCatching {
        val userRole = withContext(ioDispatcher) {
            userDataRepository.getUserRole()
        }

        _currentUserRole.value = userRole
    }

    fun getHomeScreens(): ArrayList<HomeRoute> {
        val role = _currentUserRole.value
        val screens: ArrayList<HomeRoute> = arrayListOf()

        if (role == RolesEnum.COMPANY.role) {
            screens.add(HomeRoute.Home)
            screens.add(HomeRoute.Requests)
            screens.add(HomeRoute.Profile)
        } else if (role == RolesEnum.USER.role) {
            screens.add(HomeRoute.Home)
            screens.add(HomeRoute.Orders)
            screens.add(HomeRoute.Profile)
        }

        return screens
    }
}