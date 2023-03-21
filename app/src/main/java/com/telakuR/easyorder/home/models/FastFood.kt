package com.telakuR.easyorder.home.models

import android.view.MenuItem

data class FastFood(val name: String, val picture: String, var menu: List<FastFoodMenu> = emptyList())
data class FastFoodMenu(val name: String, var menu: List<MenuItem>)
data class MenuItem(val picture: String, val name: String, val price: Double)
