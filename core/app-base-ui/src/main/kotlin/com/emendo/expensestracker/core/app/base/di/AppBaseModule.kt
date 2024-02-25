package com.emendo.expensestracker.core.app.base.di

import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBusImpl
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
}