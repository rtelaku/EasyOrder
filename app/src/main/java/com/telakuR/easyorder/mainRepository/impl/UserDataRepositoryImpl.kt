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
import com.telakuR.easyorder.utils.Constants.TOKEN
import kotlinx.coroutines.*
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
            val currentUserId = accountService.currentUserId
            if (currentUserId.isNotEmpty()) {
                fireStore.collection(DBCollectionEnum.USERS.title)
                    .document(currentUserId).get().addOnSuccessListener {
                        val profilePic = it.get(PROFILE_PIC) as? String
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

    override suspend fun getProfile(): User? = suspendCoroutine { continuation ->
        fireStore.collection(DBCollectionEnum.USERS.title).document(accountService.currentUserId)
            .get().addOnSuccessListener {
                val user = Gson().fromJson(Gson().toJson(it.data), User::class.java)
                continuation.resume(user)
            }.addOnFailureListener {
                continuation.resume(null)
            }
    }

    override suspend fun getCompanyId(): String = suspendCoroutine { continuation ->
        try {
            fireStore.collection(DBCollectionEnum.EMPLOYEES.title).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val employees = document.data[EMPLOYEES] as ArrayList<String>
                        val hasCompanyId = employees.any { it == accountService.currentUserId }
                        if (hasCompanyId) continuation.resume(document.id)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get company name: ", e)
        }
    }

    override suspend fun isUserInACompany(): Boolean = suspendCoroutine { continuation ->
        try {
            fireStore.collection(DBCollectionEnum.EMPLOYEES.title).get().addOnSuccessListener { documents ->
                var count = 0
                for(document in documents) {
                    val employees = document.data[EMPLOYEES] as ArrayList<String>
                    employees.forEach {
                        if(it == accountService.currentUserId) {
                            count = 1
                        }
                    }
                }
                continuation.resume(count == 1)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get if user is in a company: ", e)
        }
    }

    override suspend fun getTokens(): List<String> = withContext(ioDispatcher) {
        try {
            val listOfTokens = arrayListOf<String>()
            val documents = fireStore.collection(DBCollectionEnum.EMPLOYEES.title).get().await()
            val deferredList = mutableListOf<Deferred<String?>>()
            for (document in documents) {
                val employees = document.data[EMPLOYEES] as ArrayList<String>
                val hasCompanyId = employees.any { it == accountService.currentUserId }
                if (hasCompanyId) {
                    employees.forEach {
                        val deferredToken = async {
                            val user = fireStore.collection(DBCollectionEnum.USERS.title).document(it).get().await()
                            user.get(TOKEN) as? String
                        }
                        deferredList.add(deferredToken)
                    }
                }
            }
            deferredList.awaitAll().forEach {
                if (it != null) {
                    listOfTokens.add(it)
                }
            }
            listOfTokens
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get device tokens: ", e)
            emptyList()
        }
    }
}