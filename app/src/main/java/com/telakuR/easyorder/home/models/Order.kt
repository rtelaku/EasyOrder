package com.telakuR.easyorder.home.models

data class Order(var orderId: String = "", val companyId: String, var orders: List<OrderDetails>)
data class OrderDetails(var id: String, val employeeId: String, var owner: String, val fastFood: String, var ordered: Menu)
data class Menu(val food: String, val price: Double)
