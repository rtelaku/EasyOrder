package com.telakuR.easyorder.home.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.home.models.*
import com.telakuR.easyorder.models.User
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.log

class HomeDataRepositoryImpl @Inject constructor(val firestore: FirebaseFirestore, val accountService: AccountService): HomeRepository {
    private val TAG = HomeDataRepositoryImpl::class.simpleName

    override suspend fun getEmployeesList(): List<String> = suspendCoroutine { continuation ->
        try {
            firestore.collection(DBCollectionEnum.EMPLOYEES.title).whereEqualTo(COMPANY_ID, accountService.currentUser?.uid).limit(1).get().addOnSuccessListener {
                it.documents.forEach { company ->
                val employees = company.data?.get(EMPLOYEES) as ArrayList<String>
                continuation.resume(employees)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get employees: ", e)
        }
    }

    override fun getEmployees(employees: List<String>): Flow<List<User>> = flow {
        try {
            val users = mutableListOf<User>()
            coroutineScope {
                launch {
                    employees.forEach { id ->
                        firestore.collection(DBCollectionEnum.USERS.title).document(id)
                            .get()
                            .addOnSuccessListener { result ->
                                val user = Gson().fromJson(Gson().toJson(result.data), User::class.java)
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
    }

    override suspend fun removeEmployee(id: String) {
        try {
            firestore.collection(DBCollectionEnum.EMPLOYEES.title)
                .whereEqualTo(COMPANY_ID, accountService.currentUser?.uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val docRef = snapshot.documents[0].reference
                        docRef.update(EMPLOYEES, FieldValue.arrayRemove(id))
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Remove employee: ", e)
        }
    }

    override suspend fun getRequestsList(): List<String> = suspendCoroutine { continuation ->
        try {
            firestore.collection(DBCollectionEnum.EMPLOYEES.title).whereEqualTo(COMPANY_ID, accountService.currentUser?.uid).limit(1).get().addOnSuccessListener {
                it.documents.forEach { company ->
                    val requests = company.data?.get(REQUESTS) as ArrayList<String>
                    continuation.resume(requests)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get request emails: ", e)
        }
    }

    override fun getRequests(requestsEmails: List<String>): Flow<List<User>> = flow {
        try {
            val users = mutableListOf<User>()
            coroutineScope {
                launch {
                    requestsEmails.forEach { id ->
                        firestore.collection(DBCollectionEnum.USERS.title).document(id)
                            .get()
                            .addOnSuccessListener { result ->
                                val user = Gson().fromJson(Gson().toJson(result.data), User::class.java)
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
    }

    override suspend fun acceptRequest(id: String) {
        try {
            firestore.collection(DBCollectionEnum.EMPLOYEES.title)
                .whereEqualTo(COMPANY_ID, accountService.currentUser?.uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val docRef = snapshot.documents[0].reference
                        docRef.update(REQUESTS, FieldValue.arrayRemove(id))
                        docRef.update(EMPLOYEES, FieldValue.arrayUnion(id))
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Add employee: ", e)
        }
    }

    override suspend fun removeRequest(id: String) {
        try {
            firestore.collection(DBCollectionEnum.EMPLOYEES.title)
                .whereEqualTo(COMPANY_ID, accountService.currentUser?.uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val docRef = snapshot.documents[0].reference
                        docRef.update(REQUESTS, FieldValue.arrayRemove(id))
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Remove request: ", e)
        }
    }

    override fun getMenuItems(companyName: String): Flow<List<Order>> = flow {
        try {
            val companyOrdersList = arrayListOf<Order>()

            coroutineScope {
                launch {
                    firestore.collection(DBCollectionEnum.ORDERS.title).get().addOnSuccessListener { documents ->
                        for (document in documents) {
                            val order = Gson().fromJson(Gson().toJson(document.data), Order::class.java)

                            document.reference.collection(ORDERS).get()
                                .addOnSuccessListener { subDocs ->
                                    val listOfOrders = arrayListOf<OrderDetails>()
                                    for (subDoc in subDocs) {
                                        val orders = Gson().fromJson(
                                            Gson().toJson(subDoc.data),
                                            OrderDetails::class.java
                                        )
                                        subDoc.reference.collection(ORDERED).get()
                                            .addOnSuccessListener { menu ->
                                                val menuItem = Gson().fromJson(
                                                    Gson().toJson(menu.first().data),
                                                    Menu::class.java
                                                )
                                                orders.ordered = menuItem
                                                listOfOrders.add(orders)
                                            }
                                    }
                                    order.orders = listOfOrders
                                    companyOrdersList.add(order)
                                }
                        }
                    }
                }
            }

            delay(1000)
            emit(companyOrdersList)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get menu items: ", e)
        }
    }

    override fun getOrders(userCompanyId: String): Flow<List<OrderDetails>> = flow {
        try {
            val companyOrdersList = arrayListOf<OrderDetails>()

            coroutineScope {
                launch {
                    firestore.collection(DBCollectionEnum.ORDERS.title).get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                val order = Gson().fromJson(Gson().toJson(document.data), Order::class.java)

                                if(order.companyId == userCompanyId) {
                                    document.reference.collection(ORDERS).get()
                                        .addOnSuccessListener { subDocs ->
                                            for (subDoc in subDocs) {
                                                val orderDetail = Gson().fromJson(
                                                    Gson().toJson(subDoc.data),
                                                    OrderDetails::class.java
                                                )

                                                if(orderDetail.employeeId != accountService.currentUserId) {
                                                    firestore.collection(DBCollectionEnum.USERS.title)
                                                        .document(orderDetail.employeeId).get()
                                                        .addOnSuccessListener { snapshot ->
                                                            val employeeName =
                                                                snapshot.get(NAME) as String
                                                            orderDetail.owner = employeeName
                                                            companyOrdersList.add(orderDetail)
                                                        }
                                                }
                                            }
                                        }
                                }
                            }
                        }
                }
            }

            delay(1000)
            emit(companyOrdersList)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get orders: ", e)
        }
    }

    override fun getFastFoods(): Flow<List<FastFood>> = flow {
        try {
            val fastFoods = arrayListOf<FastFood>()

            firestore.collection(DBCollectionEnum.FAST_FOODS.title).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val fastFood = Gson().fromJson(Gson().toJson(document.data), FastFood::class.java)
                        fastFoods.add(fastFood)
                    }
                }

            delay(1000)
            emit(fastFoods)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get fast foods: ", e)
        }
    }

    override fun getFastFoodMenu(fastFoodName: String): Flow<List<MenuItem>> = flow {
        try {
            val menuItems = arrayListOf<MenuItem>()

            firestore.collection(DBCollectionEnum.FAST_FOODS.title).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val fastFood = Gson().fromJson(Gson().toJson(document.data), FastFood::class.java)

                        if(fastFood.name == fastFoodName) {
                            document.reference.collection(MENU).get().addOnSuccessListener { subDocs ->
                                for(subDoc in subDocs) {
                                    val menuItem = Gson().fromJson(Gson().toJson(subDoc.data), MenuItem::class.java)
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
    }

    override suspend fun createOrderWithFastFood(
        companyId: String,
        fastFood: String,
        menuItem: MenuItem
    ): Boolean = suspendCoroutine { continuation ->
        try {
            firestore.collection(DBCollectionEnum.ORDERS.title)
                .whereEqualTo(COMPANY_ID, companyId)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val docRef = snapshot.documents[0].reference

                        val order = hashMapOf(
                            FAST_FOOD to fastFood,
                            EMPLOYEE_ID to accountService.currentUserId
                        )

                        val orderCollection = docRef.collection(ORDERS)
                        orderCollection.add(order)

                        orderCollection.get().addOnSuccessListener { subSnapShots ->
                            val subSnapShot = subSnapShots.documents[0].reference
                            subSnapShot.collection(ORDERED).add(menuItem)

                            continuation.resume(true)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't add order: ", e)
        }
    }

    override suspend fun checkIfEmployeeHasAnOrder(companyId: String): Boolean = suspendCoroutine {
        try {
            firestore.collection(DBCollectionEnum.ORDERS.title)
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
}