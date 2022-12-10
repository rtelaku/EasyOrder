package com.telakuR.easyorder.authentication.models.impl

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.telakuR.easyorder.R
import com.telakuR.easyorder.authentication.models.services.AccountService
import com.telakuR.easyorder.utils.ToastUtils.showToast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore): AccountService {

    private val TAG = AccountServiceImpl::class.simpleName

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = auth.currentUser != null

    override val currentUser = auth.currentUser

    override suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun createAnonymousAccount() {
        auth.signInAnonymously().await()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun createAccount(email: String, password: String, role: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful) {
                val users = firestore.collection("users")

                val newUser = hashMapOf(
                    "email" to email,
                    "password" to password,
                    "role" to role
                )

                auth.currentUser?.uid?.let { id ->
                    users.document(id).set(newUser)

                    showToast(messageId = R.string.account_created_successfully, length = Toast.LENGTH_SHORT)
                }
            } else {
                showToast(messageId = R.string.account_created_successfully, length = Toast.LENGTH_SHORT)
                Log.d(TAG, "Couldn't complete acc creation ${it.exception}")
            }
        }

        if(auth.currentUser != null) {
            signOut()
        }
    }

    override suspend fun deleteAccount() {
        auth.currentUser?.delete()?.await()
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}