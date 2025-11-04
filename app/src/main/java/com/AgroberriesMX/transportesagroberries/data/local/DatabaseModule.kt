package com.AgroberriesMX.transportesagroberries.data.local

import android.content.Context
import com.AgroberriesMX.transportesagroberries.data.RecordsRepositoryImpl
import com.AgroberriesMX.transportesagroberries.domain.RecordsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabaseHelper(@ApplicationContext context: Context): DatabaseHelper{
        return DatabaseHelper(context)
    }
    @Provides
    @Singleton
    fun provideAgroAccessLocalDBService(databaseHelper: DatabaseHelper): TransportesLocalDBService {
        return TransportesLocalDBServiceImpl(databaseHelper)
    }

    @Provides
    @Singleton
    fun provideRecordsRepository(localDBService: TransportesLocalDBService): RecordsRepository {
        return RecordsRepositoryImpl(localDBService)
    }
}