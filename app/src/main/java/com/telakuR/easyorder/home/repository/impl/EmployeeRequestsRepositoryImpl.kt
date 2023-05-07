package com.telakuR.easyorder.home.repository.impl

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.telakuR.easyorder.home.repository.EmployeeRequestsRepository
import com.telakuR.easyorder.main.enums.DBCollectionEnum
import com.telakuR.easyorder.main.services.AccountService
import com.telakuR.easyorder.main.services.MyFirebaseMessagingService
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.room_db.db.EasyOrderDB
import com.telakuR.easyorder.room_db.enitites.EmployeeRequest
import com.telakuR.easyorder.utils.Constants
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmployeeRequestsRepositoryImpl @Inject constructor(
    @IoDispatcher val ioDispatcher: CoroutineDispatcher,
    private val fireStore: FirebaseFirestore,
    private val accountService: AccountService,
    private val easyOrderDB: EasyOrderDB
) : EmployeeRequestsRepository {

    private val TAG = EmployeeRequestsRepositoryImpl::class.simpleName

    override fun getEmployeeRequestsFromAPI(): Flow<List<EmployeeRequest>> = callbackFlow {
        val employeeRef = fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
            .document(accountService.currentUserId)

        val subscription = employeeRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.d(TAG, "Couldn't get requests: $exception")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val requestsList = snapshot.get(Constants.REQUESTS) as ArrayList<String>

                val users = mutableListOf<EmployeeRequest>()
                val documents = mutableListOf<DocumentSnapshot>()

                val deferredDocuments = requestsList.map { id ->
                    async {
                        fireStore.collection(DBCollectionEnum.USERS.title).document(id).get()
                            .await()
                    }
                }

                runBlocking {
                    documents.addAll(deferredDocuments.awaitAll())
                }

                documents.forEachIndexed { index, document ->
                    val user = Gson().fromJson(Gson().toJson(document.data), EmployeeRequest::class.java)
                    user.id = requestsList[index]
                    users.add(user)
                }

                this.trySend(users).isSuccess
            } else {
                this.trySend(emptyList<EmployeeRequest>()).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }.flowOn(ioDispatcher)

    override fun getEmployeeRequestsFromDB(): Flow<List<EmployeeRequest>> {
        return easyOrderDB.companyRequestsDao().getCompanyEmployeeRequests()
    }

    override suspend fun acceptRequest(id: String) {
        fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
            .document(accountService.currentUserId)
            .update(
                Constants.REQUESTS, FieldValue.arrayRemove(id),
                Constants.EMPLOYEES, FieldValue.arrayUnion(id)
            )
            .addOnSuccessListener {
                MyFirebaseMessagingService.sendRequestStateMessage(ownerId = accountService.currentUserId, employeeId = id, hasBeenAccepted = true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't accept request: ", exception)
            }
    }

    override suspend fun removeRequest(id: String) {
        fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
            .document(accountService.currentUserId)
            .update(Constants.REQUESTS, FieldValue.arrayRemove(id))
            .addOnSuccessListener {
                MyFirebaseMessagingService.sendRequestStateMessage(
                    employeeId = id,
                    hasBeenAccepted = false,
                    ownerId = accountService.currentUserId
                )
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't remove request: ", exception)
            }
    }

    override suspend fun saveEmployeeRequestOnDB(employeeRequests: List<EmployeeRequest>) = withContext(ioDispatcher) {
        easyOrderDB.companyRequestsDao().deleteAndInsertEmployeeRequests(employeeRequests = employeeRequests)
    }
}