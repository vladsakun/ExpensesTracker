package com.emendo.expensestracker.core.data.di

import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.amount.AmountFormatterImpl
import com.emendo.expensestracker.core.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UtilsModule {

  @Binds
  fun bindsAmountFormatter(amountFormatterImpl: AmountFormatterImpl): AmountFormatter
}