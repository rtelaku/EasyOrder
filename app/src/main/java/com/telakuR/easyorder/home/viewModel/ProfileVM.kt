package com.telakuR.easyorder.home.viewModel

import androidx.compose.runtime.mutableStateOf
import com.telakuR.easyorder.authentication.models.AuthUiState
import com.telakuR.easyorder.authentication.models.AuthenticationRoute
import com.telakuR.easyorder.main.repository.UserDataRepository
import com.telakuR.easyorder.main.services.AccountService
import com.telakuR.easyorder.main.services.LogService
import com.telakuR.easyorder.main.viewmodel.EasyOrderViewModel
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.room_db.enitites.Profile
import com.telakuR.easyorder.setupProfile.route.SetUpProfileRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileVM @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val accountService: AccountService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    logService: LogService
) : EasyOrderViewModel(logService) {

    var uiState = mutableStateOf(AuthUiState())
        private set

    private val _profile = MutableStateFlow<Profile?>(null)
    var profile: StateFlow<Profile?> = _profile

    private val _screenToSetup = MutableStateFlow("")
    var screenToSetup: StateFlow<String> = _screenToSetup

    init {
        setScreenToLaunch()
    }

    private fun setScreenToLaunch() = launchCatching {
        val profilePic = userDataRepository.getUserProfilePicture()

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
            userDataRepository.getProfileFlow().collect { userProfile ->
                if (userProfile != null) {
                    userDataRepository.saveProfileOnDB(userProfile)
                }
            }
        }
    }

    fun getProfileFromDB() = launchCatching {
        val userProfile = userDataRepository.getProfileFromDB()
        uiState.value = uiState.value.copy(name = userProfile?.name ?: "", email = userProfile?.email ?: "")
        _profile.value = userProfile
    }

    fun editProfile(currentPassword: String) {
        launchCatching {
            withContext(ioDispatcher) {
                accountService.editProfile(profile = uiState.value, currentPassword = currentPassword)
            }
        }
    }

    fun logOut() {
        launchCatching {
            accountService.signOut()
            _screenToSetup.value = AuthenticationRoute.Login.route
        }
    }
}