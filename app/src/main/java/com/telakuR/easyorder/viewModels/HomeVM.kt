package com.telakuR.easyorder.viewModels

import androidx.lifecycle.viewModelScope
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.repositories.impl.UserDataRepositoryImpl
import com.telakuR.easyorder.services.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val userDataRepositoryImpl: UserDataRepositoryImpl,
    logService: LogService,
    @IoDispatcher val ioDispatcher: CoroutineDispatcher
) : EasyOrderViewModel(logService) {

    private val _shouldLaunchSetupScreen = MutableStateFlow(false)
    val shouldLaunchSetupScreen: StateFlow<Boolean> get() = _shouldLaunchSetupScreen

    init {
        setScreenToLaunch()
    }

    private fun setScreenToLaunch() = viewModelScope.launch(ioDispatcher) {
        val profilePic = userDataRepositoryImpl.getUserProfilePicture()
        if (profilePic.toString().isEmpty() || profilePic == null) {
            _shouldLaunchSetupScreen.value = true
        }
    }
}