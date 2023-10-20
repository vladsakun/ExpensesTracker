package com.emendo.expensestracker.core.app.common.network.di

import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

  @Provides
  @Dispatcher(ExpeDispatchers.IO)
  fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

  @Provides
  @Dispatcher(ExpeDispatchers.Default)
  fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}