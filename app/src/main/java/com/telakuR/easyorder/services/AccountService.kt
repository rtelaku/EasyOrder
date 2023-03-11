package com.telakuR.easyorder.services

import com.google.firebase.auth.FirebaseUser
import com.telakuR.easyorder.authentication.models.AuthUiState

interface AccountService {
    val currentUserId: String

    val hasUser: Boolean

    var currentUser: FirebaseUser?

    suspend fun authenticate(email: String, password: String): FirebaseUser?

    suspend fun sendRecoveryEmail(email: String)

    suspend fun createAccount(name: String, email: String, password: String, role: String): FirebaseUser?

    suspend fun deleteAccount()

    suspend fun signOut()

    suspend fun editProfile(profile: AuthUiState)
}