package com.telakuR.easyorder.home.repository.impl

import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.telakuR.easyorder.R
import com.telakuR.easyorder.enums.DBCollectionEnum
import com.telakuR.easyorder.home.models.*
import com.telakuR.easyorder.home.repository.HomeRepository
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.services.AccountService
import com.telakuR.easyorder.services.MyFirebaseMessagingService
import com.telakuR.easyorder.utils.Constants
import com.telakuR.easyorder.utils.Constants.COMPANY_ID
import com.telakuR.easyorder.utils.Constants.DEFAULT_PRICE
import com.telakuR.easyorder.utils.Constants.EMPLOYEES
import com.telakuR.easyorder.utils.Constants.EMPLOYEE_ID
import com.telakuR.easyorder.utils.Constants.FAST_FOOD
import com.telakuR.easyorder.utils.Constants.MENU
import com.telakuR.easyorder.utils.Constants.NAME
import com.telakuR.easyorder.utils.Constants.ORDERED
import com.telakuR.easyorder.utils.Constants.ORDERS
import com.telakuR.easyorder.utils.Constants.PAID
import com.telakuR.easyorder.utils.Constants.PAYMENT
import com.telakuR.easyorder.utils.Constants.PROFILE_PIC
import com.telakuR.easyorder.utils.Constants.REQUESTS
import com.telakuR.easyorder.utils.Constants.TOTAL_PRICE
import com.telakuR.easyorder.utils.ToastUtils.showToast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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
    private val accountService: AccountService
) :
    HomeRepository {
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
            val documents = mutableListOf<DocumentSnapshot>()

            for (id in employees) {
                val document = fireStore.collection(DBCollectionEnum.USERS.title).document(id).get().await()
                documents.add(document)
            }

            documents.forEachIndexed { index, document ->
                val user = Gson().fromJson(Gson().toJson(document.data), User::class.java)
                user.id = employees[index]
                users.add(user)
            }

            emit(users)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get employees: ", e)
            emit(emptyList())
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

    override fun getEmployeesRequestsDetails(requestsEmails: List<String>): Flow<List<User>> = flow {
            try {
                val users = mutableListOf<User>()
                val documents = mutableListOf<DocumentSnapshot>()

                for (id in requestsEmails) {
                    val document =
                        fireStore.collection(DBCollectionEnum.USERS.title).document(id).get().await()
                    documents.add(document)
                }

                documents.forEachIndexed { index, document ->
                    val user = Gson().fromJson(Gson().toJson(document.data), User::class.java)
                    user.id = requestsEmails[index]
                    users.add(user)
                }

                emit(users)
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't get requests: ", e)
                emit(emptyList())
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
                .whereEqualTo(COMPANY_ID, userCompanyId)
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

    override fun getMyOrder(companyId: String, orderId: String): Flow<List<EmployeeMenuItem>> = flow {
        try {
            val employeeOrders = arrayListOf<EmployeeMenuItem>()

            val task = fireStore.collection(DBCollectionEnum.ORDERS.title)
                .whereEqualTo(COMPANY_ID, companyId)
                .get().await()

            if (task.documents.isNotEmpty()) {
                val docRef = task.documents[0].reference

                val subTask = docRef.collection(ORDERS).document(orderId)
                    .collection(ORDERED).get().await()

                val documents = subTask.documents

                if (documents.isNotEmpty()) {
                    val deferredEmployeeMenuItems = documents.map { document ->
                        val employeeMenuItemResponse =
                            Gson().fromJson(
                                Gson().toJson(document.data),
                                EmployeeMenuItemResponse::class.java
                            )

                        val employeeTask = fireStore.collection(DBCollectionEnum.USERS.title)
                            .document(employeeMenuItemResponse.employeeId).get()

                        Tasks.await(employeeTask)
                        val employee = employeeTask.result

                        val employeeName = employee.getString(NAME) ?: ""
                        val employeePicture = employee.getString(PROFILE_PIC) ?: ""
                        val userInfo = UserInfo(id = employee.id, name = employeeName, picture = employeePicture)
                        val employeeMenuItem = EmployeeMenuItem(
                            userInfo = userInfo,
                            menuItem = employeeMenuItemResponse.menuItem
                        )

                        employeeMenuItem
                    }

                    employeeOrders.addAll(deferredEmployeeMenuItems)
                }
            }

            emit(employeeOrders)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get my order: ", e)
            emit(emptyList())
        }
    }.flowOn(ioDispatcher)

    override fun getMyOrders(companyId: String): Flow<List<OrderDetails>> = flow {
        val companyOrdersList = mutableListOf<OrderDetails>()

        try {
            val snapshot = fireStore.collection(DBCollectionEnum.ORDERS.title).whereEqualTo(COMPANY_ID, companyId).get().await()
            val docRef = snapshot.documents[0].reference
            val subSnapShot = docRef.collection(ORDERS).get().await()
            val ordersDocs = subSnapShot.documents

            if (ordersDocs.isNotEmpty()) {
                for (ordersDoc in ordersDocs) {
                    val orderedSnapshot = ordersDoc.reference.collection(ORDERED).whereEqualTo(EMPLOYEE_ID, accountService.currentUserId).get().await()
                    if(orderedSnapshot.documents.isNotEmpty()) {
                        val orderDetail = Gson().fromJson(Gson().toJson(ordersDoc.data), OrderDetails::class.java)
                        orderDetail.id = ordersDoc.id

                        val employeeName = fireStore.collection(DBCollectionEnum.USERS.title).document(orderDetail.employeeId).get().await().getString(NAME) ?: ""
                        orderDetail.owner = employeeName

                        val fastFoodName = fireStore.collection(DBCollectionEnum.FAST_FOODS.title).document(orderDetail.fastFood).get().await().getString(NAME) ?: ""
                        orderDetail.fastFood = fastFoodName

                        companyOrdersList.add(orderDetail)
                    }
                }
            }

            emit(companyOrdersList)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get my orders: ", e)
            emit(emptyList())
        }
    }.flowOn(ioDispatcher)

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
                    docRef.collection(ORDERS).document(orderId).collection(ORDERED)
                        .add(employeeMenuItem).addOnSuccessListener {
                            continuation.resume(true)
                        }.addOnFailureListener {
                            continuation.resume(false)
                        }

                    savePaymentDetails(orderId = orderId, employeeMenuItem = employeeMenuItem)
                }
            }
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
                                    MyFirebaseMessagingService.sendNewOrderMessage(fastFood = fastFood)
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

    override fun completeOrder(orderId: String, companyId: String) {
        fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(COMPANY_ID, companyId)
            .get()
            .addOnSuccessListener { snapshot ->
                if(snapshot.documents.isNotEmpty()) {
                    val orderRef = snapshot.documents[0].reference.collection(ORDERS).document(orderId)
                    orderRef.collection(ORDERED).get().addOnSuccessListener {
                        if(it.documents.isNotEmpty()) {
                            for (doc in it.documents) {
                                doc.reference.delete()
                            }
                        }
                    }

                    orderRef.delete()
                        .addOnSuccessListener {
                            removeAllOrdersPaymentDetails(orderId = orderId)
                            showToast(
                                messageId = R.string.order_completed,
                                length = Toast.LENGTH_SHORT
                            )
                        }
                        .addOnFailureListener {
                            showToast(
                                messageId = R.string.failed_order_completion,
                                length = Toast.LENGTH_SHORT
                            )
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't complete order: ", exception)
            }
    }

    override fun removeMenuItemFromOrder(orderId: String, companyId: String, menuItem: MenuItem) {
        fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(COMPANY_ID, companyId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.documents.isNotEmpty()) {
                    val ordersRef = snapshot.documents[0].reference.collection(ORDERS).document(orderId)

                    ordersRef.get().addOnSuccessListener { subSnapShot ->
                        val ownerId = subSnapShot.get(EMPLOYEE_ID) as String

                        subSnapShot.reference.collection(ORDERED)
                            .whereEqualTo(EMPLOYEE_ID, accountService.currentUserId)
                            .whereEqualTo(Constants.MENU_ITEM_NAME, menuItem.name)
                            .get()
                            .addOnSuccessListener { snapshots ->
                                val document = snapshots.documents[0]
                                document.reference.delete()
                                    .addOnSuccessListener {
                                        if (snapshots.documents.size <= 1 && ownerId == accountService.currentUserId) {
                                            ordersRef.delete()
                                        }
                                        showToast(messageId = R.string.order_removed, length = Toast.LENGTH_SHORT)
                                        removeMenuItemPaymentDetails(orderId = orderId, menuItem = menuItem)
                                    }
                                    .addOnFailureListener { e ->
                                        Log.d(TAG, "Couldn't remove menu item: $e")
                                        showToast(messageId = R.string.failed_order_deletion, length = Toast.LENGTH_SHORT)
                                    }
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't get company orders: ", exception)
            }
    }

    override fun getOtherOrder(companyId: String, orderId: String): Flow<List<EmployeeMenuItem>> = flow {
        try {
            val employeeOrders = arrayListOf<EmployeeMenuItem>()

            val task = fireStore.collection(DBCollectionEnum.ORDERS.title)
                .whereEqualTo(COMPANY_ID, companyId)
                .get().await()

            if (task.documents.isNotEmpty()) {
                val docRef = task.documents[0].reference

                val subTask = docRef.collection(ORDERS).document(orderId)
                    .collection(ORDERED).whereEqualTo(EMPLOYEE_ID, accountService.currentUserId)
                    .get().await()

                val documents = subTask.documents

                if (documents.isNotEmpty()) {
                    val deferredEmployeeMenuItems = documents.map { document ->
                        val employeeMenuItemResponse =
                            Gson().fromJson(
                                Gson().toJson(document.data),
                                EmployeeMenuItemResponse::class.java
                            )

                        val employeeTask = fireStore.collection(DBCollectionEnum.USERS.title)
                            .document(employeeMenuItemResponse.employeeId).get().await()

                        val employeeName = employeeTask.getString(NAME) ?: ""
                        val employeePicture = employeeTask.getString(PROFILE_PIC) ?: ""
                        val userInfo = UserInfo(name = employeeName, picture = employeePicture)
                        val employeeMenuItem = EmployeeMenuItem(
                            userInfo = userInfo,
                            menuItem = employeeMenuItemResponse.menuItem
                        )

                        employeeMenuItem
                    }

                    employeeOrders.addAll(deferredEmployeeMenuItems)
                }
            }

            emit(employeeOrders)
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't get other order details: ", e)
            emit(emptyList())
        }
    }

    override fun removeOrder(orderId: String, companyId: String) {
        fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(COMPANY_ID, companyId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.documents.isNotEmpty()) {
                    val orderRef =
                        snapshot.documents[0].reference.collection(ORDERS).document(orderId)
                    orderRef.collection(ORDERED).whereEqualTo(EMPLOYEE_ID, accountService.currentUserId).get().addOnSuccessListener {
                        if (it.documents.isNotEmpty()) {
                            for (doc in it.documents) {
                                doc.reference.delete().addOnSuccessListener {
                                    removeOrderPaymentDetails(orderId = orderId)
                                    showToast(
                                        messageId = R.string.order_removed,
                                        length = Toast.LENGTH_SHORT
                                    )
                                }.addOnFailureListener {
                                        showToast(
                                            messageId = R.string.failed_order_deletion,
                                            length = Toast.LENGTH_SHORT
                                        )
                                    }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't remove order", exception)
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

    private fun removeOrderPaymentDetails(orderId: String) {
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
                        payments.removeAt(index)
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
                }
            } else {
                Log.d(TAG, "Couldn't remove payment details: ${task.exception}")
            }
        }
    }

    private fun removeAllOrdersPaymentDetails(orderId: String) {
        val paymentRef = fireStore.collection(DBCollectionEnum.PAYMENTS.title).document(orderId)
        paymentRef.delete()
            .addOnSuccessListener {
                Log.d(TAG, "Document $orderId deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Failed to delete document $orderId: $exception")
            }
    }


    override fun removeMenuItemPaymentDetails(orderId: String, menuItem: MenuItem) {
        val paymentRef = fireStore.collection(DBCollectionEnum.PAYMENTS.title)
            .document(orderId)

        paymentRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val jsonPaymentsList = document.get(PAYMENT) as List<Map<String, Any>>

                val payments = jsonPaymentsList.map { map ->
                    val employeeId = map[EMPLOYEE_ID] as String
                    val totalPrice = map[TOTAL_PRICE] as Double
                    val paid = map[PAID] as Double
                    UserPaymentModel(
                        employeeId = employeeId,
                        totalPayment = totalPrice,
                        paid = paid
                    )
                }.toMutableList()

                val index = payments.indexOfFirst { it.employeeId == accountService.currentUserId }

                if (index != -1) {
                    val totalPrice = payments[index].totalPayment - menuItem.price
                    val paid = payments[index].paid
                    val paymentModel = UserPaymentModel(
                        employeeId = accountService.currentUserId,
                        totalPayment = totalPrice,
                        paid = paid
                    )

                    if(totalPrice.toString() == DEFAULT_PRICE) {
                        payments.removeAt(index)
                    } else {
                        payments[index] = paymentModel
                    }
                }

                if(payments.isEmpty()) {
                    paymentRef.delete().addOnSuccessListener {
                        Log.d(TAG, "Order payment deleted successfully")
                    }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Failed to delete order payments: $exception")
                        }
                } else {
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
                }
            }
        }.addOnFailureListener { e ->
            Log.d(TAG, "Couldn't remove payment details: $e")
        }
    }

    override fun getPaymentDetails(
        companyId: String,
        orderId: String
    ): Flow<List<UserPaymentModelResponse>> = flow {
        val document = fireStore.collection(DBCollectionEnum.PAYMENTS.title)
            .document(orderId).get().await()

        if(document.exists()) {
            val jsonPaymentsList = document.get(PAYMENT) as List<Map<String, Any>>

            val payments = jsonPaymentsList.map { map ->
                val employeeId = map[EMPLOYEE_ID] as String
                val document = fireStore.collection(DBCollectionEnum.USERS.title).document(employeeId).get().await()
                val user = Gson().fromJson(Gson().toJson(document.data), User::class.java)
                val totalPrice = map[TOTAL_PRICE] as Double
                val paid = map[PAID] as Double
                UserPaymentModelResponse(userInfo = UserInfo(id = employeeId, name = user.name, picture = user.profilePic), totalPayment = totalPrice, paid = paid)
            }.toMutableList()

            emit(payments)
        } else {
            emit(emptyList())
        }
    }

    override fun setPaidValuesToPayments(employeeId: String, paid: String, orderId: String) {
        val paymentRef = fireStore.collection(DBCollectionEnum.PAYMENTS.title).document(orderId)

        paymentRef.get().addOnSuccessListener { document ->
            val jsonPaymentsList = document.get(PAYMENT) as List<Map<String, Any>>

            val payments = jsonPaymentsList.map { map ->
                val employeeId = map[EMPLOYEE_ID] as String
                val totalPrice = map[TOTAL_PRICE] as Double
                val paid = map[PAID] as Double
                UserPaymentModel(employeeId = employeeId, totalPayment = totalPrice, paid = paid)
            }.toMutableList()

            val index = payments.indexOfFirst { it.employeeId == employeeId }

            if (index != -1) {
                payments[index].paid = paid.toDouble()

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
            }
        }.addOnFailureListener { e ->
            Log.d(TAG, "Couldn't set ValuesToPayments: ")
        }
    }

    override suspend fun getOrder(orderId: String, companyId: String): OrderDetails = suspendCoroutine { continuation ->
        fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(COMPANY_ID, companyId)
            .get()
            .addOnSuccessListener { task ->
                val orderRef =
                    task.documents[0].reference.collection(ORDERS).document(orderId).get()
                orderRef.addOnSuccessListener { document ->
                    val orderDetail =
                        Gson().fromJson(Gson().toJson(document.data), OrderDetails::class.java)
                    orderDetail.id = orderId
                    continuation.resume(orderDetail)
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Couldn't get order with orderId $orderId: ", e)
                    continuation.resume(OrderDetails())
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Couldn't find company $companyId: ", e)
                continuation.resume(OrderDetails())
            }
    }
}