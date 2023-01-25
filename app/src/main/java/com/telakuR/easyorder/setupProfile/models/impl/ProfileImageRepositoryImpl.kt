package com.telakuR.easyorder.setupProfile.models.impl

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.telakuR.easyorder.Response
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.repositories.impl.AccountServiceImpl
import com.telakuR.easyorder.setupProfile.repositories.ProfileImageRepository
import com.telakuR.easyorder.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileImageRepositoryImpl @Inject constructor(
    private val imagesStorageRef: StorageReference,
    private val imagesCollRef: FirebaseFirestore,
    private val accountServiceImpl: AccountServiceImpl
) : ProfileImageRepository {
    override suspend fun addImageToFirebaseStorage(imageUri: Uri) = flow {
        try {
            emit(Response.Loading)
            val downloadUrl = imagesStorageRef.child(accountServiceImpl.currentUserId)
                .putFile(imageUri).await()
                .storage.downloadUrl.await()
            emit(Response.Success(downloadUrl))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }

    override suspend fun addImageToFirestore(downloadUrl: Uri) = flow {
        try {
            emit(Response.Loading)
            imagesCollRef.collection(DBCollectionEnum.USERS.title).document(accountServiceImpl.currentUserId).update(Constants.PROFILE_PIC, downloadUrl).await()
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }

    override suspend fun getImageFromFirestore(): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            val url = imagesCollRef.collection(DBCollectionEnum.USERS.title).document(accountServiceImpl.currentUserId).get().await().get(Constants.PROFILE_PIC).toString()
            emit(Response.Success(url))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }
}