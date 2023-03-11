package com.telakuR.easyorder.home.viewModel

import androidx.compose.runtime.mutableStateOf
import com.telakuR.easyorder.authentication.models.AuthUiState
import com.telakuR.easyorder.authentication.models.AuthenticationRoute
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.mainRepository.impl.AccountServiceImpl
import com.telakuR.easyorder.mainRepository.impl.UserDataRepositoryImpl
import com.telakuR.easyorder.services.LogService
import com.telakuR.easyorder.setupProfile.route.SetUpProfileRoute
import com.telakuR.easyorder.mainViewModel.EasyOrderViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileVM @Inject constructor(
    private val userDataRepositoryImpl: UserDataRepositoryImpl,
    private val accountServiceImpl: AccountServiceImpl,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    logService: LogService
) : EasyOrderViewModel(logService) {

    var uiState = mutableStateOf(AuthUiState())
        private set

    private val _profile = MutableStateFlow<User?>(null)
    var profile: StateFlow<User?> = _profile

    private val _screenToSetup = MutableStateFlow("")
    var screenToSetup: StateFlow<String> = _screenToSetup

    init {
        setScreenToLaunch()
    }

    private fun setScreenToLaunch() = launchCatching {
        val profilePic = userDataRepositoryImpl.getUserProfilePicture()

        if (profilePic.isNullOrEmpty()) {
            _screenToSetup.value = SetUpProfileRoute.SelectPicture.route
        }
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

    fun getProfile() {
        launchCatching {
            withContext(ioDispatcher) {
                userDataRepositoryImpl.getProfile()
            }.collect {
                _profile.value = it
                uiState.value =
                    uiState.value.copy(name = profile.value?.name ?: "", email = profile.value?.email ?: "")
            }
        }
    }

    fun editProfile() {
        launchCatching {
            withContext(ioDispatcher) {
                accountServiceImpl.editProfile(uiState.value)

                delay(1000)
                getProfile()
            }
        }
    }

    fun logOut() {
        launchCatching {
            accountServiceImpl.signOut()
            _screenToSetup.value = AuthenticationRoute.Login.route
        }
    }
}