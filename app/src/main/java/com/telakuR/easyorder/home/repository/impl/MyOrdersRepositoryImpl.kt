package com.telakuR.easyorder.home.repository.impl

import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.models.EmployeeMenuItemResponse
import com.telakuR.easyorder.home.models.UserInfo
import com.telakuR.easyorder.home.models.UserPaymentModel
import com.telakuR.easyorder.home.models.UserPaymentModelResponse
import com.telakuR.easyorder.home.repository.MyOrdersRepository
import com.telakuR.easyorder.main.enums.DBCollectionEnum
import com.telakuR.easyorder.main.models.User
import com.telakuR.easyorder.main.services.AccountService
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.room_db.db.EasyOrderDB
import com.telakuR.easyorder.room_db.enitites.EmployeeMenuItem
import com.telakuR.easyorder.room_db.enitites.MyOrder
import com.telakuR.easyorder.room_db.enitites.MyOrderWithDetails
import com.telakuR.easyorder.utils.Constants
import com.telakuR.easyorder.utils.ToastUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MyOrdersRepositoryImpl @Inject constructor(
    @IoDispatcher val ioDispatcher: CoroutineDispatcher,
    private val fireStore: FirebaseFirestore,
    private val accountService: AccountService,
    private val easyOrderDB: EasyOrderDB
) : MyOrdersRepository {

    private val TAG = MyOrdersRepositoryImpl::class.simpleName

    override fun getMyOrderDetails(companyId: String, orderId: String): Flow<List<EmployeeMenuItem>> =
        callbackFlow {
            val orderDoc = fireStore.collection(DBCollectionEnum.ORDERS.title)
                .whereEqualTo(Constants.COMPANY_ID, companyId).get().await()

            val orderColl = orderDoc.documents[0].reference.collection(Constants.ORDERS)

            val orderOwnerId = orderColl.document(orderId).get().await().get(Constants.EMPLOYEE_ID) as String
            Log.d("rigiii", "orderOwnerId: $orderOwnerId acc service ${accountService.currentUserId}")
            val isMyOrder = orderOwnerId == accountService.currentUserId

            val docRef = orderColl.document(orderId).collection(Constants.ORDERED)

            Log.d("rigiii", "getMyOrderDetails: $isMyOrder")

            val subTask = if(isMyOrder) docRef else docRef.whereEqualTo(Constants.EMPLOYEE_ID, accountService.currentUserId)

            val listenerRegistration = subTask.addSnapshotListener { subTaskSnapshot, subTaskError ->
                if (subTaskError != null) {
                    Log.e(TAG, "Error fetching ordered items: ", subTaskError)
                    close(subTaskError)
                    return@addSnapshotListener
                }

                if (subTaskSnapshot != null && !subTaskSnapshot.isEmpty) {
                    val employeeOrders = arrayListOf<EmployeeMenuItem>()

                    subTaskSnapshot.documents.map { document ->
                        val employeeMenuItemResponse =
                            Gson().fromJson(
                                Gson().toJson(document.data),
                                EmployeeMenuItemResponse::class.java
                            )

                        val employeeTask = fireStore.collection(DBCollectionEnum.USERS.title)
                            .document(employeeMenuItemResponse.employeeId).get()

                        employeeTask.addOnSuccessListener { employee ->
                            val employeeName = employee.getString(Constants.NAME) ?: ""
                            val employeePicture = employee.getString(Constants.PROFILE_PIC) ?: ""
                            val userInfo = UserInfo(
                                employeeId = employee.id,
                                employeeName = employeeName,
                                employeePicture = employeePicture
                            )
                            val employeeMenuItem = EmployeeMenuItem(
                                userInfo = userInfo,
                                menuItem = employeeMenuItemResponse.menuItem
                            )
                            employeeOrders.add(employeeMenuItem)
                            trySend(employeeOrders.toList())
                        }.addOnFailureListener { exception ->
                            Log.e(TAG, "Error fetching employee info: ", exception)
                            close(exception)
                        }
                    }
                } else {
                    trySend(emptyList<EmployeeMenuItem>())
                }
            }

            awaitClose {
                listenerRegistration.remove()
            }
        }.flowOn(ioDispatcher)

    override fun getMyOrderDetailsFromDB(orderId: String): Flow<MyOrderWithDetails> {
        return easyOrderDB.myOrdersDao().getOrderDetailsById(id = orderId)
    }

    @OptIn(FlowPreview::class)
    override fun getMyOrdersFromAPI(companyId: String): Flow<List<MyOrder>> = callbackFlow {
        val snapshot = fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(Constants.COMPANY_ID, companyId).get().await()
        val docRef = snapshot.documents[0].reference

        val listenerRegistration = docRef.collection(Constants.ORDERS).addSnapshotListener { value, error ->
            if(error != null) {
                Log.d(TAG, "Failed to get orders: $error")
                this.trySend(emptyList<MyOrder>()).isSuccess
            }

            val ordersDocs = value?.documents
            if (!ordersDocs.isNullOrEmpty()) {
                for (ordersDoc in ordersDocs) {
                    val companyOrdersList = mutableListOf<MyOrder>()
                    val orderOwnerId = ordersDoc.get(Constants.EMPLOYEE_ID) as String

                    ordersDoc.reference.collection(Constants.ORDERED).whereEqualTo(Constants.EMPLOYEE_ID, accountService.currentUserId).get().addOnSuccessListener {
                        if (it.documents.isNotEmpty()) {
                            val orderDetail = Gson().fromJson(Gson().toJson(ordersDoc.data), MyOrder::class.java)
                            orderDetail.id = ordersDoc.id
                            orderDetail.isMyOrder = orderOwnerId == accountService.currentUserId

                            fireStore.collection(DBCollectionEnum.USERS.title).document(orderDetail.employeeId).get().addOnSuccessListener { snapShot ->
                                val employeeName = snapShot.getString(Constants.NAME) ?: ""
                                orderDetail.owner = employeeName

                                fireStore.collection(DBCollectionEnum.FAST_FOODS.title).document(orderDetail.fastFood).get().addOnSuccessListener { subSnapShot ->
                                    val fastFoodName = subSnapShot.getString(Constants.NAME) ?: ""
                                    orderDetail.fastFood = fastFoodName

                                    companyOrdersList.add(orderDetail)
                                    // Apply debounce here with a timeout of 500 milliseconds
                                    this@callbackFlow.channel.trySend(companyOrdersList).isSuccess
                                }.addOnFailureListener { e ->
                                    Log.d(TAG, "Failed to get fast food info: $e")
                                    this.trySend(emptyList<MyOrder>()).isSuccess
                                }
                            }.addOnFailureListener { e ->
                                Log.d(TAG, "Failed to get employee info: $e")
                                this.trySend(emptyList<MyOrder>()).isSuccess
                            }
                        }
                    }
                }
            } else {
                this.trySend(emptyList<MyOrder>()).isSuccess
            }
        }

        awaitClose { listenerRegistration.remove() }

    }.flowOn(ioDispatcher).debounce(50)

    override suspend fun completeOrder(orderId: String, companyId: String) {
        fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(Constants.COMPANY_ID, companyId)
            .get()
            .addOnSuccessListener { snapshot ->
                if(snapshot.documents.isNotEmpty()) {

                    val orderRef = snapshot.documents[0].reference.collection(Constants.ORDERS).document(orderId)
                    orderRef.collection(Constants.ORDERED).get().addOnSuccessListener {

                        if(it.documents.isNotEmpty()) {
                            for (doc in it.documents) {
                                doc.reference.delete()
                            }
                        }
                    }

                    orderRef.delete()
                        .addOnSuccessListener {
                            removeAllOrdersPaymentDetails(orderId = orderId)
                            ToastUtils.showToast(
                                messageId = R.string.order_completed,
                                length = Toast.LENGTH_SHORT
                            )
                        }
                        .addOnFailureListener {
                            ToastUtils.showToast(
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

    override suspend fun removeMenuItemFromOrder(
        orderId: String,
        companyId: String,
        menuItem: EmployeeMenuItem?
    ) {
        fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(Constants.COMPANY_ID, companyId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.documents.isNotEmpty()) {
                    val ordersRef = snapshot.documents[0].reference.collection(Constants.ORDERS).document(orderId)

                    ordersRef.get().addOnSuccessListener { subSnapShot ->
                        val ownerId = subSnapShot.get(Constants.EMPLOYEE_ID) as String

                        subSnapShot.reference.collection(Constants.ORDERED)
                            .whereEqualTo(Constants.EMPLOYEE_ID, menuItem?.userInfo?.employeeId)
                            .whereEqualTo(Constants.MENU_ITEM_NAME, menuItem?.menuItem?.menuName)
                            .get()
                            .addOnSuccessListener { snapshots ->
                                val document = snapshots.documents
                                if(document.isNotEmpty()) {
                                    document[0].reference.delete()
                                        .addOnSuccessListener {
                                            if (snapshots.documents.isEmpty() && ownerId == accountService.currentUserId) {
                                                ordersRef.delete()
                                            }
                                            ToastUtils.showToast(
                                                messageId = R.string.order_removed,
                                                length = Toast.LENGTH_SHORT
                                            )

                                            menuItem?.let { item ->
                                                removeMenuItemPaymentDetails(
                                                    orderId = orderId,
                                                    menuItem = item
                                                )
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.d(TAG, "Couldn't remove menu item: $e")
                                            ToastUtils.showToast(
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
                Log.e(TAG, "Couldn't get company orders: ", exception)
            }
    }

    override suspend fun removeMenuItemFromOrder(
        orderId: String,
        companyId: String,
        menuItem: com.telakuR.easyorder.home.models.EmployeeMenuItem?
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun removeOrder(orderId: String, companyId: String) {
        fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(Constants.COMPANY_ID, companyId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.documents.isNotEmpty()) {
                    val orderRef =
                        snapshot.documents[0].reference.collection(Constants.ORDERS).document(orderId)
                    orderRef.collection(Constants.ORDERED).whereEqualTo(Constants.EMPLOYEE_ID, accountService.currentUserId).get().addOnSuccessListener {
                        if (it.documents.isNotEmpty()) {
                            for (doc in it.documents) {
                                doc.reference.delete().addOnSuccessListener {
                                    removeOrderPaymentDetails(orderId = orderId)
                                    ToastUtils.showToast(
                                        messageId = R.string.order_removed,
                                        length = Toast.LENGTH_SHORT
                                    )
                                }.addOnFailureListener {
                                    ToastUtils.showToast(
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

    private fun removeOrderPaymentDetails(orderId: String) {
        val paymentRef = fireStore.collection(DBCollectionEnum.PAYMENTS.title)
            .document(orderId)

        paymentRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    val jsonPaymentsList = document.get(Constants.PAYMENT) as List<Map<String, Any>>

                    val payments = jsonPaymentsList.map { map ->
                        val employeeId = map[Constants.EMPLOYEE_ID] as String
                        val totalPrice = map[Constants.TOTAL_PRICE] as Double
                        val paid = map[Constants.PAID] as Double
                        UserPaymentModel(employeeId = employeeId, totalPayment = totalPrice, paid = paid)
                    }.toMutableList()

                    val index = payments.indexOfFirst { it.employeeId == accountService.currentUserId }

                    if (index != -1) {
                        payments.removeAt(index)
                    }

                    val updatedPaymentsList = payments.map { paymentModel ->
                        mapOf(
                            Constants.EMPLOYEE_ID to paymentModel.employeeId,
                            Constants.TOTAL_PRICE to paymentModel.totalPayment,
                            Constants.PAID to paymentModel.paid
                        )
                    }

                    paymentRef.update(Constants.PAYMENT, updatedPaymentsList)
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

    override fun removeMenuItemPaymentDetails(orderId: String, menuItem: EmployeeMenuItem) {
        val paymentRef = fireStore.collection(DBCollectionEnum.PAYMENTS.title)
            .document(orderId)

        paymentRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val jsonPaymentsList = document.get(Constants.PAYMENT) as List<Map<String, Any>>

                val payments = jsonPaymentsList.map { map ->
                    val employeeId = map[Constants.EMPLOYEE_ID] as String
                    val totalPrice = map[Constants.TOTAL_PRICE] as Double
                    val paid = map[Constants.PAID] as Double
                    UserPaymentModel(
                        employeeId = employeeId,
                        totalPayment = totalPrice,
                        paid = paid
                    )
                }.toMutableList()

                val index = payments.indexOfFirst { it.employeeId == menuItem.userInfo.employeeId }

                if (index != -1) {
                    val totalPrice = payments[index].totalPayment - menuItem.menuItem.price
                    val paid = payments[index].paid
                    val paymentModel = UserPaymentModel(
                        employeeId = accountService.currentUserId,
                        totalPayment = totalPrice,
                        paid = paid
                    )

                    if(totalPrice.toString() == Constants.DEFAULT_PRICE) {
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
                            Constants.EMPLOYEE_ID to paymentModel.employeeId,
                            Constants.TOTAL_PRICE to paymentModel.totalPayment,
                            Constants.PAID to paymentModel.paid
                        )
                    }

                    paymentRef.update(Constants.PAYMENT, updatedPaymentsList)
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

    override fun getMyOrdersFromDB(): Flow<List<MyOrderWithDetails>> {
        return easyOrderDB.myOrdersDao().getAllOrders()
    }

    override suspend fun saveOrderDetailsOnDB(orders: List<EmployeeMenuItem>, orderId: String) {
        val employeeMenuItems = orders.map { it.copy(orderId = orderId) }
        easyOrderDB.myOrdersDao().insertEmployeeMenuItems(employeeMenuItems = employeeMenuItems)
    }

    override suspend fun saveMyOrders(myCustomizedOrderList: MutableList<MyOrder>) {
        easyOrderDB.myOrdersDao().deleteAndInsertOrders(orders = myCustomizedOrderList)
    }

    override fun getPaymentDetails(
        companyId: String,
        orderId: String
    ): Flow<List<UserPaymentModelResponse>> = flow {
        val document = fireStore.collection(DBCollectionEnum.PAYMENTS.title)
            .document(orderId).get().await()

        if(document.exists()) {
            val jsonPaymentsList = document.get(Constants.PAYMENT) as List<Map<String, Any>>

            val payments = jsonPaymentsList.map { map ->
                val employeeId = map[Constants.EMPLOYEE_ID] as String
                val document = fireStore.collection(DBCollectionEnum.USERS.title).document(employeeId).get().await()
                val user = Gson().fromJson(Gson().toJson(document.data), User::class.java)
                val totalPrice = map[Constants.TOTAL_PRICE] as Double
                val paid = map[Constants.PAID] as Double
                UserPaymentModelResponse(userInfo = UserInfo(employeeId = employeeId, employeeName = user.name, employeePicture = user.profilePic), totalPayment = totalPrice, paid = paid)
            }.toMutableList()

            emit(payments)
        } else {
            emit(emptyList())
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

    override suspend fun setPaidValuesToPayments(employeeId: String, paid: String, orderId: String) {
        val paymentRef = fireStore.collection(DBCollectionEnum.PAYMENTS.title).document(orderId)

        paymentRef.get().addOnSuccessListener { document ->
            val jsonPaymentsList = document.get(Constants.PAYMENT) as List<Map<String, Any>>

            val payments = jsonPaymentsList.map { map ->
                val employeeId = map[Constants.EMPLOYEE_ID] as String
                val totalPrice = map[Constants.TOTAL_PRICE] as Double
                val paid = map[Constants.PAID] as Double
                UserPaymentModel(employeeId = employeeId, totalPayment = totalPrice, paid = paid)
            }.toMutableList()

            val index = payments.indexOfFirst { it.employeeId == employeeId }

            if (index != -1) {
                payments[index].paid = paid.toDouble()

                val updatedPaymentsList = payments.map { paymentModel ->
                    mapOf(
                        Constants.EMPLOYEE_ID to paymentModel.employeeId,
                        Constants.TOTAL_PRICE to paymentModel.totalPayment,
                        Constants.PAID to paymentModel.paid
                    )
                }

                paymentRef.update(Constants.PAYMENT, updatedPaymentsList)
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


}