package com.emendo.expensestracker.core.app.base.di

import com.emendo.expensestracker.core.app.base.helper.NumericKeyboardCommanderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ViewModelScopedModule {

  @Binds
  fun bindsCalculatorCommander(calculatorCommanderImpl: NumericKeyboardCommanderImpl): com.emendo.expensestracker.app.base.api.helper.NumericKeyboardCommander
}