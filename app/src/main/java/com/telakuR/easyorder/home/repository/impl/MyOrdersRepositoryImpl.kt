package com.telakuR.easyorder.home.repository.impl

import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.models.*
import com.telakuR.easyorder.home.repository.MyOrdersRepository
import com.telakuR.easyorder.main.enums.DBCollectionEnum
import com.telakuR.easyorder.main.models.User
import com.telakuR.easyorder.main.services.AccountService
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.utils.Constants
import com.telakuR.easyorder.utils.ToastUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MyOrdersRepositoryImpl @Inject constructor(
    @IoDispatcher val ioDispatcher: CoroutineDispatcher,
    private val fireStore: FirebaseFirestore,
    private val accountService: AccountService,
) : MyOrdersRepository {

    private val TAG = MyOrdersRepositoryImpl::class.simpleName
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun getMyOrderDetails(companyId: String, orderId: String, isMyOrder: Boolean): Flow<List<EmployeeMenuItem>> =
        callbackFlow {
            val orderDoc = fireStore.collection(DBCollectionEnum.ORDERS.title)
                .whereEqualTo(Constants.COMPANY_ID, companyId).get().await()

            val docRef = orderDoc.documents[0].reference.collection(Constants.ORDERS).document(orderId)
                .collection(Constants.ORDERED)

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
                            trySend(employeeOrders)
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

    override fun getMyOrders(companyId: String): Flow<List<OrderDetails>> = flow {
        val companyOrdersList = mutableListOf<OrderDetails>()

        try {
            val snapshot = getOrdersSnapshot(companyId)
            val docRef = snapshot.documents[0].reference
            val ordersSnapshot = docRef.collection(Constants.ORDERS).get().await()
            val ordersDocs = ordersSnapshot.documents

            if (ordersDocs.isNotEmpty()) {
                for (ordersDoc in ordersDocs) {
                    val orderedRef = getOrderedSnapshot(ordersDoc)
                    val employeeId = ordersDoc.get(Constants.EMPLOYEE_ID) as String

                    if(orderedRef.documents.isNotEmpty() || employeeId == accountService.currentUserId) {
                        val orderDetail = createOrderDetailsFromJson(ordersDoc)
                        orderDetail.owner = getEmployeeName(orderDetail.employeeId)
                        orderDetail.fastFood = getFastFoodName(orderDetail.fastFood)
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

    private suspend fun getOrdersSnapshot(companyId: String): QuerySnapshot {
        return fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(Constants.COMPANY_ID, companyId)
            .get()
            .await()
    }

    private suspend fun getOrderedSnapshot(ordersDoc: DocumentSnapshot): QuerySnapshot {
        return ordersDoc.reference.collection(Constants.ORDERED).whereEqualTo(
            Constants.EMPLOYEE_ID, accountService.currentUserId
        ).get().await()
    }

    private fun createOrderDetailsFromJson(ordersDoc: DocumentSnapshot): OrderDetails {
        val orderDetail = Gson().fromJson(Gson().toJson(ordersDoc.data), OrderDetails::class.java)
        orderDetail.id = ordersDoc.id
        return orderDetail
    }

    private suspend fun getEmployeeName(employeeId: String): String {
        val document = fireStore.collection(DBCollectionEnum.USERS.title)
            .document(employeeId)
            .get()
            .await()
        return document.getString(Constants.NAME) ?: ""
    }

    private suspend fun getFastFoodName(fastFoodId: String): String {
        val document = fireStore.collection(DBCollectionEnum.FAST_FOODS.title)
            .document(fastFoodId)
            .get()
            .await()
        return document.getString(Constants.NAME) ?: ""
    }

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

    private fun convertJsonPaymentsList(jsonPaymentsList: List<Map<String, Any>>): MutableList<UserPaymentModel> {
        return jsonPaymentsList.map { map ->
            val employeeId = map[Constants.EMPLOYEE_ID] as String
            val totalPrice = map[Constants.TOTAL_PRICE] as Double
            val paid = map[Constants.PAID] as Double
            UserPaymentModel(employeeId = employeeId, totalPayment = totalPrice, paid = paid)
        }.toMutableList()
    }

    private fun convertPaymentsToMapList(payments: MutableList<UserPaymentModel>): List<Map<String, Any>> {
        return payments.map { paymentModel ->
            mapOf(
                Constants.EMPLOYEE_ID to paymentModel.employeeId,
                Constants.TOTAL_PRICE to paymentModel.totalPayment,
                Constants.PAID to paymentModel.paid
            )
        }
    }

    private fun updatePaymentRef(paymentRef: DocumentReference, updatedPaymentsList: List<Map<String, Any>>): Task<Void> {
        return paymentRef.update(Constants.PAYMENT, updatedPaymentsList)
    }

    private fun handlePaymentUpdateSuccess() {
        Log.d(TAG, "Payment updated successfully")
    }

    private fun handlePaymentUpdateFailure(exception: Exception) {
        Log.d(TAG, "Failed to update payment: $exception")
    }

    private fun handlePaymentDetailsRemovalFailure(exception: Exception) {
        Log.d(TAG, "Couldn't remove payment details: $exception")
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

    override fun setPaidValuesToPayments(employeeId: String, paid: String, orderId: String) {
        val paymentRef = fireStore.collection(DBCollectionEnum.PAYMENTS.title).document(orderId)

        paymentRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val payments = extractPaymentsFromDocument(document)

                    val index = payments.indexOfFirst { it.employeeId == employeeId }

                    if (index != -1) {
                        updatePaymentPaidValue(payments, index, paid)

                        val updatedPaymentsList = createUpdatedPaymentsList(payments)

                        updateOrderPayment(paymentRef, updatedPaymentsList)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Couldn't set values to payments: $exception")
            }
    }

    private fun updatePaymentPaidValue(payments: MutableList<UserPaymentModel>, index: Int, paid: String) {
        payments[index].paid = paid.toDouble()
    }

    private fun createUpdatedPaymentsList(payments: List<UserPaymentModel>): List<UserPaymentModel> {
        return payments.map { paymentModel ->
            UserPaymentModel(
                employeeId = paymentModel.employeeId,
                totalPayment = paymentModel.totalPayment,
                paid = paymentModel.paid
            )
        }
    }

    override fun removeMenuItemFromOrder(orderId: String, companyId: String, menuItem: EmployeeMenuItem) {
        fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(Constants.COMPANY_ID, companyId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.documents.isNotEmpty()) {
                    val ordersRef = snapshot.documents[0].reference.collection(Constants.ORDERS).document(orderId)

                    ordersRef.get().addOnSuccessListener { subSnapShot ->
                        val ownerId = subSnapShot.get(Constants.EMPLOYEE_ID) as String

                        subSnapShot.reference.collection(Constants.ORDERED)
                            .whereEqualTo(Constants.EMPLOYEE_ID, accountService.currentUserId)
                            .whereEqualTo(Constants.MENU_ITEM_NAME, menuItem.menuItem.menuName)
                            .limit(1)
                            .get()
                            .addOnSuccessListener { snapshots ->
                                if (snapshots.documents.isNotEmpty()) {
                                    val document = snapshots.documents[0]

                                    document?.reference?.delete()?.addOnSuccessListener {
                                        ToastUtils.showToast(
                                            messageId = R.string.order_removed,
                                            length = Toast.LENGTH_SHORT
                                        )
                                        removeMenuItemPaymentDetails(
                                            orderId = orderId,
                                            menuItem = menuItem
                                        )

                                        if (snapshots.documents.isEmpty() && ownerId == accountService.currentUserId) {
                                            ordersRef.delete()
                                        }
                                    }?.addOnFailureListener { e ->
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

    override fun removeOrder(companyId: String, orderId: String) {
        fireStore.collection(DBCollectionEnum.ORDERS.title)
            .whereEqualTo(Constants.COMPANY_ID, companyId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.documents.isNotEmpty()) {
                    val ordersRef = snapshot.documents[0].reference.collection(Constants.ORDERS)
                        .document(orderId)
                    ordersRef.delete()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't get company orders: ", exception)
            }
    }

    override fun removeMenuItemPaymentDetails(orderId: String, menuItem: EmployeeMenuItem) {
        val paymentRef = fireStore.collection(DBCollectionEnum.PAYMENTS.title).document(orderId)

        paymentRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val payments = extractPaymentsFromDocument(document)

                    val index = payments.indexOfFirst { it.employeeId == menuItem.userInfo.employeeId }

                    if (index != -1) {
                        updatePayment(payments, index, menuItem)
                    }

                    if (payments.isEmpty()) {
                        deleteOrderPayment(paymentRef)
                    } else {
                        updateOrderPayment(paymentRef, payments)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Couldn't remove payment details: $exception")
            }
    }

    private fun extractPaymentsFromDocument(document: DocumentSnapshot): MutableList<UserPaymentModel> {
        val jsonPaymentsList = document.get(Constants.PAYMENT) as? List<Map<String, Any>> ?: emptyList()

        return jsonPaymentsList.map { map ->
            val employeeId = map[Constants.EMPLOYEE_ID] as String
            val totalPrice = map[Constants.TOTAL_PRICE] as Double
            val paid = map[Constants.PAID] as Double

            UserPaymentModel(
                employeeId = employeeId,
                totalPayment = totalPrice,
                paid = paid
            )
        }.toMutableList()
    }

    private fun updatePayment(payments: MutableList<UserPaymentModel>, index: Int, menuItem: EmployeeMenuItem) {
        val totalPrice = payments[index].totalPayment - menuItem.menuItem.price
        val paid = payments[index].paid
        val paymentModel = UserPaymentModel(
            employeeId = accountService.currentUserId,
            totalPayment = totalPrice,
            paid = paid
        )

        if (totalPrice.toString() == Constants.DEFAULT_PRICE) {
            payments.removeAt(index)
        } else {
            payments[index] = paymentModel
        }
    }

    private fun deleteOrderPayment(paymentRef: DocumentReference) {
        paymentRef.delete()
            .addOnSuccessListener {
                Log.d(TAG, "Order payment deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Failed to delete order payments: $exception")
            }
    }

    private fun updateOrderPayment(paymentRef: DocumentReference, payments: List<UserPaymentModel>) {
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