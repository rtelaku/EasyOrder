package com.telakuR.easyorder.authentication.viewmodel

import androidx.compose.runtime.mutableStateOf
import com.telakuR.easyorder.authentication.models.AuthUiState
import com.telakuR.easyorder.services.AccountService
import com.telakuR.easyorder.services.LogService
import com.telakuR.easyorder.viewModels.EasyOrderViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : EasyOrderViewModel(logService) {
    var uiState = mutableStateOf(AuthUiState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password


    private val _shouldShowHomeView = MutableStateFlow(false)
    var shouldShowHomeView: StateFlow<Boolean> = _shouldShowHomeView

    private val _shouldShowToast = MutableStateFlow(false)
    var shouldShowToast: StateFlow<Boolean> = _shouldShowToast

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick() {
        launchCatching {
            val currentUser = accountService.authenticate(email, password)
            if(currentUser != null) {
                _shouldShowHomeView.value = true
            } else {
                _shouldShowToast.value = true
            }
        }
    }

    fun onForgotPasswordClick() {
        launchCatching {
            accountService.sendRecoveryEmail(email)
        }
    }
}