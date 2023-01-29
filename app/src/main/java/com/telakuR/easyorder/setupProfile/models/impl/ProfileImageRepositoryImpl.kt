package com.telakuR.easyorder.setupProfile.models.impl

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.telakuR.easyorder.Response
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.enums.RolesEnum
import com.telakuR.easyorder.repositories.impl.AccountServiceImpl
import com.telakuR.easyorder.setupProfile.repositories.ProfileImageRepository
import com.telakuR.easyorder.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.google.gson.Gson
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.utils.ToastUtils.showToast

@Singleton
class ProfileImageRepositoryImpl @Inject constructor(
    private val imagesStorageRef: StorageReference,
    private val fireStore: FirebaseFirestore,
    private val accountServiceImpl: AccountServiceImpl
) : ProfileImageRepository {

    private val TAG = ProfileImageRepositoryImpl::class.simpleName

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
            fireStore.collection(DBCollectionEnum.USERS.title).document(accountServiceImpl.currentUserId).update(Constants.PROFILE_PIC, downloadUrl).await()
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }

    override suspend fun getImageFromFirestore(): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            val url = fireStore.collection(DBCollectionEnum.USERS.title).document(accountServiceImpl.currentUserId).get().await().get(Constants.PROFILE_PIC).toString()
            emit(Response.Success(url))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }

    override suspend fun getCompanies(): Flow<List<User>> = flow {
        try {
            val list = arrayListOf<User>()
            val companies = fireStore.collection(DBCollectionEnum.USERS.title).whereEqualTo("role", RolesEnum.COMPANY.role).get().await()
            companies.forEach {
                val decodedCompanies = Gson().fromJson(Gson().toJson(it.data), User::class.java)
                list.add(decodedCompanies)
            }
            emit(list)
        } catch (e: Exception) {
            Log.e(TAG, "getCompanies: ", e)
        }
    }

    override suspend fun requestToJoin(email: String) {
        try {
            fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val docRef = snapshot.documents[0].reference
                        docRef.update("employees", FieldValue.arrayUnion(accountServiceImpl.currentUserId))
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "requestToJoin: ", e)
        }
    }

    override suspend fun removeRequest(email: String) {
        try {
            fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val docRef = snapshot.documents[0].reference
                        docRef.update("employees", FieldValue.arrayRemove(accountServiceImpl.currentUserId))
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "removeRequest: ", e)
        }
    }
}