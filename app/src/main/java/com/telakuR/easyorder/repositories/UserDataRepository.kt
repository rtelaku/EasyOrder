package com.telakuR.easyorder.repositories

import com.telakuR.easyorder.models.User
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    suspend fun getUserProfilePicture(): Any?

    suspend fun getUserRole(): String

    suspend fun getProfile(): Flow<User>

}