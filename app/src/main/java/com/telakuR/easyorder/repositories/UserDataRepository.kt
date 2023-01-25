package com.telakuR.easyorder.repositories

interface UserDataRepository {

    suspend fun getUserProfilePicture(): Any?

}