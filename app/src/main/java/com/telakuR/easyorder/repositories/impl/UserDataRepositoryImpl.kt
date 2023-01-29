package com.telakuR.easyorder.repositories.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.repositories.UserDataRepository
import com.telakuR.easyorder.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    val firestore: FirebaseFirestore,
    val accountServiceImpl: AccountServiceImpl
): UserDataRepository {

    override suspend fun getUserProfilePicture(): String? {
        if(accountServiceImpl.currentUser?.uid != null) {
            val profilePic = firestore.collection(DBCollectionEnum.USERS.title)
                .document(accountServiceImpl.currentUserId)
                .get().await().get(Constants.PROFILE_PIC).toString()
            Log.d("rigii", "getUserProfilePicture: $profilePic")

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
}