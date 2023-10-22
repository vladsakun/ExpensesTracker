package com.emendo.expensestracker.core.database

import com.emendo.expensestracker.core.database.dao.AccountDao
import com.emendo.expensestracker.core.database.dao.CategoryDao
import com.emendo.expensestracker.core.database.dao.CurrencyRateDao
import com.emendo.expensestracker.core.database.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {
  @Provides
  fun providesAccountDao(database: ExpDatabase): AccountDao = database.accountDao()

  @Provides
  fun providesCategoryDao(database: ExpDatabase): CategoryDao = database.categoryDao()

  @Provides
  fun providesTransactionDao(database: ExpDatabase): TransactionDao = database.transactionDao()

  @Provides
  fun providesCurrencyRateDao(database: ExpDatabase): CurrencyRateDao = database.currencyRateDao()
}