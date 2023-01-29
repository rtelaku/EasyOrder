package com.telakuR.easyorder.services

import com.google.firebase.auth.FirebaseUser

interface AccountService {
    val currentUserId: String

    val hasUser: Boolean

    val currentUser: FirebaseUser?

    suspend fun authenticate(email: String, password: String): FirebaseUser?

    suspend fun sendRecoveryEmail(email: String)

    suspend fun createAccount(name: String, email: String, password: String, role: String): Boolean

    suspend fun deleteAccount()

    suspend fun signOut()
}