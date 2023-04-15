package com.telakuR.easyorder.modules

import com.telakuR.easyorder.mainRepository.impl.UserDataRepositoryImpl
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
    }

    fun getUserDataImplService(): UserDataRepositoryImpl {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(EasyOrder.getInstance(), EasyOrderProviderEntryPoint::class.java)
        return hiltEntryPoint.userDataImplService()
    }
}