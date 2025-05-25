package com.emendo.expensestracker.core.data.di

import com.emendo.expensestracker.core.android.api.OnAppCreate
import com.emendo.expensestracker.core.data.RepositorySetup
import com.emendo.expensestracker.core.data.amount.AmountFormatterImpl
import com.emendo.expensestracker.core.data.amount.CalculatorFormatterImpl
import com.emendo.expensestracker.core.data.manager.ExpeDateManagerImpl
import com.emendo.expensestracker.core.data.manager.ExpeLocaleManagerImpl
import com.emendo.expensestracker.core.data.manager.ExpeTimeZoneManagerImpl
import com.emendo.expensestracker.data.api.DecimalSeparator
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.amount.CalculatorFormatter
import com.emendo.expensestracker.data.api.manager.ExpeDateManager
import com.emendo.expensestracker.data.api.manager.ExpeLocaleManager
import com.emendo.expensestracker.data.api.manager.ExpeTimeZoneManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
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

  @Binds
  @Singleton
  fun bindsDateManager(dateManagerImpl: ExpeDateManagerImpl): ExpeDateManager

  @Binds
  @Singleton
  fun bindsTimeZoneManager(timeZoneManagerImpl: ExpeTimeZoneManagerImpl): ExpeTimeZoneManager

  @Binds
  @IntoSet
  fun setupRepositoryOnCreate(repositorySetup: RepositorySetup): OnAppCreate

  companion object {

    @Provides
    @DecimalSeparator
    fun providesDecimalSeparator(calculatorFormatter: CalculatorFormatter): String =
      calculatorFormatter.decimalSeparator.toString()
  }
}

