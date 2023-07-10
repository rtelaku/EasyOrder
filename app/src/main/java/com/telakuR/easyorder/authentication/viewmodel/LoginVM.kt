package com.telakuR.easyorder.authentication.viewmodel

import androidx.compose.runtime.mutableStateOf
import com.telakuR.easyorder.R
import com.telakuR.easyorder.authentication.models.AuthUiState
import com.telakuR.easyorder.main.services.AccountService
import com.telakuR.easyorder.main.services.LogService
import com.telakuR.easyorder.main.viewmodel.EasyOrderViewModel
import com.telakuR.easyorder.modules.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val accountService: AccountService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    logService: LogService
) : EasyOrderViewModel(logService) {
    var uiState = mutableStateOf(AuthUiState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    private val _showHomeView = MutableStateFlow(false)
    var showHomeView: StateFlow<Boolean> = _showHomeView

    private val _toastMessageId = MutableStateFlow<Int?>(null)
    var toastMessageId: StateFlow<Int?> = _toastMessageId

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick() {
        launchCatching {
            val currentUser = async(ioDispatcher) {
                return@async accountService.authenticate(email, password)
            }.await()

            if (currentUser != null) {
//            if(currentUser.isEmailVerified) {
                accountService.generateToken()
                _showHomeView.value = true
//            } else {
//                if (currentUser.isEmailVerified) {
//                    _shouldShowHomeView.value = true
//                } else {
//                    _toastMessageId.value = R.string.please_verify_your_email
//                }
//            }
            } else {
                _toastMessageId.value = R.string.something_went_wrong
            }
        }
    }

    fun onForgotPasswordClick() {
        launchCatching {
            withContext(ioDispatcher) {
                accountService.sendRecoveryEmail(email)
            }
        }
    }
}