package com.emendo.expensestracker.core.database

import com.emendo.expensestracker.core.database.dao.AccountDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {
  @Provides
  fun providesAccountDao(database: ExpDatabase): AccountDao = database.accountDao()
}