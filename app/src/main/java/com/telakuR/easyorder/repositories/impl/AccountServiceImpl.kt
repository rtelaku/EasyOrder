package com.telakuR.easyorder.repositories.impl

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.telakuR.easyorder.R
import com.telakuR.easyorder.authentication.models.AuthUiState
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.enums.RolesEnum
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.services.AccountService
import com.telakuR.easyorder.utils.ToastUtils.showToast
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AccountServiceImpl @Inject constructor(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore): AccountService {

    private val TAG = AccountServiceImpl::class.simpleName

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = auth.currentUser != null

    override val currentUser = auth.currentUser

    override suspend fun authenticate(email: String, password: String): FirebaseUser? {
        return auth.signInWithEmailAndPassword(email, password).await().user
    }

    override suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Email to update password sent.")
                showToast(
                    messageId = R.string.password_sent_to_email,
                    length = Toast.LENGTH_SHORT
                )
            }
        }.await()
    }

    override suspend fun createAccount(name: String, email: String, password: String, role: String): Boolean {
        val emailExists = doesEmailExist(email)
        if(emailExists) {
            showToast(
                messageId = R.string.email_already_exists,
                length = Toast.LENGTH_SHORT
            )
            return true
        } else {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {

                    val users = firestore.collection(DBCollectionEnum.USERS.title)
                    auth.currentUser?.let { currentUser ->

                        val user = User(
                            name = name,
                            email = email,
                            role = role,
                            profilePic = ""
                        )
                        users.document(currentUser.uid).set(user)

                        if (role == RolesEnum.COMPANY.role) {
                            val employees = firestore.collection(DBCollectionEnum.EMPLOYEES.title)
                            employees.document(currentUser.uid).set(
                                mapOf(
                                    "companyName" to name,
                                    "employees" to emptyList<Map<String, Any>>()
                                )
                            )
                        }

                        showToast(
                            messageId = R.string.account_created_successfully,
                            length = Toast.LENGTH_SHORT
                        )
                    }
                } else {
                    showToast(
                        messageId = R.string.account_created_successfully,
                        length = Toast.LENGTH_SHORT
                    )
                    Log.d(TAG, "Couldn't complete acc creation ${it.exception}")
                }
            }

            if (auth.currentUser != null) {
                signOut()
            }
        }

        return false
    }

    private suspend fun doesEmailExist(email: String): Boolean {
        val users = firestore.collection(DBCollectionEnum.USERS.title).whereEqualTo("email", email).get()
        return users.await().documents.size > 0
    }

    override suspend fun deleteAccount() {
        auth.currentUser?.delete()?.await()
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun editProfile(profile: AuthUiState) {

        val userRef = firestore.collection(DBCollectionEnum.USERS.title).document(currentUserId)

        if(doesEmailExist(email = profile.email)) {
            auth.currentUser?.updateEmail(profile.email)
            val updates = mapOf("email" to profile.email)
            userRef.update(updates)
        } else {
            showToast(message = "Email already exists. Try another!", length = Toast.LENGTH_SHORT)
        }

        if(profile.password.isNotEmpty()) {
            auth.currentUser?.updatePassword(profile.password)
        } else {
            showToast(message = "Password must not be empty to be changed", length = Toast.LENGTH_SHORT)
        }

        if(profile.name.isNotEmpty()) {
            val updates = mapOf("name" to profile.name)
            userRef.update(updates)
        } else {
            showToast(message = "Name must not be empty to be changed", length = Toast.LENGTH_SHORT)
        }
    }
}