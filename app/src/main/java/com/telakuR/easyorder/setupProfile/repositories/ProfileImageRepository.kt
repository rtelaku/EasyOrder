package com.telakuR.easyorder.setupProfile.repositories

import android.net.Uri
import com.telakuR.easyorder.Response
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.setupProfile.models.CompanyModel
import kotlinx.coroutines.flow.Flow

interface ProfileImageRepository {
    suspend fun addImageToFirebaseStorage(imageUri: Uri): Flow<Response<Uri>>

    suspend fun addImageToFirestore(downloadUrl: Uri): Flow<Response<Boolean>>

    suspend fun getImageFromFirestore(): Flow<Response<String>>

    suspend fun getCompanies(): Flow<List<User>>
}