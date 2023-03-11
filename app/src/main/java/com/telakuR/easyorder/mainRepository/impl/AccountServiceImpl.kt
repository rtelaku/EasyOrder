package com.telakuR.easyorder.mainRepository.impl

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.telakuR.easyorder.R
import com.telakuR.easyorder.authentication.models.AuthUiState
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.enums.RolesEnum
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.services.AccountService
import com.telakuR.easyorder.utils.Constants.COMPANY_ID
import com.telakuR.easyorder.utils.Constants.EMAIL
import com.telakuR.easyorder.utils.Constants.EMPLOYEES
import com.telakuR.easyorder.utils.Constants.NAME
import com.telakuR.easyorder.utils.Constants.REQUESTS
import com.telakuR.easyorder.utils.ToastUtils.showToast
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AccountServiceImpl @Inject constructor(private val auth: FirebaseAuth, private val fireStore: FirebaseFirestore): AccountService {

    private val TAG = AccountServiceImpl::class.simpleName

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = auth.currentUser != null

    override var currentUser = auth.currentUser

    override suspend fun authenticate(email: String, password: String): FirebaseUser? {
        return auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                when (task.exception) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        showToast(messageId = R.string.incorrect_email_or_password, length = Toast.LENGTH_SHORT)
                    }
                    else -> {
                        showToast(messageId = R.string.something_went_wrong, length = Toast.LENGTH_SHORT)
                    }
                }
            }
        }.await().user
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

    override suspend fun createAccount(
        name: String,
        email: String,
        password: String,
        role: String
    ): FirebaseUser? {
        val emailExists = doesEmailExist(email)
        if (emailExists) {
            showToast(messageId = R.string.email_already_exists, length = Toast.LENGTH_SHORT)
        } else {
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                showToast(messageId = R.string.fill_all_fields, length = Toast.LENGTH_SHORT)
            } else {
                return auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        auth.currentUser?.let { currentUser ->

                            sendEmailVerification(currentUser)

                            val user = User(
                                name = name,
                                email = email,
                                role = role,
                                profilePic = ""
                            )

                            insertUser(currentUser = currentUser, user = user, role = role)
                        }
                    } else {
                        showToast(
                            messageId = R.string.account_creation_failed,
                            length = Toast.LENGTH_SHORT
                        )
                        Log.d(TAG, "Couldn't complete account creation ${it.exception}")
                    }
                }.await().user
            }
        }

        return null
    }

    private fun insertUser(user: User, role: String, currentUser: FirebaseUser) {
        val users = fireStore.collection(DBCollectionEnum.USERS.title)
        users.document(currentUser.uid).set(user)

        if (role == RolesEnum.COMPANY.role) {
            val employees = fireStore.collection(DBCollectionEnum.EMPLOYEES.title).document(currentUser.uid)
            employees.set(
                mapOf(
                    COMPANY_ID to currentUser.uid,
                    EMPLOYEES to emptyList<Map<String, Any>>(),
                    REQUESTS to emptyList<Map<String, Any>>()
                )
            )
//
//            val orders = fireStore.collection(DBCollectionEnum.ORDERS.title).document(currentUser.uid)
//            orders.set(mapOf(COMPANY_ID to currentUser.uid))
//
//            val employeeOrders = orders.collection(ORDERS).document()
//            employeeOrders.collection(ORDERED)
        }
    }

    private fun sendEmailVerification(currentUser: FirebaseUser) {
        currentUser.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast(
                    messageId = R.string.verification_email_sent,
                    length = Toast.LENGTH_SHORT
                )
            } else {
                showToast(
                    messageId = R.string.failed_sending_email_verification,
                    length = Toast.LENGTH_SHORT
                )
            }
        }
    }

    suspend fun doesEmailExist(email: String): Boolean = suspendCoroutine { continuation ->
        var count = 0
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (result != null && result.signInMethods != null && !result.signInMethods.isNullOrEmpty()) {
                        count = 1
                    }

                    continuation.resume(count == 1)
                }
            }
    }

    override suspend fun deleteAccount() {
        auth.currentUser?.delete()?.await()
    }

    override suspend fun signOut() {
       auth.signOut()
    }

    override suspend fun editProfile(profile: AuthUiState) {
        val userRef = fireStore.collection(DBCollectionEnum.USERS.title).document(currentUserId)

        if(doesEmailExist(email = profile.email)) {
            auth.currentUser?.updateEmail(profile.email)
            val updates = mapOf(EMAIL to profile.email)
            userRef.update(updates)
        } else {
            Handler(Looper.getMainLooper()).post {
                showToast(messageId = R.string.email_already_exists, length = Toast.LENGTH_SHORT)
            }
        }

        if(profile.password.isNotEmpty()) {
            auth.currentUser?.updatePassword(profile.password)
        } else {
            Handler(Looper.getMainLooper()).post {
                showToast(messageId = R.string.password_changing_failed, length = Toast.LENGTH_SHORT)
            }
        }

        if(profile.name.isNotEmpty()) {
            val updates = mapOf(NAME to profile.name)
            userRef.update(updates)
        } else {
            Handler(Looper.getMainLooper()).post {
                showToast(messageId = R.string.name_changing_failed, length = Toast.LENGTH_SHORT)
            }
        }
    }
}