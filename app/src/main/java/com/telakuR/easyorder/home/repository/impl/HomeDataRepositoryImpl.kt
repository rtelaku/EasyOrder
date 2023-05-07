package com.telakuR.easyorder.home.repository.impl

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.telakuR.easyorder.home.models.*
import com.telakuR.easyorder.home.repository.HomeRepository
import com.telakuR.easyorder.main.enums.DBCollectionEnum
import com.telakuR.easyorder.main.services.AccountService
import com.telakuR.easyorder.main.services.MyFirebaseMessagingService
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.room_db.db.EasyOrderDB
import com.telakuR.easyorder.room_db.enitites.CompanyOrderDetails
import com.telakuR.easyorder.room_db.enitites.Employee
import com.telakuR.easyorder.room_db.enitites.MyOrder
import com.telakuR.easyorder.utils.Constants.COMPANY_ID
import com.telakuR.easyorder.utils.Constants.EMPLOYEES
import com.telakuR.easyorder.utils.Constants.EMPLOYEE_ID
import com.telakuR.easyorder.utils.Constants.FAST_FOOD
import com.telakuR.easyorder.utils.Constants.MENU
import com.telakuR.easyorder.utils.Constants.NAME
import com.telakuR.easyorder.utils.Constants.ORDERED
import com.telakuR.easyorder.utils.Constants.ORDERS
import com.telakuR.easyorder.utils.Constants.PAID
import com.telakuR.easyorder.utils.Constants.PAYMENT
import com.telakuR.easyorder.utils.Constants.TOTAL_PRICE
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HomeDataRepositoryImpl @Inject constructor(
    @IoDispatcher val ioDispatcher: CoroutineDispatcher,
    private val fireStore: FirebaseFirestore,
    private val accountService: AccountService,
    private val easyOrderDB: EasyOrderDB
) : HomeRepository {
    private val TAG = HomeDataRepositoryImpl::class.simpleName

    override fun getEmployeesFromAPI(): Flow<List<Employee>> = callbackFlow {
        val employeeRef = fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
            .document(accountService.currentUserId)

        val subscription = employeeRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.d(TAG, "Couldn't get employees: $exception")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val employeesList = snapshot.get(EMPLOYEES) as ArrayList<String>

                val employees = mutableListOf<Employee>()
                val documents = mutableListOf<DocumentSnapshot>()

                val deferredDocuments = employeesList.map { id ->
                    async {
                        fireStore.collection(DBCollectionEnum.USERS.title).document(id).get().await()
                    }
                }

                runBlocking {
                    documents.addAll(deferredDocuments.awaitAll())
                }

                documents.forEachIndexed { index, document ->
                    val user = Gson().fromJson(Gson().toJson(document.data), Employee::class.java)
                    user.id = employeesList[index]
                    employees.add(user)
                }

                this.trySend(employees).isSuccess
            } else {
                this.trySend(emptyList<Employee>()).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }.flowOn(ioDispatcher)

    override suspend fun removeEmployee(id: String) {
        fireStore.collection(DBCollectionEnum.EMPLOYEES.title)
            .document(accountService.currentUserId)
            .update(EMPLOYEES, FieldValue.arrayRemove(id))
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to remove employee: ", exception)
            }
    }

    override fun getOrdersFromAPI(userCompanyId: String): Flow<List<CompanyOrderDetails>> = flow {
        try {
            val companyOrdersList = mutableListOf<CompanyOrderDetails>()
            val ordersCollection = fireStore.collection(DBCollectionEnum.ORDERS.title)

            val querySnapshot = ordersCollection
                .whereEqualTo(COMPANY_ID, userCompanyId)
                .get().await()

            val orderIds = querySnapshot.documents.map { it.id }

            val subCollectionTasks = orderIds.map { orderId ->
                ordersCollection.document(orderId).collection(ORDERS).get().await()
            }

            for (task in subCollectionTasks) {
                for (subDoc in task.documents) {
                    val orderDetail =
                        Gson().fromJson(Gson().toJson(subDoc.data), CompanyOrderDetails::class.java)

                    orderDetail.id = subDoc.id

                    if (orderDetail.employeeId != accountService.currentUserId) {
                        val employeeSnapshot = fireStore.collection(DBCollectionEnum.USERS.title)
                            .document(orderDetail.employeeId).get().await()

                        val employeeName = employeeSnapshot.getString(NAME) ?: ""
                        orderDetail.owner = employeeName

                        val fastFoodName = fireStore.collection(DBCollectionEnum.FAST_FOODS.title)
                            .document(orderDetail.fastFood).get().await().getString(NAME) ?: ""
                        orderDetail.fastFood = fastFoodName

                        companyOrdersList.add(orderDetail)
                    }
                }
            }

            emit(companyOrdersList)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get orders: ", e)
            emit(emptyList())
        }
    }.flowOn(ioDispatcher)

    override fun getFastFoods(): Flow<List<FastFood>> = flow {
        try {
            val fastFoods = arrayListOf<FastFood>()
            val fastFoodsData = fireStore.collection(DBCollectionEnum.FAST_FOODS.title).get().await()

            fastFoodsData.forEach { document ->
                val fastFood =
                    Gson().fromJson(Gson().toJson(document.data), FastFood::class.java)
                fastFood.id = document.id
                fastFoods.add(fastFood)
            }

            emit(fastFoods)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get fast foods: ", e)
            emit(emptyList())
        }
    }.flowOn(ioDispatcher)

    override fun getFastFoodMenu(fastFoodId: String): Flow<List<MenuItem>> = flow {
        try {
            val menuItems = arrayListOf<MenuItem>()

            val fastFoodDoc = fireStore.collection(DBCollectionEnum.FAST_FOODS.title).document(fastFoodId).get().await()
            val menuDocs = fastFoodDoc.reference.collection(MENU).get().await().documents

            for (menuDoc in menuDocs) {
                val menuItem = Gson().fromJson(Gson().toJson(menuDoc.data), MenuItem::class.java)
                menuItems.add(menuItem)
            }

            emit(menuItems)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get fast food menu: ", e)
            emit(emptyList())
        }
    }.flowOn(ioDispatcher)

    override fun getEmployeesListFromDB(): Flow<List<Employee>> {
        return easyOrderDB.companyEmployeesDao().getCompanyEmployees()
    }

    override suspend fun addMenuItemToOrder(
        companyId: String,
        menuItem: MenuItem,
        orderId: String
    ): Boolean = suspendCoroutine { continuation ->
        fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(COMPANY_ID, companyId)
            .get()
            .addOnSuccessListener { snapshot ->
                val docRef = snapshot.documents.firstOrNull()?.reference
                if (docRef != null) {
                    val employeeMenuItem = EmployeeMenuItemResponse(
                        employeeId = accountService.currentUserId,
                        menuItem = menuItem
                    )
                    val orderDoc = docRef.collection(ORDERS).document(orderId)

                    orderDoc.collection(ORDERED).add(employeeMenuItem)
                        .addOnSuccessListener { documentRef ->
                            orderDoc.get().addOnSuccessListener { documentSnapshot ->
                                val ownerId = documentSnapshot.get(EMPLOYEE_ID) as String
                                MyFirebaseMessagingService.sendNewMenuItemMessage(ownerId = ownerId)
                            }
                            continuation.resume(true)
                        }.addOnFailureListener {
                        continuation.resume(false)
                    }

                    savePaymentDetails(orderId = orderId, employeeMenuItem = employeeMenuItem)
                }
            }
    }

    override suspend fun saveEmployeesOnDB(employees: List<Employee>) = withContext(ioDispatcher) {
        easyOrderDB.companyEmployeesDao().deleteAndInsertEmployees(companyEmployees = employees)
    }

    override fun getOrdersFromDB(): Flow<List<CompanyOrderDetails>> {
        return easyOrderDB.companyOrdersDao().getCompanyOrders()
    }

    override suspend fun saveOrdersOnDB(companyOrders: List<CompanyOrderDetails>) = withContext(ioDispatcher) {
        easyOrderDB.companyOrdersDao().deleteAndInsertOrders(companyOrders = companyOrders)
    }

    override suspend fun saveOrderOnDB(order: MyOrder) {
        easyOrderDB.myOrdersDao().insertOrder(order = order)
    }

    private fun savePaymentDetails(orderId: String, employeeMenuItem: EmployeeMenuItemResponse) {
        val paymentRef = fireStore.collection(DBCollectionEnum.PAYMENTS.title)
            .document(orderId)

        paymentRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    val jsonPaymentsList = document.get(PAYMENT) as List<Map<String, Any>>

                    val payments = jsonPaymentsList.map { map ->
                        val employeeId = map[EMPLOYEE_ID] as String
                        val totalPrice = map[TOTAL_PRICE] as Double
                        val paid = map[PAID] as Double
                        UserPaymentModel(employeeId = employeeId, totalPayment = totalPrice, paid = paid)
                    }.toMutableList()

                    val index = payments.indexOfFirst { it.employeeId == accountService.currentUserId }

                    if (index != -1) {
                        val totalPrice = employeeMenuItem.menuItem.price + payments[index].totalPayment
                        val paid = payments[index].paid
                        val paymentModel = UserPaymentModel(employeeId = accountService.currentUserId, totalPayment = totalPrice, paid = paid)
                        payments[index] = paymentModel
                    } else {
                        val paymentModel = UserPaymentModel(employeeId = accountService.currentUserId, totalPayment = employeeMenuItem.menuItem.price, paid = 0.00)
                        payments.add(paymentModel)
                    }

                    val updatedPaymentsList = payments.map { paymentModel ->
                        mapOf(
                            EMPLOYEE_ID to paymentModel.employeeId,
                            TOTAL_PRICE to paymentModel.totalPayment,
                            PAID to paymentModel.paid
                        )
                    }

                    paymentRef.update(PAYMENT, updatedPaymentsList)
                        .addOnSuccessListener {
                            Log.d(TAG, "Payment updated successfully")
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Failed to update payment: $exception")
                        }
                } else {
                    val paymentModel = UserPaymentModel(employeeId = accountService.currentUserId, totalPayment = employeeMenuItem.menuItem.price, paid = 0.00)
                    paymentRef.set(mapOf(PAYMENT to listOf(paymentModel)))
                }
            } else {
                Log.d(TAG, "Couldn't save payment details: ${task.exception}")
            }
        }
    }

    override suspend fun createOrderWithFastFood(
        companyId: String,
        fastFood: String,
        menuItem: MenuItem
    ): String = suspendCoroutine { continuation ->
            fireStore.collection(DBCollectionEnum.ORDERS.title)
                .whereEqualTo(COMPANY_ID, companyId)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.documents.isNotEmpty()) {
                        val docRef = snapshot.documents[0].reference

                        val order = hashMapOf(
                            FAST_FOOD to fastFood,
                            EMPLOYEE_ID to accountService.currentUserId
                        )

                        val orderCollection = docRef.collection(ORDERS)
                        orderCollection.add(order).addOnSuccessListener { orderReference ->
                            orderCollection.document(orderReference.id).get().addOnSuccessListener { document ->
                                val employeeMenuItem = EmployeeMenuItemResponse(
                                    employeeId = accountService.currentUserId,
                                    menuItem = menuItem
                                )
                                document.reference.collection(ORDERED).add(employeeMenuItem).addOnSuccessListener {
                                    continuation.resume(orderReference.id)
                                    savePaymentDetails(orderId = orderReference.id, employeeMenuItem = employeeMenuItem)

                                    fireStore.collection(DBCollectionEnum.FAST_FOODS.title).document(fastFood).get().addOnSuccessListener {
                                        val fastFoodName = it.get(NAME) as String
                                        MyFirebaseMessagingService.sendNewOrderMessage(fastFood = fastFoodName)
                                    }
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Couldn't add order: ", e)
                    continuation.resume("")
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
                        val employeeId = doc.get(EMPLOYEE_ID) as String

                        if (employeeId == accountService.currentUserId) {
                            countOfOrders++
                        }
                    }

                    continuation.resume(countOfOrders >= 1)
                } else {
                    continuation.resume(false)
                }
            }
    }

    override suspend fun getFastFoodId(orderId: String, companyId: String): String = suspendCoroutine { continuation ->
            try {
                if(orderId.isNotEmpty()) {
                    fireStore.collection(DBCollectionEnum.ORDERS.title).whereEqualTo(COMPANY_ID, companyId)
                        .get().addOnSuccessListener { snapshot ->
                            val documents = snapshot.documents
                            if(documents.isNotEmpty()) {
                                for (document in documents) {
                                    document.reference.collection(ORDERS).document(orderId).get().addOnSuccessListener { subSnapshot ->
                                        continuation.resume(subSnapshot.get(FAST_FOOD) as String)
                                    }
                                }
                            }
                        }
                } else {
                    continuation.resume("")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't get fast food: ", e)
            }
    }

    override suspend fun getOrder(orderId: String, companyId: String): MyOrder? = suspendCoroutine { continuation ->
        fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(COMPANY_ID, companyId)
            .get()
            .addOnSuccessListener { task ->
                val orderRef =
                    task.documents[0].reference.collection(ORDERS).document(orderId).get()
                orderRef.addOnSuccessListener { document ->
                    val orderDetail =
                        Gson().fromJson(Gson().toJson(document.data), MyOrder::class.java)
                    orderDetail.id = orderId
                    continuation.resume(orderDetail)
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Couldn't get order with orderId $orderId: ", e)
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Couldn't find order of $companyId: ", e)
                continuation.resume(null)
            }
    }
}