package com.telakuR.easyorder.modules

import com.telakuR.easyorder.home.repository.HomeDataRepositoryImpl
import com.telakuR.easyorder.home.repository.HomeRepository
import com.telakuR.easyorder.mainRepository.UserDataRepository
import com.telakuR.easyorder.mainRepository.impl.AccountServiceImpl
import com.telakuR.easyorder.mainRepository.impl.LogServiceImpl
import com.telakuR.easyorder.mainRepository.impl.UserDataRepositoryImpl
import com.telakuR.easyorder.services.AccountService
import com.telakuR.easyorder.services.LogService
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

}