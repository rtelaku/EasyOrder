package com.telakuR.easyorder.room_db.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.telakuR.easyorder.room_db.dao.*
import com.telakuR.easyorder.room_db.enitites.*

@Database(
    entities = [CompanyOrderDetails::class, NotificationModel::class,
        Employee::class, EmployeeRequest::class, Profile::class, MyOrder::class],
    version = 1,
    exportSchema = false
)
abstract class EasyOrderDB : RoomDatabase() {

    abstract fun companyEmployeesDao(): CompanyEmployeesDao

    abstract fun companyRequestsDao(): CompanyEmployeeRequestsDao

    abstract fun companyOrdersDao(): CompanyOrdersDao

    abstract fun myOrdersDao(): MyOrdersDao

    abstract fun notificationsDao(): NotificationsDao

    abstract fun profileDao(): ProfileDao

}
