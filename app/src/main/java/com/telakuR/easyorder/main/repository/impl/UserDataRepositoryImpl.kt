package com.telakuR.easyorder.main.repository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.telakuR.easyorder.main.enums.DBCollectionEnum
import com.telakuR.easyorder.main.models.User
import com.telakuR.easyorder.main.models.mapUserToProfile
import com.telakuR.easyorder.main.repository.UserDataRepository
import com.telakuR.easyorder.main.services.AccountService
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.room_db.db.EasyOrderDB
import com.telakuR.easyorder.room_db.enitites.Profile
import com.telakuR.easyorder.utils.Constants.EMPLOYEES
import com.telakuR.easyorder.utils.Constants.PROFILE_PIC
import com.telakuR.easyorder.utils.Constants.TOKEN
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserDataRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val fireStore: FirebaseFirestore,
    private val accountService: AccountService,
    private val easyOrderDB: EasyOrderDB
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

    override fun getProfileFlow(): Flow<User?> = callbackFlow {
        val listener = fireStore.collection(DBCollectionEnum.USERS.title)
            .document(accountService.currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null).isSuccess
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = Gson().fromJson(Gson().toJson(snapshot.data), User::class.java)
                    trySend(user).isSuccess
                } else {
                    trySend(null).isSuccess
                }
            }

        // Cancel the listener when the flow is cancelled
        awaitClose {
            listener.remove()
        }
    }

    override suspend fun getProfileFromDB(): Profile? {
        return easyOrderDB.profileDao().getProfile().firstOrNull()
    }

    override suspend fun saveProfileOnDB(userProfile: User) {
        val profile = userProfile.mapUserToProfile()
        easyOrderDB.profileDao().deleteAndInsertProfile(profile)
    }

    override suspend fun getCompanyId(): String? {
        return getProfileFromDB()?.companyId
    }

    override suspend fun getCompanyIdFromAPI(): String = suspendCoroutine { continuation ->
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

    override suspend fun getTokens(companyId: String): List<String> = withContext(ioDispatcher) {
        try {
            val listOfTokens = arrayListOf<String>()
            val document = fireStore.collection(DBCollectionEnum.EMPLOYEES.title).document(companyId).get().await()
            val deferredList = mutableListOf<Deferred<String?>>()
            val employees = document.data?.get(EMPLOYEES) as? ArrayList<String>
            employees?.forEach {
                val deferredToken = async {
                    val user = fireStore.collection(DBCollectionEnum.USERS.title).document(it).get().await()
                    user.get(TOKEN) as? String
                }
                deferredList.add(deferredToken)
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

    override suspend fun getOrderOwnerDeviceToken(ownerId: String): String? = suspendCoroutine { continuation ->
        fireStore.collection(DBCollectionEnum.USERS.title).document(ownerId).get()
            .addOnSuccessListener { snapShot ->
                val token = snapShot.get(TOKEN) as? String
                continuation.resume(token)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Couldn't get device token: ", e)
                continuation.resume(null)
            }
    }

    override suspend fun setCompanyId(userCompanyId: String?) {
        easyOrderDB.profileDao().setCompanyId(companyId = userCompanyId ?: "")
    }
}