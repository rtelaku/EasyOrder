package com.telakuR.easyorder.home.viewModel

import com.telakuR.easyorder.home.repository.HomeDataRepositoryImpl
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.mainRepository.impl.UserDataRepositoryImpl
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
class RequestsVM @Inject constructor(
    private val homeDataRepositoryImpl: HomeDataRepositoryImpl,
    private val userDataRepositoryImpl: UserDataRepositoryImpl,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _requests = MutableStateFlow<List<User>>(emptyList())
    var requests: StateFlow<List<User>> = _requests

    private val _currentUserRole = MutableStateFlow("")
    var currentUserRole: StateFlow<String> = _currentUserRole

    init {
        getUserRole()
    }

    private fun getUserRole() = launchCatching {
        val userRole = withContext(ioDispatcher) {
            userDataRepositoryImpl.getUserRole()
        }

        _currentUserRole.value = userRole
    }

    fun getListOfRequests() {
        launchCatching {
            withContext(ioDispatcher) {
                val requestsList = async {
                    return@async homeDataRepositoryImpl.getRequestsList()
                }.await()

                homeDataRepositoryImpl.getRequests(requestsList)
            }.collect {
                _requests.value = it
            }
        }
    }

    fun acceptRequest(id: String) {
        launchCatching {
            withContext(ioDispatcher) {
                homeDataRepositoryImpl.acceptRequest(id)
                delay(1000)
                getListOfRequests()
            }
        }
    }

    fun removeRequest(id: String) {
        launchCatching {
            withContext(ioDispatcher) {
                homeDataRepositoryImpl.removeRequest(id)
                delay(1000)
                getListOfRequests()
            }
        }
    }
}