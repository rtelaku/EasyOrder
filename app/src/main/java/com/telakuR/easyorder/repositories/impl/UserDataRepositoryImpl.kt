package com.telakuR.easyorder.repositories.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.repositories.UserDataRepository
import com.telakuR.easyorder.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    val firestore: FirebaseFirestore,
    val accountServiceImpl: AccountServiceImpl
): UserDataRepository {

    override suspend fun getUserProfilePicture(): Any? {
        if(accountServiceImpl.currentUser?.uid != null) {
            return firestore.collection(DBCollectionEnum.USERS.title)
                .document(accountServiceImpl.currentUserId)
                .get().await().get(Constants.PROFILE_PIC)
        }

        return null
    }
}