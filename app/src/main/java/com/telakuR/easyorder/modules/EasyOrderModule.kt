package com.telakuR.easyorder.modules

import android.content.Context
import androidx.room.Room
import com.telakuR.easyorder.room_db.db.EasyOrderDB
import com.telakuR.easyorder.utils.EasyOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EasyOrderModule {

    @Singleton
    @Provides
    fun provideApplicationContext(@ApplicationContext context: Context): EasyOrder {
        return context as EasyOrder
    }

    @Provides
    @Singleton
    fun provideDB(
        @ApplicationContext context: Context,
        providerDB: Provider<EasyOrderDB>
    ): EasyOrderDB {
        return Room.databaseBuilder(
            context,
            EasyOrderDB::class.java,
            "easyorder_db"
        )
            .build()
    }
}
