package com.telakuR.easyorder.main.viewmodel

import com.telakuR.easyorder.authentication.models.AuthenticationRoute
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.main.services.AccountService
import com.telakuR.easyorder.main.services.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _screenToLaunch = MutableStateFlow("")
    var screenToLaunch: StateFlow<String> = _screenToLaunch

    init {
        setScreenToLaunch()
    }

    private fun setScreenToLaunch() = launchCatching {
        if(accountService.currentUser != null) {
            _screenToLaunch.value = HomeRoute.Home.route
        } else {
            _screenToLaunch.value = AuthenticationRoute.Login.route
        }
    }
}