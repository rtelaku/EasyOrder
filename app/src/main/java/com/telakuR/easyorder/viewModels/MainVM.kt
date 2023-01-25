package com.telakuR.easyorder.viewModels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.telakuR.easyorder.authentication.models.AuthenticationRoute
import com.telakuR.easyorder.models.UserRoute
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.repositories.impl.AccountServiceImpl
import com.telakuR.easyorder.services.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    val accountServiceImpl: AccountServiceImpl,
    logService: LogService,
    @IoDispatcher val ioDispatcher: CoroutineDispatcher
) : EasyOrderViewModel(logService) {

    var screenToLaunch = MutableStateFlow("")
        private set

    init {
        setScreenToLaunch()
    }

    private fun setScreenToLaunch() = viewModelScope.launch(ioDispatcher) {
        if(accountServiceImpl.currentUser != null) {
            screenToLaunch.value = UserRoute.Home.route
        } else {
            Log.d("rigiii", "setScreenToLaunch: login")
            screenToLaunch.value = AuthenticationRoute.Login.route
        }
    }
}