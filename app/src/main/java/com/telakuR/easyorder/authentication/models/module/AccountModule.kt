package com.telakuR.easyorder.authentication.models.module

import com.telakuR.easyorder.authentication.models.impl.AccountServiceImpl
import com.telakuR.easyorder.authentication.models.impl.LogServiceImpl
import com.telakuR.easyorder.authentication.models.services.AccountService
import com.telakuR.easyorder.authentication.models.services.LogService
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