package com.telakuR.easyorder.authentication.viewmodel

import androidx.compose.runtime.mutableStateOf
import com.telakuR.easyorder.R
import com.telakuR.easyorder.authentication.models.AuthUiState
import com.telakuR.easyorder.main.enums.RolesEnum
import com.telakuR.easyorder.main.ext.isValidEmail
import com.telakuR.easyorder.main.viewmodel.EasyOrderViewModel
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.main.services.AccountService
import com.telakuR.easyorder.main.services.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpVM @Inject constructor(
    private val accountService: AccountService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    logService: LogService
) : EasyOrderViewModel(logService) {
    var uiState = mutableStateOf(AuthUiState())
        private set

    private val _toastMessageId = MutableStateFlow<Int?>(null)
    var toastMessageId: StateFlow<Int?> = _toastMessageId

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
            _toastMessageId.value = R.string.invalid_email
            _shouldShowLoginView.value = false
            return
        }

        launchCatching {
            val currentUser = withContext(ioDispatcher) {
                accountService.createAccount(name, email, password, role)
            }

            if (currentUser != null) {
                accountService.signOut()
                _shouldShowLoginView.value = true
            }
        }
    }
}