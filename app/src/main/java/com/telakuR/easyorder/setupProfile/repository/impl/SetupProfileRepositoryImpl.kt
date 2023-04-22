package com.telakuR.easyorder.setupProfile.repository.impl

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.enums.RolesEnum
import com.telakuR.easyorder.models.Response
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.services.AccountService
import com.telakuR.easyorder.setupProfile.repository.SetupProfileRepository
import com.telakuR.easyorder.utils.Constants.PROFILE_PIC
import com.telakuR.easyorder.utils.Constants.REQUESTS
import com.telakuR.easyorder.utils.Constants.ROLE
import com.telakuR.easyorder.utils.EasyOrderPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetupProfileRepositoryImpl @Inject constructor(
    private val imagesStorageRef: StorageReference,
    private val fireStore: FirebaseFirestore,
    private val accountService: AccountService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SetupProfileRepository {

    private val TAG = SetupProfileRepositoryImpl::class.simpleName

    override fun addImageToFirebaseStorage(imageUri: Uri) = flow {
        try {
            emit(Response.Loading)
            val downloadUrl = imagesStorageRef.child(accountService.currentUserId)
                .putFile(imageUri).await()
                .storage.downloadUrl.await()
            emit(Response.Success(downloadUrl))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add image in storage: ", e)
        }
    }.flowOn(ioDispatcher)

    override fun addImageToFirestore(downloadUrl: Uri) = flow {
        try {
            emit(Response.Loading)
            fireStore.collection(DBCollectionEnum.USERS.title).document(accountService.currentUserId).update(PROFILE_PIC, downloadUrl).await()
            emit(Response.Success(true))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add image in firestore: ", e)
        }
    }.flowOn(ioDispatcher)

    override fun getImageFromFirestore(): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            val url = fireStore.collection(DBCollectionEnum.USERS.title).document(accountService.currentUserId).get().await().get(PROFILE_PIC).toString()
            emit(Response.Success(url))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get image from firestore: ", e)
        }
    }.flowOn(ioDispatcher)

    override fun getCompanies(): Flow<List<User>> = flow {
        try {
            val list = arrayListOf<User>()
            val companies = fireStore.collection(DBCollectionEnum.USERS.title).whereEqualTo(ROLE, RolesEnum.COMPANY.role).get().await()
            companies.forEach {
                val decodedCompany = Gson().fromJson(Gson().toJson(it.data), User::class.java)
                decodedCompany.id = it.id
                list.add(decodedCompany)
            }
            emit(list)
        } catch (e: Exception) {
            Log.e(TAG, "Get companies: ", e)
        }
    }.flowOn(ioDispatcher)

    override suspend fun requestToJoin(id: String) {
        fireStore.collection(DBCollectionEnum.EMPLOYEES.title).document(id)
            .update(REQUESTS, FieldValue.arrayUnion(accountService.currentUserId))
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't request to join: ", exception)
            }
    }

    override suspend fun removeRequest(id: String) {
        fireStore.collection(DBCollectionEnum.EMPLOYEES.title).document(id)
            .update(REQUESTS, FieldValue.arrayRemove(accountService.currentUserId))
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't remove request: ", exception)
            }
    }

    override fun getRequestedCompanyId(): String {
        return EasyOrderPreferences.getRequestedCompanyId()
    }

    override fun saveCompanyIdToPreferences(companyId: String) {
        EasyOrderPreferences.saveRequestedCompanyId(companyId = companyId)
    }
}