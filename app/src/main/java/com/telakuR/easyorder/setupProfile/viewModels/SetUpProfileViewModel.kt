package com.telakuR.easyorder.setupProfile.viewModels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telakuR.easyorder.Response
import com.telakuR.easyorder.Response.Loading
import com.telakuR.easyorder.Response.Success
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.repositories.impl.UserDataRepositoryImpl
import com.telakuR.easyorder.setupProfile.models.impl.ProfileImageRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetUpProfileViewModel @Inject constructor(
    private val repositoryImpl: ProfileImageRepositoryImpl,
    private val userDataRepositoryImpl: UserDataRepositoryImpl,
    @IoDispatcher val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    var addImageToStorageResponse by mutableStateOf<Response<Uri>>(Success(null))
        private set

    var addImageToDatabaseResponse by mutableStateOf<Response<Boolean>>(Success(null))
        private set

    var getImageFromDatabaseResponse by mutableStateOf<Response<String>>(Success(null))
        private set

    var companies = MutableStateFlow<List<User>>(emptyList())
        private set

    var currentUserRole = MutableStateFlow("")
        private set

    init {
        viewModelScope.launch(ioDispatcher) {
            currentUserRole.value = userDataRepositoryImpl.getUserRole()
        }
    }

    fun addImageToStorage(imageUri: Uri) = viewModelScope.launch(ioDispatcher) {
        addImageToStorageResponse = Loading
       repositoryImpl.addImageToFirebaseStorage(imageUri).collect {
           addImageToStorageResponse = it
       }
    }

    fun addImageToDatabase(downloadUrl: Uri) = viewModelScope.launch(ioDispatcher) {
        addImageToDatabaseResponse = Loading
        repositoryImpl.addImageToFirestore(downloadUrl).collect {
            addImageToDatabaseResponse = it
        }
    }

    fun getImageFromDatabase() = viewModelScope.launch(ioDispatcher) {
        getImageFromDatabaseResponse = Loading
        repositoryImpl.getImageFromFirestore().collect {
            getImageFromDatabaseResponse = it
        }
    }

    fun getCompanies() {
        viewModelScope.launch {
            repositoryImpl.getCompanies().collect {
                companies.value = it
            }
        }
    }

    fun handleRequestState(email: String, state: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            if(state) {
                repositoryImpl.requestToJoin(email)
            } else {
                repositoryImpl.removeRequest(email)
            }
        }
    }
}