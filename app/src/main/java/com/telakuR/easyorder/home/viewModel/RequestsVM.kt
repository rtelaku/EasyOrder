package com.telakuR.easyorder.home.viewModel

import com.telakuR.easyorder.home.repository.EmployeeRequestsRepository
import com.telakuR.easyorder.main.services.LogService
import com.telakuR.easyorder.main.viewmodel.EasyOrderViewModel
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.room_db.enitites.EmployeeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RequestsVM @Inject constructor(
    private val employeeRequestsRepository: EmployeeRequestsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _requests = MutableStateFlow<List<EmployeeRequest>>(emptyList())
    var requests: StateFlow<List<EmployeeRequest>> = _requests

    fun getListOfRequestsFromAPI() {
        launchCatching {
            employeeRequestsRepository.getEmployeeRequestsFromAPI().collect { requests ->
                employeeRequestsRepository.saveEmployeeRequestOnDB(employeeRequests = requests)
            }
        }
    }

    fun getListOfRequestsFromDB() {
        launchCatching {
            employeeRequestsRepository.getEmployeeRequestsFromDB().collect { requests ->
                _requests.value = requests
            }
        }
    }

    fun acceptRequest(id: String) {
        launchCatching {
            withContext(ioDispatcher) {
                employeeRequestsRepository.acceptRequest(id)
            }
        }
    }

    fun removeRequest(id: String) {
        launchCatching {
            withContext(ioDispatcher) {
                employeeRequestsRepository.removeRequest(id)
            }
        }
    }
}