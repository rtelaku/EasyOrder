package com.telakuR.easyorder.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.telakuR.easyorder.authentication.models.AuthUiState
import com.telakuR.easyorder.home.repository.HomeDataRepositoryImpl
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.repositories.impl.AccountServiceImpl
import com.telakuR.easyorder.repositories.impl.UserDataRepositoryImpl
import com.telakuR.easyorder.services.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val userDataRepositoryImpl: UserDataRepositoryImpl,
    private val homeDataRepositoryImpl: HomeDataRepositoryImpl,
    private val accountServiceImpl: AccountServiceImpl,
    logService: LogService,
    @IoDispatcher val ioDispatcher: CoroutineDispatcher
) : EasyOrderViewModel(logService) {

    var uiState = mutableStateOf(AuthUiState())
        private set

    var employees = MutableStateFlow<List<User>>(emptyList())
        private set

    var requests = MutableStateFlow<List<User>>(emptyList())
        private set

    var profile = MutableStateFlow(User("", "", "", ""))
        private set

    private val _shouldLaunchSetupScreen = MutableStateFlow(false)
    val shouldLaunchSetupScreen: StateFlow<Boolean> get() = _shouldLaunchSetupScreen

    init {
        setScreenToLaunch()
    }

    private fun setScreenToLaunch() = viewModelScope.launch(ioDispatcher) {
        val profilePic = userDataRepositoryImpl.getUserProfilePicture()
        if (profilePic.isNullOrEmpty()) {
            _shouldLaunchSetupScreen.value = true
        }
    }

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onNameChanged(newValue: String) {
        uiState.value = uiState.value.copy(name = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun getListOfEmployees() {
        viewModelScope.launch(ioDispatcher) {
            val employeeEmails = async {
                return@async homeDataRepositoryImpl.getEmployeeEmails()
            }.await()

            homeDataRepositoryImpl.getEmployees(employeeEmails).collect {
                employees.value = it
            }
        }
    }

    fun removeEmployee(email: String) {
        viewModelScope.launch(ioDispatcher) {
            homeDataRepositoryImpl.removeEmployee(email)
            delay(1000)
            getListOfEmployees()
        }
    }

    fun getListOfRequests() {
        viewModelScope.launch(ioDispatcher) {
            val requestsEmails = async {
                return@async homeDataRepositoryImpl.getRequestsEmails()
            }.await()

            homeDataRepositoryImpl.getRequests(requestsEmails).collect {
                requests.value = it
            }
        }
    }

    fun acceptRequest(email: String) {
        viewModelScope.launch(ioDispatcher) {
            homeDataRepositoryImpl.acceptRequest(email)
            delay(1000)
            getListOfRequests()
        }
    }

    fun removeRequest(email: String) {
        viewModelScope.launch(ioDispatcher) {
            homeDataRepositoryImpl.removeRequest(email)
            delay(1000)
            getListOfRequests()
        }
    }

    fun getProfile() {
        viewModelScope.launch(ioDispatcher) {
            userDataRepositoryImpl.getProfile().collect {
                profile.value = it
                uiState.value = uiState.value.copy(name = profile.value.name, email = profile.value.email)
            }
        }
    }

    fun logOut() {
        viewModelScope.launch(ioDispatcher) {
            accountServiceImpl.signOut()
        }
    }

    fun editProfile() {
        viewModelScope.launch(ioDispatcher) {
            accountServiceImpl.editProfile(uiState.value)
            delay(1000)
            getProfile()
        }
    }
}