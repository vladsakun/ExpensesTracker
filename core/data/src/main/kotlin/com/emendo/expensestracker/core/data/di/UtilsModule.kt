package com.emendo.expensestracker.core.data.di

import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.amount.AmountFormatterImpl
import com.emendo.expensestracker.core.data.amount.CalculatorFormatter
import com.emendo.expensestracker.core.data.amount.CalculatorFormatterImpl
import com.emendo.expensestracker.core.data.manager.ExpeLocaleManager
import com.emendo.expensestracker.core.data.manager.ExpeLocaleManagerImpl
import com.emendo.expensestracker.core.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UtilsModule {

  @Binds
  @Singleton
  fun bindsAmountFormatter(amountFormatterImpl: AmountFormatterImpl): AmountFormatter

  @Binds
  @Singleton
  fun bindsCalculatorFormatter(calculatorFormatter: CalculatorFormatterImpl): CalculatorFormatter

  @Binds
  @Singleton
  fun bindsLocaleManager(localeManagerImpl: ExpeLocaleManagerImpl): ExpeLocaleManager

  companion object {

    @Provides
    @DecimalSeparator
    fun providesDecimalSeparator(calculatorFormatter: CalculatorFormatter): String =
      calculatorFormatter.decimalSeparator.toString()
  }
}

annotation class DecimalSeparator