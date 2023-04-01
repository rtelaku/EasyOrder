package com.telakuR.easyorder.mainRepository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.mainRepository.UserDataRepository
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.services.AccountService
import com.telakuR.easyorder.utils.Constants.EMPLOYEES
import com.telakuR.easyorder.utils.Constants.PROFILE_PIC
import com.telakuR.easyorder.utils.Constants.ROLE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserDataRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val fireStore: FirebaseFirestore,
    private val accountService: AccountService
): UserDataRepository {

    private val TAG = UserDataRepositoryImpl::class.simpleName

    override suspend fun getUserProfilePicture(): String? = suspendCoroutine { continuation ->
        try {
            if(accountService.currentUser?.uid != null) {
                fireStore.collection(DBCollectionEnum.USERS.title)
                    .document(accountService.currentUserId).get().addOnSuccessListener {
                        val profilePic = it.get(PROFILE_PIC) as String
                        continuation.resume(profilePic)
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get profile picture: ", e)
        }
    }

    override suspend fun getUserRole(): String = suspendCoroutine { continuation ->
        try {
            fireStore.collection(DBCollectionEnum.USERS.title)
                .document(accountService.currentUserId).get().addOnSuccessListener {
                    val profilePic = it.get(ROLE) as String
                    continuation.resume(profilePic)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get role: ", e)
        }
    }

    override fun getProfile(): Flow<User> = flow {
        try {
            val doc = fireStore.collection(DBCollectionEnum.USERS.title).document(accountService.currentUserId).get().await()
            val user = Gson().fromJson(Gson().toJson(doc.data), User::class.java)
            emit(user)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get profile: ", e)
        }
    }.flowOn(ioDispatcher)

    override suspend fun getCompanyId(): String = suspendCoroutine { continuation ->
        try {
            fireStore.collection(DBCollectionEnum.EMPLOYEES.title).get().addOnSuccessListener { documents ->
                for(document in documents) {
                    val employees = document.data[EMPLOYEES] as ArrayList<String>
                    val hasCompanyId = employees.any { it == accountService.currentUserId }
                    if(hasCompanyId) continuation.resume(document.id)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get company name: ", e)
        }
    }

    override suspend fun isUserInACompany(): Boolean = suspendCoroutine { continuation ->
        try {
            fireStore.collection(DBCollectionEnum.EMPLOYEES.title).get().addOnSuccessListener { documents ->
                for(document in documents) {
                    val employees = document.data[EMPLOYEES] as ArrayList<String>
                    val isInACompany = employees.any { it == accountService.currentUserId }
                    continuation.resume(isInACompany)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get if user is in a company: ", e)
        }
    }
}