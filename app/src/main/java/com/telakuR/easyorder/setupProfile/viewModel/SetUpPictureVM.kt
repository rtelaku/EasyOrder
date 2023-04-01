package com.telakuR.easyorder.setupProfile.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.telakuR.easyorder.mainRepository.UserDataRepository
import com.telakuR.easyorder.mainViewModel.EasyOrderViewModel
import com.telakuR.easyorder.models.Response
import com.telakuR.easyorder.models.Response.Loading
import com.telakuR.easyorder.models.Response.Success
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.services.LogService
import com.telakuR.easyorder.setupProfile.repository.SetupProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SetUpPictureVM @Inject constructor(
    private val setupProfileRepository: SetupProfileRepository,
    private val userDataRepository: UserDataRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _currentUserRole = MutableStateFlow("")
    var currentUserRole: StateFlow<String> = _currentUserRole

    private val _shouldShowFindCompanyScreen = MutableStateFlow(true)
    var shouldShowFindCompanyScreen: StateFlow<Boolean> = _shouldShowFindCompanyScreen

    var addImageToStorageResponse by mutableStateOf<Response<Uri>>(Success(null))
        private set

    var addImageToDatabaseResponse by mutableStateOf<Response<Boolean>>(Success(null))
        private set

    var getImageFromDatabaseResponse by mutableStateOf<Response<String>>(Success(null))
        private set

    init {
         launchCatching {
             checkIfUserHasSetupPic()

             val userRole = withContext(ioDispatcher) {
                 userDataRepository.getUserRole()
             }

             _currentUserRole.value = userRole
        }
    }

    private suspend fun checkIfUserHasSetupPic() {
        val pic = userDataRepository.getUserProfilePicture()

        if (pic.isNullOrEmpty()) {
            _shouldShowFindCompanyScreen.value = false
        }
    }

    fun addImageToStorage(imageUri: Uri) = launchCatching {
        addImageToStorageResponse = Loading
        setupProfileRepository.addImageToFirebaseStorage(imageUri).collect {
            addImageToStorageResponse = it
        }
    }

    fun addImageToDatabase(downloadUrl: Uri) = launchCatching {
        addImageToDatabaseResponse = Loading
        setupProfileRepository.addImageToFirestore(downloadUrl).collect {
            addImageToDatabaseResponse = it
        }
    }

    fun getImageFromDatabase() = launchCatching {
        getImageFromDatabaseResponse = Loading
        setupProfileRepository.getImageFromFirestore().collect {
            getImageFromDatabaseResponse = it
        }
    }
}