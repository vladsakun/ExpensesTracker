package com.emendo.expensestracker.core.app.base.di

import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBusImpl
import com.emendo.expensestracker.core.app.base.manager.AppInitManager
import com.emendo.expensestracker.core.app.base.manager.AppInitManagerImpl
import com.emendo.expensestracker.core.app.base.manager.CreateTransactionRepository
import com.emendo.expensestracker.core.app.base.manager.CreateTransactionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppBaseModule {

  @Binds
  @Singleton
  fun bindAppNavigationEventBus(appNavigationEventBusImpl: AppNavigationEventBusImpl): AppNavigationEventBus

  @Binds
  @Singleton
  fun bindsCreateTransactionRepository(createTransactionRepository: CreateTransactionRepositoryImpl): CreateTransactionRepository

  @Binds
  @Singleton
  fun bindsAppInitManager(appInitManagerImpl: AppInitManagerImpl): AppInitManager
}