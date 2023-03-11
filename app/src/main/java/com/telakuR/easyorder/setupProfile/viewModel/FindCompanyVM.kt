package com.telakuR.easyorder.setupProfile.viewModel

import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.services.LogService
import com.telakuR.easyorder.setupProfile.repository.SetupProfileRepository
import com.telakuR.easyorder.mainViewModel.EasyOrderViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FindCompanyVM @Inject constructor(
    private val setupProfileRepository: SetupProfileRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _companies = MutableStateFlow<List<User>>(emptyList())
    var companies: StateFlow<List<User>> = _companies

    private val _previousRequestedCompany = MutableStateFlow<String?>("")
    var previousRequestedCompany: StateFlow<String?> = _previousRequestedCompany

    fun getCompanies() {
        launchCatching {
            withContext(ioDispatcher) {
                setupProfileRepository.getCompanies()
            }.collect {
                _companies.value = it
            }
        }
    }

    fun handleRequestState(id: String, state: Boolean) {
        launchCatching {
            withContext(ioDispatcher) {
                if (state) {
                    setupProfileRepository.requestToJoin(id)
                } else {
                    setupProfileRepository.removeRequest(id)
                }
            }
        }
    }

    fun getSelectedCompany() {
        launchCatching {
            val previousCompany = async(ioDispatcher) {
                setupProfileRepository.getRequestedCompany()
            }.await()

            _previousRequestedCompany.value = previousCompany
        }
    }
}
