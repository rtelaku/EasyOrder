package com.telakuR.easyorder.main.repository

import com.telakuR.easyorder.main.models.User
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    fun getProfileFlow(): Flow<User?>

    suspend fun getProfile(): User?

    suspend fun getUserProfilePicture(): String?

    suspend fun getUserRole(): String

    suspend fun getCompanyId(): String

    suspend fun isUserInACompany(): Boolean

    suspend fun getTokens(companyId: String): List<String>

    suspend fun getOrderOwnerDeviceToken(ownerId: String): String?
}