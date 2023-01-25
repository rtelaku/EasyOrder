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
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.repositories.impl.AccountServiceImpl
import com.telakuR.easyorder.setupProfile.models.impl.ProfileImageRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetUpProfileViewModel @Inject constructor(
    val accountServiceImpl: AccountServiceImpl,
    private val repositoryImpl: ProfileImageRepositoryImpl,
    @IoDispatcher val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    var addImageToStorageResponse by mutableStateOf<Response<Uri>>(Success(null))
        private set

    var addImageToDatabaseResponse by mutableStateOf<Response<Boolean>>(Success(null))
        private set

    var getImageFromDatabaseResponse by mutableStateOf<Response<String>>(Success(null))
        private set

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
}