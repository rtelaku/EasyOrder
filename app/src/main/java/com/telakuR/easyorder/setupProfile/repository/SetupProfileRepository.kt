package com.telakuR.easyorder.setupProfile.repository

import android.net.Uri
import com.telakuR.easyorder.models.Response
import com.telakuR.easyorder.models.User
import kotlinx.coroutines.flow.Flow

interface  SetupProfileRepository {
    fun addImageToFirebaseStorage(imageUri: Uri): Flow<Response<Uri>>

    fun addImageToFirestore(downloadUrl: Uri): Flow<Response<Boolean>>

    fun getImageFromFirestore(): Flow<Response<String>>

    fun getCompanies(): Flow<List<User>>

    fun getRequestedCompanyId(): String

    fun saveCompanyIdToPreferences(companyId: String)

    suspend fun requestToJoin(id: String)

    suspend fun removeRequest(id: String)

}