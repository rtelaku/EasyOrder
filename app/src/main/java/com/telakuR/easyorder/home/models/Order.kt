package com.telakuR.easyorder.home.models

data class OrderDetails(var id: String = "", val employeeId: String = "", var owner: String = "", var fastFood: String = "", var ordered: Menu? = null)
data class Menu(val food: String, val price: Double)
