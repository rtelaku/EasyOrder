package com.telakuR.easyorder.modules

import com.telakuR.easyorder.repositories.impl.AccountServiceImpl
import com.telakuR.easyorder.repositories.impl.LogServiceImpl
import com.telakuR.easyorder.services.AccountService
import com.telakuR.easyorder.services.LogService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideLogService(impl: LogServiceImpl): LogService

    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

}