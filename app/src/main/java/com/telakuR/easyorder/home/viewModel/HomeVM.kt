package com.telakuR.easyorder.home.viewModel

import com.telakuR.easyorder.home.repository.HomeRepository
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.main.enums.RolesEnum
import com.telakuR.easyorder.main.repository.UserDataRepository
import com.telakuR.easyorder.main.services.LogService
import com.telakuR.easyorder.main.viewmodel.EasyOrderViewModel
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.room_db.enitites.CompanyOrderDetails
import com.telakuR.easyorder.room_db.enitites.Employee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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

    private val _orders = MutableStateFlow<List<CompanyOrderDetails>>(emptyList())
    var orders: StateFlow<List<CompanyOrderDetails>> = _orders

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    var employees: StateFlow<List<Employee>> = _employees

    private val _isUserOnACompany = MutableStateFlow<Boolean?>(null)
    var isUserOnACompany: StateFlow<Boolean?> = _isUserOnACompany

    private val _hasAlreadyAnOrder = MutableStateFlow<Boolean?>(null)
    var hasAlreadyAnOrder: StateFlow<Boolean?> = _hasAlreadyAnOrder

    private val _currentUserRole = MutableStateFlow("")
    var currentUserRole: StateFlow<String> = _currentUserRole

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

    fun removeEmployee(id: String) {
        launchCatching {
            homeRepository.removeEmployee(id = id)
        }
    }

    fun getListOfOrdersFromAPI() {
        launchCatching {
            val userCompanyId = userDataRepository.getCompanyId()
            homeRepository.getOrdersFromAPI(userCompanyId = userCompanyId).collect { orders ->
                homeRepository.saveOrdersOnDB(companyOrders = orders)
            }
        }
    }

    fun getListOfOrdersFromDB() {
        launchCatching {
            homeRepository.getOrdersFromDB().collect { companyOrders ->
                _orders.value = companyOrders
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

    fun getListOfEmployeesFromAPI() {
        launchCatching {
            homeRepository.getEmployeesFromAPI().collect { employees ->
                homeRepository.saveEmployeesOnDB(employees = employees)
            }
        }
    }

    fun getListOfEmployeesFromDB() {
        launchCatching {
            homeRepository.getEmployeesListFromDB().collect { employees ->
                _employees.value = employees
            }
        }
    }
}