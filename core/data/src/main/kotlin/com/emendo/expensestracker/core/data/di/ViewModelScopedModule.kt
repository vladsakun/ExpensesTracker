package com.emendo.expensestracker.core.data.di

import com.emendo.expensestracker.core.data.CalculatorInput
import com.emendo.expensestracker.core.data.CalculatorInputImpl
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