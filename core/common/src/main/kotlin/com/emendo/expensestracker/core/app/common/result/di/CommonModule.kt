package com.emendo.expensestracker.core.app.common.result.di

import com.emendo.expensestracker.core.app.common.result.AmountFormatter
import com.emendo.expensestracker.core.app.common.result.AmountFormatterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CommonModule {

  @Binds
  fun bindsAmountFormatter(amountFormatter: AmountFormatterImpl): AmountFormatter
}