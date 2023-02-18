package com.telakuR.easyorder.home.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.repositories.impl.AccountServiceImpl
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HomeDataRepositoryImpl @Inject constructor(val firestore: FirebaseFirestore, val accountServiceImpl: AccountServiceImpl): HomeRepository {
    private val TAG = HomeDataRepositoryImpl::class.simpleName

    override suspend fun getEmployeeEmails(): List<String> = suspendCoroutine { continuation ->
        try {
            firestore.collection(DBCollectionEnum.EMPLOYEES.title).whereEqualTo("email", "team@solaborate.com").limit(1).get().addOnSuccessListener {
                it.documents.forEach { company ->
                val employees = company.data?.get("employees") as ArrayList<String>
                continuation.resume(employees)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get Employees: ", e)
        }
    }

    override suspend fun getEmployees(requests: List<String>): Flow<List<User>> = flow {
        try {
            val users = mutableListOf<User>()
            coroutineScope {
                launch {
                    requests.forEach { email ->
                        firestore.collection(DBCollectionEnum.USERS.title)
                            .whereEqualTo("email", email)
                            .limit(1)
                            .get()
                            .addOnSuccessListener { result ->
                                result.documents.map { userDoc ->
                                    val user = Gson().fromJson(Gson().toJson(userDoc.data), User::class.java)
                                    users.add(user)
                                }
                            }
                    }
                }
            }

            delay(1000)
            emit(users)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get Employees: ", e)
        }
    }

    override suspend fun removeEmployee(email: String) {
        try {
            firestore.collection(DBCollectionEnum.EMPLOYEES.title)
                .whereEqualTo("email", "team@solaborate.com")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val docRef = snapshot.documents[0].reference
                        docRef.update("requests", FieldValue.arrayRemove(email))
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "remove employee: ", e)
        }
    }

    override suspend fun getRequestsEmails(): List<String> = suspendCoroutine { continuation ->
        try {
            firestore.collection(DBCollectionEnum.EMPLOYEES.title).whereEqualTo("email", "team@solaborate.com").limit(1).get().addOnSuccessListener {
                it.documents.forEach { company ->
                    val requests = company.data?.get("requests") as ArrayList<String>
                    continuation.resume(requests)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get Request Emails: ", e)
        }
    }

    override suspend fun getRequests(requestsEmails: List<String>): Flow<List<User>> = flow {
        try {
            val users = mutableListOf<User>()
            coroutineScope {
                launch {
                    requestsEmails.forEach { email ->
                        firestore.collection(DBCollectionEnum.USERS.title)
                            .whereEqualTo("email", email)
                            .limit(1)
                            .get()
                            .addOnSuccessListener { result ->
                                result.documents.map { userDoc ->
                                    val user = Gson().fromJson(Gson().toJson(userDoc.data), User::class.java)
                                    users.add(user)
                                }
                            }
                    }
                }
            }

            delay(1000)
            emit(users)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get Requests: ", e)
        }
    }

    override suspend fun acceptRequest(email: String) {
        try {
            firestore.collection(DBCollectionEnum.EMPLOYEES.title)
                .whereEqualTo("email", "team@solaborate.com")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val docRef = snapshot.documents[0].reference
                        docRef.update("requests", FieldValue.arrayRemove(email))
                        docRef.update("employees", FieldValue.arrayUnion(email))
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "add employee: ", e)
        }
    }

    override suspend fun removeRequest(email: String) {
        try {
            firestore.collection(DBCollectionEnum.EMPLOYEES.title)
                .whereEqualTo("email", "team@solaborate.com")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val docRef = snapshot.documents[0].reference
                        docRef.update("requests", FieldValue.arrayRemove(email))
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "removeRequest: ", e)
        }
    }
}