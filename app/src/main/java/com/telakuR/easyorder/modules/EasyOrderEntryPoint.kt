package com.telakuR.easyorder.modules

import com.telakuR.easyorder.main.repository.NotificationsRepository
import com.telakuR.easyorder.main.repository.impl.UserDataRepositoryImpl
import com.telakuR.easyorder.room_db.db.EasyOrderDB
import com.telakuR.easyorder.utils.EasyOrder
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

object EasyOrderEntryPoint {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface EasyOrderProviderEntryPoint {
        fun userDataImplService(): UserDataRepositoryImpl
        fun notificationsImplService(): NotificationsRepository
        fun getEasyOrderDB(): EasyOrderDB
    }

    fun getUserDataImplService(): UserDataRepositoryImpl {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(EasyOrder.getInstance(), EasyOrderProviderEntryPoint::class.java)
        return hiltEntryPoint.userDataImplService()
    }

    fun getNotificationsImplService(): NotificationsRepository {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(EasyOrder.getInstance(), EasyOrderProviderEntryPoint::class.java)
        return hiltEntryPoint.notificationsImplService()
    }

    fun getEasyOrderDB(): EasyOrderDB {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(EasyOrder.getInstance(), EasyOrderProviderEntryPoint::class.java)
        return hiltEntryPoint.getEasyOrderDB()
    }
}