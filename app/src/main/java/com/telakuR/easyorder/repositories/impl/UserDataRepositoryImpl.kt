package com.telakuR.easyorder.repositories.impl

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.repositories.UserDataRepository
import com.telakuR.easyorder.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    val firestore: FirebaseFirestore,
    val accountServiceImpl: AccountServiceImpl
): UserDataRepository {

    private val TAG = UserDataRepositoryImpl::class.simpleName

    override suspend fun getUserProfilePicture(): String? {
        if(accountServiceImpl.currentUser?.uid != null) {
            val profilePic = firestore.collection(DBCollectionEnum.USERS.title)
                .document(accountServiceImpl.currentUserId)
                .get().await().get(Constants.PROFILE_PIC).toString()

            return Gson().fromJson(Gson().toJson(profilePic), String::class.java)
        }

        return null
    }

    override suspend fun getUserRole(): String {
        val role = firestore.collection(DBCollectionEnum.USERS.title)
            .document(accountServiceImpl.currentUserId)
            .get().await().get("role")
        return Gson().fromJson(Gson().toJson(role), String::class.java)
    }

    override suspend fun getProfile(): Flow<User> = flow {
        try {
            val doc = firestore.collection(DBCollectionEnum.USERS.title).document(accountServiceImpl.currentUserId).get().await()
            val user = Gson().fromJson(Gson().toJson(doc.data), User::class.java)
            Log.d("rigiii", "getProfile: $user")
            emit(user)
        } catch (e: Exception) {
            Log.e(TAG, "Couldnt get profile: ", e)
        }
    }
}