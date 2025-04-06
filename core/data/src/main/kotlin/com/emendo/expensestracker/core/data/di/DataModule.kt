package com.emendo.expensestracker.core.data.di

import com.emendo.expensestracker.core.data.manager.CurrencyCacheManagerImpl
import com.emendo.expensestracker.core.data.manager.CurrencyConverterImpl
import com.emendo.expensestracker.core.data.repository.*
import com.emendo.expensestracker.data.api.manager.CurrencyCacheManager
import com.emendo.expensestracker.data.api.manager.CurrencyConverter
import com.emendo.expensestracker.data.api.repository.*
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
  fun bindsAccountsRepository(accountsRepository: OfflineFirstAccountRepository): AccountRepository

  @Binds
  @Singleton
  fun bindsCategoriesRepository(categoriesRepository: OfflineFirstCategoryRepository): CategoryRepository

  @Binds
  @Singleton
  fun bindsTransactionRepository(transactionRepository: OfflineFirstTransactionRepository): TransactionRepository

  @Binds
  @Singleton
  fun bindsUserDataRepository(userDataRepository: OfflineFirstUserDataRepository): UserDataRepository

  @Binds
  @Singleton
  fun bindsCurrencyConverter(currencyConverter: CurrencyConverterImpl): CurrencyConverter

  @Binds
  @Singleton
  fun bindsCurrencyRatesRepository(currencyRatesRepository: OfflineFirstCurrencyRateRepository): CurrencyRateRepository

  @Binds
  @Singleton
  fun bindsCurrencyCacheManager(currencyCacheManagerImpl: CurrencyCacheManagerImpl): CurrencyCacheManager

  @Binds
  @Singleton
  fun bindsSubcategoryRepository(subcategoryRepository: OfflineFirstSubcategoryRepository): SubcategoryRepository
}