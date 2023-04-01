package com.telakuR.easyorder.home.models

data class FastFood(val name: String, val picture: String)
data class MenuItem(val picture: String, val name: String, val price: Double)
data class EmployeeMenuItem(val employeeId: String, val menuItem: MenuItem)
