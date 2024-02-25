package com.emendo.expensestracker.core.data.di

import com.emendo.expensestracker.core.data.CalculatorInputImpl
import com.emendo.expensestracker.data.api.CalculatorInput
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ViewModelScopedModule {

  @Binds
  fun bindsCalculatorInput(calculatorInput: CalculatorInputImpl): CalculatorInput
}