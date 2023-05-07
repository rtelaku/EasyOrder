package com.telakuR.easyorder.main.repository

import com.telakuR.easyorder.main.models.User
import com.telakuR.easyorder.room_db.enitites.Profile
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    fun getProfileFlow(): Flow<User?>

    suspend fun getProfileFromDB(): Profile?

    suspend fun saveProfileOnDB(userProfile: User)

    suspend fun getUserProfilePicture(): String?

    suspend fun getCompanyId(): String?

    suspend fun getTokens(companyId: String): List<String>

    suspend fun getOrderOwnerDeviceToken(ownerId: String): String?

    suspend fun setCompanyId(userCompanyId: String?)

    suspend fun getCompanyIdFromAPI(): String
}