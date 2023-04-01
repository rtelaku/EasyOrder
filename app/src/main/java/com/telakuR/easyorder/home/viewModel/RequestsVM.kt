package com.telakuR.easyorder.home.viewModel

import com.telakuR.easyorder.home.repository.HomeRepository
import com.telakuR.easyorder.mainViewModel.EasyOrderViewModel
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.services.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RequestsVM @Inject constructor(
    private val homeRepository: HomeRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _requests = MutableStateFlow<List<User>>(emptyList())
    var requests: StateFlow<List<User>> = _requests

    fun getListOfRequests() {
        launchCatching {
            val requestsList = withContext(ioDispatcher) {
                    return@withContext homeRepository.getRequestsList()
                }

            homeRepository.getEmployeesRequestsDetails(requestsList).collect {
                _requests.value = it
            }
        }
    }

    fun acceptRequest(id: String) {
        launchCatching {
            withContext(ioDispatcher) {
                homeRepository.acceptRequest(id)
                delay(1000)
                getListOfRequests()
            }
        }
    }

    fun removeRequest(id: String) {
        launchCatching {
            withContext(ioDispatcher) {
                homeRepository.removeRequest(id)
                delay(1000)
                getListOfRequests()
            }
        }
    }
}