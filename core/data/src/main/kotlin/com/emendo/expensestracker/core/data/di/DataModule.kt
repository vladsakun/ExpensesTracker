package com.emendo.expensestracker.core.data.di

import com.emendo.expensestracker.core.data.repository.AccountsRepository
import com.emendo.expensestracker.core.data.repository.OfflineFirstAccountsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

  @Binds
  fun bindsAccountsRepository(
    accountsRepository: OfflineFirstAccountsRepository,
  ): AccountsRepository
}