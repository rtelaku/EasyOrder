package com.telakuR.easyorder.authentication.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.telakuR.easyorder.EasyOrderViewModel
import com.telakuR.easyorder.authentication.ext.isValidEmail
import com.telakuR.easyorder.authentication.models.LoginUiState
import com.telakuR.easyorder.authentication.models.services.AccountService
import com.telakuR.easyorder.authentication.models.services.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    val accountService: AccountService,
    logService: LogService
) : EasyOrderViewModel(logService)  {
    var uiState = mutableStateOf(LoginUiState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        if (!email.isValidEmail()) {
            return
        }

        if (password.isBlank()) {
            return
        }

        launchCatching {
            accountService.authenticate(email, password)
//            openAndPopUp(SETTINGS_SCREEN, LOGIN_SCREEN)
        }
    }

    fun onForgotPasswordClick() {
        if (!email.isValidEmail()) {
            return
        }

        launchCatching {
            accountService.sendRecoveryEmail(email)
        }
    }
}