package com.telakuR.easyorder.home.repository

import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.telakuR.easyorder.R
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.home.models.EmployeeMenuItem
import com.telakuR.easyorder.home.models.FastFood
import com.telakuR.easyorder.home.models.MenuItem
import com.telakuR.easyorder.home.models.OrderDetails
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.services.AccountService
import com.telakuR.easyorder.utils.Constants.COMPANY_ID
import com.telakuR.easyorder.utils.Constants.EMPLOYEES
import com.telakuR.easyorder.utils.Constants.EMPLOYEE_ID
import com.telakuR.easyorder.utils.Constants.FAST_FOOD
import com.telakuR.easyorder.utils.Constants.MENU
import com.telakuR.easyorder.utils.Constants.NAME
import com.telakuR.easyorder.utils.Constants.ORDERED
import com.telakuR.easyorder.utils.Constants.ORDERS
import com.telakuR.easyorder.utils.Constants.REQUESTS
import com.telakuR.easyorder.utils.ToastUtils.showToast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HomeDataRepositoryImpl @Inject constructor(@IoDispatcher val ioDispatcher: CoroutineDispatcher, val fireStore: FirebaseFirestore, val accountService: AccountService): HomeRepository {
    private val TAG = HomeDataRepositoryImpl::class.simpleName

    override suspend fun getEmployeesList(): List<String> = suspendCoroutine { continuation ->
        fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
            .document(accountService.currentUserId)
            .get()
            .addOnSuccessListener { snapshot ->
                val employees = snapshot.data?.get(EMPLOYEES) as ArrayList<String>
                continuation.resume(employees)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't get employees: ", exception)
            }
    }

    override fun getEmployeesDetails(employees: List<String>): Flow<List<User>> = flow {
        try {
            val users = mutableListOf<User>()
            coroutineScope {
                launch {
                    employees.forEach { id ->
                        fireStore.collection(DBCollectionEnum.USERS.title).document(id)
                            .get()
                            .addOnSuccessListener { result ->
                                val user =
                                    Gson().fromJson(Gson().toJson(result.data), User::class.java)
                                user.id = id
                                users.add(user)
                            }
                    }
                }
            }

            delay(1000)
            emit(users)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get employees: ", e)
        }
    }.flowOn(ioDispatcher)

    override suspend fun removeEmployee(id: String) {
        fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
            .document(accountService.currentUserId)
            .update(EMPLOYEES, FieldValue.arrayRemove(id))
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to remove employee: ", exception)
            }
    }

    override suspend fun getRequestsList(): List<String> = suspendCoroutine { continuation ->
        fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
            .document(accountService.currentUserId)
            .get()
            .addOnSuccessListener { snapshot ->
                val requests = snapshot.data?.get(REQUESTS) as ArrayList<String>
                continuation.resume(requests)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't get request emails: ", exception)
            }
    }

    override fun getEmployeesRequestsDetails(requestsEmails: List<String>): Flow<List<User>> =
        flow {
            try {
                val users = mutableListOf<User>()
                coroutineScope {
                    launch {
                        requestsEmails.forEach { id ->
                            fireStore.collection(DBCollectionEnum.USERS.title).document(id)
                                .get()
                                .addOnSuccessListener { result ->
                                    val user = Gson().fromJson(
                                        Gson().toJson(result.data),
                                        User::class.java
                                    )
                                    user.id = id
                                    users.add(user)
                                }
                        }
                    }
                }

                delay(1000)
                emit(users)
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't get requests: ", e)
            }
        }.flowOn(ioDispatcher)

    override suspend fun acceptRequest(id: String) {
        fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
            .document(accountService.currentUserId)
            .update(
                REQUESTS, FieldValue.arrayRemove(id),
                EMPLOYEES, FieldValue.arrayUnion(id)
            )
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't accept request: ", exception)
            }
    }

    override suspend fun removeRequest(id: String) {
        fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
            .document(accountService.currentUserId)
            .update(REQUESTS, FieldValue.arrayRemove(id))
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't remove request: ", exception)
            }
    }

    override fun getOrders(userCompanyId: String): Flow<List<OrderDetails>> = flow {
        try {
            val companyOrdersList = mutableListOf<OrderDetails>()
            val ordersCollection = fireStore.collection(DBCollectionEnum.ORDERS.title)

            val querySnapshot = ordersCollection
                .whereEqualTo("companyId", userCompanyId)
                .get().await()

            val orderIds = querySnapshot.documents.map { it.id }

            val subCollectionTasks = orderIds.map { orderId ->
                ordersCollection.document(orderId).collection(ORDERS).get().await()
            }

            for (task in subCollectionTasks) {
                for (subDoc in task.documents) {
                    val orderDetail =
                        Gson().fromJson(Gson().toJson(subDoc.data), OrderDetails::class.java)

                    orderDetail.id = subDoc.id

                    if (orderDetail.employeeId != accountService.currentUserId) {
                        val employeeSnapshot = fireStore.collection(DBCollectionEnum.USERS.title)
                            .document(orderDetail.employeeId).get().await()

                        val employeeName = employeeSnapshot.getString(NAME) ?: ""
                        orderDetail.owner = employeeName
                        companyOrdersList.add(orderDetail)
                    }
                }
            }

            emit(companyOrdersList)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get orders: ", e)
        }
    }.flowOn(ioDispatcher)

    override fun getFastFoods(): Flow<List<FastFood>> = flow {
        try {
            val fastFoods = arrayListOf<FastFood>()

            fireStore.collection(DBCollectionEnum.FAST_FOODS.title).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val fastFood =
                            Gson().fromJson(Gson().toJson(document.data), FastFood::class.java)
                        fastFoods.add(fastFood)
                    }
                }

            delay(1000)
            emit(fastFoods)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get fast foods: ", e)
        }
    }.flowOn(ioDispatcher)

    override fun getFastFoodMenu(fastFoodName: String): Flow<List<MenuItem>> = flow {
        try {
            val menuItems = arrayListOf<MenuItem>()

            fireStore.collection(DBCollectionEnum.FAST_FOODS.title).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val fastFood =
                            Gson().fromJson(Gson().toJson(document.data), FastFood::class.java)

                        if (fastFood.name == fastFoodName) {
                            document.reference.collection(MENU).get()
                                .addOnSuccessListener { subDocs ->
                                    for (subDoc in subDocs) {
                                        val menuItem = Gson().fromJson(
                                            Gson().toJson(subDoc.data),
                                            MenuItem::class.java
                                        )
                                        menuItems.add(menuItem)
                                    }
                                }
                        }
                    }
                }

            delay(1000)
            emit(menuItems)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get fast food menu: ", e)
        }
    }.flowOn(ioDispatcher)

    override fun getMyOrder(): Flow<OrderDetails> {
        TODO("Not yet implemented")
    }

    override fun getMyOrders(companyId: String): Flow<List<OrderDetails>> = flow {
        try {
            val companyOrdersList = mutableListOf<OrderDetails>()
            fireStore.collection(DBCollectionEnum.ORDERS.title).whereEqualTo(COMPANY_ID, companyId).get().addOnSuccessListener { snapshot ->
                val docRef = snapshot.documents[0].reference

                docRef.collection(ORDERS).get().addOnSuccessListener { subSnapShot ->
                    val ordersDoc = subSnapShot.documents[0]

                    ordersDoc.reference.collection(ORDERED).whereEqualTo(EMPLOYEE_ID, accountService.currentUserId).get().addOnSuccessListener {
                        val orderDetail =
                            Gson().fromJson(Gson().toJson(ordersDoc.data), OrderDetails::class.java)

                        orderDetail.id = ordersDoc.id
                        fireStore.collection(DBCollectionEnum.USERS.title)
                            .document(orderDetail.employeeId).get().addOnSuccessListener {
                                val employeeName = it.getString(NAME) ?: ""
                                orderDetail.owner = employeeName
                                companyOrdersList.add(orderDetail)
                            }
                    }
                }
            }

            delay(1000)
            emit(companyOrdersList)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get orders: ", e)
        }
    }.flowOn(ioDispatcher)

    override suspend fun addMenuItemToOrder(
        companyId: String,
        menuItem: MenuItem,
        orderId: String
    ): Boolean = suspendCoroutine { continuation ->
        try {
            fireStore.collection(DBCollectionEnum.ORDERS.title)
                .whereEqualTo(COMPANY_ID, companyId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val docRef = snapshot.documents.firstOrNull()?.reference
                    if (docRef != null) {
                        val employeeMenuItem = EmployeeMenuItem(
                            employeeId = accountService.currentUserId,
                            menuItem = menuItem
                        )

                        docRef.collection(ORDERS).document(orderId).collection(ORDERED)
                            .add(employeeMenuItem).addOnSuccessListener {
                                continuation.resume(true)
                            }.addOnFailureListener {
                                continuation.resume(false)
                            }
                    }
                }

        } catch (e: Exception) {
            Log.e(TAG, "Couldn't add order: ", e)
        }
    }

    override suspend fun createOrderWithFastFood(
        companyId: String,
        fastFood: String,
        menuItem: MenuItem
    ): Boolean = suspendCoroutine { continuation ->
        try {
            fireStore.collection(DBCollectionEnum.ORDERS.title)
                .whereEqualTo(COMPANY_ID, companyId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val docRef = snapshot.documents[0].reference

                    val order = hashMapOf(
                        FAST_FOOD to fastFood,
                        EMPLOYEE_ID to accountService.currentUserId
                    )

                    val orderCollection = docRef.collection(ORDERS)
                    orderCollection.add(order)

                    orderCollection.get().addOnSuccessListener { subSnapShots ->
                        val subSnapShot = subSnapShots.documents[0].reference
                        val employeeMenuItem = EmployeeMenuItem(employeeId = accountService.currentUserId, menuItem = menuItem)
                        subSnapShot.collection(ORDERED).add(employeeMenuItem)

                        continuation.resume(true)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't add order: ", e)
        }
    }

    override suspend fun checkIfEmployeeHasAnOrder(companyId: String): Boolean = suspendCoroutine {
        try {
            fireStore.collection(DBCollectionEnum.ORDERS.title)
                .whereEqualTo(COMPANY_ID, companyId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result

                        if (document != null && !document.isEmpty)
                            handleEmployeeOrderResponse(document = document, continuation = it)

                    } else {
                        it.resume(false)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't check if employee has an order: ", e)
        }
    }

    private fun handleEmployeeOrderResponse(
        document: QuerySnapshot,
        continuation: Continuation<Boolean>
    ) {
        document.documents[0].reference.collection(ORDERS).get()
            .addOnCompleteListener { subTask ->
                if (subTask.isSuccessful && (!subTask.result.isEmpty && subTask.result != null)) {
                    var countOfOrders = 0

                    for (doc in subTask.result.documents) {
                        val order =
                            Gson().fromJson(Gson().toJson(doc.data), OrderDetails::class.java)

                        if (order.owner == accountService.currentUserId) {
                            countOfOrders++
                        }
                    }

                    continuation.resume(countOfOrders >= 1)
                } else {
                    continuation.resume(false)
                }
            }
    }

    override fun completeOrder(orderId: String, companyId: String) {
        fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(COMPANY_ID, companyId)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents[0].reference.collection(ORDERS).document(orderId).delete()
                    .addOnSuccessListener {
                        showToast(messageId = R.string.order_completed, length = Toast.LENGTH_SHORT)
                    }
                    .addOnFailureListener {
                        showToast(messageId = R.string.failed_order_completion, length = Toast.LENGTH_SHORT)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't get company orders: ", exception)
            }
    }

    override fun removeMenuItemFromOrder(orderId: String, companyId: String) {
        fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(COMPANY_ID, companyId)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents[0].reference.collection(ORDERS).document(orderId).get()
                    .addOnSuccessListener { subSnapShot ->
                        subSnapShot.reference.collection(ORDERED)
                            .whereEqualTo(EMPLOYEE_ID, accountService.currentUserId).get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    document.reference.delete()
                                        .addOnSuccessListener {
                                            showToast(
                                                messageId = R.string.order_removed,
                                                length = Toast.LENGTH_SHORT
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            showToast(
                                                messageId = R.string.failed_order_deletion,
                                                length = Toast.LENGTH_SHORT
                                            )
                                        }
                                }
                            }
                    }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't get company orders: ", exception)
            }
    }
}