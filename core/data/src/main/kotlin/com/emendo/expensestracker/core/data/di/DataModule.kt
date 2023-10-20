package com.emendo.expensestracker.core.data.di

import com.emendo.expensestracker.core.data.manager.AppInitManager
import com.emendo.expensestracker.core.data.manager.AppInitManagerImpl
import com.emendo.expensestracker.core.data.manager.CurrencyConverter
import com.emendo.expensestracker.core.data.manager.CurrencyConverterImpl
import com.emendo.expensestracker.core.data.repository.*
import com.emendo.expensestracker.core.data.repository.api.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

  @Binds
  @Singleton
  fun bindsAccountsRepository(accountsRepository: OfflineFirstAccountsRepository): AccountsRepository

  @Binds
  @Singleton
  fun bindsCategoriesRepository(categoriesRepository: OfflineFirstCategoryRepository): CategoryRepository

  @Binds
  @Singleton
  fun bindsTransactionRepository(transactionRepository: OfflineFirstTransactionsRepository): TransactionsRepository

  @Binds
  @Singleton
  fun bindsUserDataRepository(userDataRepository: OfflineFirstUserDataRepository): UserDataRepository

  @Binds
  @Singleton
  fun bindsAppInitManager(appInitManagerImpl: AppInitManagerImpl): AppInitManager

  @Binds
  @Singleton
  fun bindsCurrencyConverter(currencyConverter: CurrencyConverterImpl): CurrencyConverter

  @Binds
  @Singleton
  fun bindsCurrencyRatesRepository(currencyRatesRepository: OfflineFirstCurrencyRatesRepository): CurrencyRatesRepository

  @Binds
  @Singleton
  fun providesCurrencyRepository(currencyRepository: OfflineFirstCurrencyRepository): CurrencyRepository
}