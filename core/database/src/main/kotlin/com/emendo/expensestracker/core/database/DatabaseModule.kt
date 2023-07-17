package com.emendo.expensestracker.core.database

import android.content.Context
import androidx.room.Room
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
  fun providesExpDatabase(
    @ApplicationContext context: Context,
  ): ExpDatabase = Room.databaseBuilder(
    context,
    ExpDatabase::class.java,
    "expensestracker-database",
  ).build()
}