package com.telakuR.easyorder.home.models

data class FastFood(var id: String, val name: String, val picture: String)
data class MenuItem(val menuPicture: String, val menuName: String, val price: Double)

data class EmployeeMenuItem(val userInfo: UserInfo, val menuItem: MenuItem)
data class EmployeeMenuItemResponse(val employeeId: String, val menuItem: MenuItem)
