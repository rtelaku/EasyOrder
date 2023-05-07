package com.telakuR.easyorder.modules

import com.telakuR.easyorder.home.repository.EmployeeRequestsRepository
import com.telakuR.easyorder.home.repository.impl.HomeDataRepositoryImpl
import com.telakuR.easyorder.home.repository.HomeRepository
import com.telakuR.easyorder.home.repository.MyOrdersRepository
import com.telakuR.easyorder.home.repository.impl.EmployeeRequestsRepositoryImpl
import com.telakuR.easyorder.home.repository.impl.MyOrdersRepositoryImpl
import com.telakuR.easyorder.main.repository.NotificationsRepository
import com.telakuR.easyorder.main.repository.UserDataRepository
import com.telakuR.easyorder.main.repository.impl.AccountServiceImpl
import com.telakuR.easyorder.main.repository.impl.LogServiceImpl
import com.telakuR.easyorder.main.repository.impl.NotificationsRepositoryImpl
import com.telakuR.easyorder.main.repository.impl.UserDataRepositoryImpl
import com.telakuR.easyorder.main.services.AccountService
import com.telakuR.easyorder.main.services.LogService
import com.telakuR.easyorder.setupProfile.repository.SetupProfileRepository
import com.telakuR.easyorder.setupProfile.repository.impl.SetupProfileRepositoryImpl
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
    abstract fun provideUserDataService(impl: UserDataRepositoryImpl): UserDataRepository

    @Binds
    abstract fun provideHomeDataService(impl: HomeDataRepositoryImpl): HomeRepository

    @Binds
    abstract fun provideProfileDataService(impl: SetupProfileRepositoryImpl): SetupProfileRepository

    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    abstract fun provideNotificationsService(impl: NotificationsRepositoryImpl): NotificationsRepository

    @Binds
    abstract fun provideEmployeeRequestsService(impl: EmployeeRequestsRepositoryImpl): EmployeeRequestsRepository

    @Binds
    abstract fun provideMyOrdersService(impl: MyOrdersRepositoryImpl): MyOrdersRepository

}