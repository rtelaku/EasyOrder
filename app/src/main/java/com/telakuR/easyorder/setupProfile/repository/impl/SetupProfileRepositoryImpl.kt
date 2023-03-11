package com.telakuR.easyorder.setupProfile.repository.impl

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.enums.RolesEnum
import com.telakuR.easyorder.mainRepository.impl.AccountServiceImpl
import com.telakuR.easyorder.models.Response
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.setupProfile.repository.SetupProfileRepository
import com.telakuR.easyorder.utils.Constants.COMPANY_ID
import com.telakuR.easyorder.utils.Constants.PROFILE_PIC
import com.telakuR.easyorder.utils.Constants.REQUESTS
import com.telakuR.easyorder.utils.Constants.ROLE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class SetupProfileRepositoryImpl @Inject constructor(
    private val imagesStorageRef: StorageReference,
    private val fireStore: FirebaseFirestore,
    private val accountServiceImpl: AccountServiceImpl
) : SetupProfileRepository {

    private val TAG = SetupProfileRepositoryImpl::class.simpleName

    override suspend fun addImageToFirebaseStorage(imageUri: Uri) = flow {
        try {
            emit(Response.Loading)
            val downloadUrl = imagesStorageRef.child(accountServiceImpl.currentUserId)
                .putFile(imageUri).await()
                .storage.downloadUrl.await()
            emit(Response.Success(downloadUrl))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add image in storage: ", e)
        }
    }

    override suspend fun addImageToFirestore(downloadUrl: Uri) = flow {
        try {
            emit(Response.Loading)
            fireStore.collection(DBCollectionEnum.USERS.title).document(accountServiceImpl.currentUserId).update(PROFILE_PIC, downloadUrl).await()
            emit(Response.Success(true))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add image in firestore: ", e)
        }
    }

    override suspend fun getImageFromFirestore(): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            val url = fireStore.collection(DBCollectionEnum.USERS.title).document(accountServiceImpl.currentUserId).get().await().get(PROFILE_PIC).toString()
            emit(Response.Success(url))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get image from firestore: ", e)
        }
    }

    override suspend fun getCompanies(): Flow<List<User>> = flow {
        try {
            val list = arrayListOf<User>()
            val companies = fireStore.collection(DBCollectionEnum.USERS.title).whereEqualTo(ROLE, RolesEnum.COMPANY.role).get().await()
            companies.forEach {
                val decodedCompanies = Gson().fromJson(Gson().toJson(it.data), User::class.java)
                decodedCompanies.id = it.id
                list.add(decodedCompanies)
            }
            emit(list)
        } catch (e: Exception) {
            Log.e(TAG, "Get companies: ", e)
        }
    }

    override suspend fun requestToJoin(id: String) {
        try {
            fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
                .whereEqualTo(COMPANY_ID, id)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val docRef = snapshot.documents[0].reference
                        docRef.update(REQUESTS, FieldValue.arrayUnion(accountServiceImpl.currentUserId))
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Request to join: ", e)
        }
    }

    override suspend fun removeRequest(id: String) {
        try {
            fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
                .whereEqualTo(COMPANY_ID, id)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val docRef = snapshot.documents[0].reference
                        docRef.update(REQUESTS, FieldValue.arrayRemove(accountServiceImpl.currentUserId))
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Remove request: ", e)
        }
    }

    override suspend fun getRequestedCompany(): String? = suspendCoroutine { continuation ->
        try {
            fireStore.collection(DBCollectionEnum.EMPLOYEES.title).get()
                .addOnSuccessListener { snapshot ->
                    var count = 0
                    for(sn in snapshot) {
                        val company = Gson().fromJson(Gson().toJson(sn.data), User::class.java)
                        val requests = sn.data[REQUESTS] as ArrayList<String>
                        requests.forEach {
                            if(it == accountServiceImpl.currentUserId) {
                                count = 1
                            }

                            if(count == 1) {
                                continuation.resume(company.email)
                            }
                        }
                    }
                    if(count == 0) {
                        continuation.resume(null)
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get requested company: ", e)
        }
    }
}