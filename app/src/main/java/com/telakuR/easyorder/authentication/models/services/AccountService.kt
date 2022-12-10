package com.telakuR.easyorder.authentication.models.services

import com.google.firebase.auth.FirebaseUser

interface AccountService {
    val currentUserId: String

    val hasUser: Boolean

    val currentUser: FirebaseUser?

    suspend fun authenticate(email: String, password: String)

    suspend fun sendRecoveryEmail(email: String)

    suspend fun createAnonymousAccount()

    suspend fun createAccount(email: String, password: String, role: String)

    suspend fun deleteAccount()

    suspend fun signOut()
}