package com.telakuR.easyorder.authentication.viewmodel

import androidx.compose.runtime.mutableStateOf
import com.telakuR.easyorder.authentication.models.AuthUiState
import com.telakuR.easyorder.enums.RolesEnum
import com.telakuR.easyorder.ext.isValidEmail
import com.telakuR.easyorder.services.AccountService
import com.telakuR.easyorder.services.LogService
import com.telakuR.easyorder.viewModels.EasyOrderViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpVM @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : EasyOrderViewModel(logService) {
    var uiState = mutableStateOf(AuthUiState())
        private set

    private val _toastMessage = MutableStateFlow("")
    var toastMessage: StateFlow<String> = _toastMessage

    private val _shouldShowLoginView = MutableStateFlow(false)
    var shouldShowLoginView: StateFlow<Boolean> = _shouldShowLoginView

    private val email
        get() = uiState.value.email
    private val name
        get() = uiState.value.name
    private val password
        get() = uiState.value.password

    init {
        uiState.value = uiState.value.copy(role = getRoles()[0].role)
    }

    fun getRoles(): Array<RolesEnum> {
        return RolesEnum.values()
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

    fun onRoleChanged(newValue: String) {
        uiState.value = uiState.value.copy(role = newValue)
    }

    fun onSignUpClick(role: String) {
        if (!email.isValidEmail()) {
            _toastMessage.value = "Invalid email"
            _shouldShowLoginView.value = false
            return
        }

        launchCatching {
            val continueToLogin = accountService.createAccount(name, email, password, role)
            _shouldShowLoginView.value = continueToLogin
        }
    }
}