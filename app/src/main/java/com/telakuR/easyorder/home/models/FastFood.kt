package com.telakuR.easyorder.home.models

data class FastFood(val name: String, val picture: String)
data class MenuItem(val picture: String, val name: String, val price: Double)
data class EmployeeMenuItem(val userInfo: UserInfo, val menuItem: MenuItem)
data class UserInfo(var id: String = "", var name: String, var picture: String)
data class EmployeeMenuItemResponse(val employeeId: String, val menuItem: MenuItem)
