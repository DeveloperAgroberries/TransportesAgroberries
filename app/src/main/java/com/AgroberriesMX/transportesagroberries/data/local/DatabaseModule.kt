package com.AgroberriesMX.transportesagroberries.data.local

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabaseHelper(context: Context): DatabaseHelper{
        return DatabaseHelper(context)
    }
}