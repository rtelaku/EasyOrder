package com.telakuR.easyorder.home.models

data class UserPaymentModel(val orderId: String, val totalPayment: Double, val paid: Double)
data class UserPaymentModelResponse(val employeePic: String, val employeeName: String, val totalPayment: Double, val paid: Double)