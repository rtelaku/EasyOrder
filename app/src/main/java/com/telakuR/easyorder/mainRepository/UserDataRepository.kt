package com.telakuR.easyorder.mainRepository

import com.telakuR.easyorder.models.User
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    fun getProfile(): Flow<User>

    suspend fun getUserProfilePicture(): String?

    suspend fun getUserRole(): String

    suspend fun getCompanyId(): String

    suspend fun isUserInACompany(): Boolean
}