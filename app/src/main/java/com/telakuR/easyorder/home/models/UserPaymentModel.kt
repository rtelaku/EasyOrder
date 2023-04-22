package com.telakuR.easyorder.home.models

data class UserPaymentModel(val employeeId: String, val totalPayment: Double, var paid: Double)
data class UserPaymentModelResponse(val userInfo: UserInfo, val totalPayment: Double, val paid: Double)