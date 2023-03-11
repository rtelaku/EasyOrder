package com.telakuR.easyorder.setupProfile.repository

import android.net.Uri
import com.telakuR.easyorder.models.Response
import com.telakuR.easyorder.models.User
import kotlinx.coroutines.flow.Flow

interface SetupProfileRepository {
    suspend fun addImageToFirebaseStorage(imageUri: Uri): Flow<Response<Uri>>

    suspend fun addImageToFirestore(downloadUrl: Uri): Flow<Response<Boolean>>

    suspend fun getImageFromFirestore(): Flow<Response<String>>

    suspend fun getCompanies(): Flow<List<User>>

    suspend fun requestToJoin(id: String)

    suspend fun removeRequest(id: String)

    suspend fun getRequestedCompany(): String?
}