package com.telakuR.easyorder.mainRepository

import com.telakuR.easyorder.models.User

interface UserDataRepository {

    suspend fun getProfile(): User?

    suspend fun getUserProfilePicture(): String?

    suspend fun getUserRole(): String

    suspend fun getCompanyId(): String

    suspend fun isUserInACompany(): Boolean

    suspend fun getTokens(): List<String>
}