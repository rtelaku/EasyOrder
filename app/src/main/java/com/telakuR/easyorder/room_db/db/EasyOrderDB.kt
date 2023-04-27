package com.telakuR.easyorder.room_db.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.telakuR.easyorder.home.models.OrderDetails
import com.telakuR.easyorder.main.models.NotificationModel
import com.telakuR.easyorder.main.models.User
import com.telakuR.easyorder.room_db.dao.CompanyEmployeeRequestsDao
import com.telakuR.easyorder.room_db.dao.CompanyEmployeesDao
import com.telakuR.easyorder.room_db.dao.CompanyOrdersDao
import com.telakuR.easyorder.room_db.dao.NotificationsDao
import com.telakuR.easyorder.room_db.enitites.CompanyOrderDetails
import com.telakuR.easyorder.room_db.enitites.Employee
import com.telakuR.easyorder.room_db.enitites.EmployeeRequest

@Database(entities = [CompanyOrderDetails::class, NotificationModel::class, Employee::class, EmployeeRequest::class], version = 1, exportSchema = false)
abstract class EasyOrderDB : RoomDatabase() {

    abstract fun companyEmployeesDao(): CompanyEmployeesDao

    abstract fun companyRequestsDao(): CompanyEmployeeRequestsDao

    abstract fun companyOrdersDao(): CompanyOrdersDao

//    abstract fun employeeOrdersDao()
//
//    abstract fun profileDao()
//
    abstract fun notificationsDao(): NotificationsDao
}