package com.emendo.expensestracker.core.domain.di

import com.emendo.expensestracker.core.domain.account.GetAccountByIdUseCase
import com.emendo.expensestracker.core.domain.api.CreateTransactionController
import com.emendo.expensestracker.core.domain.common.GetModelComponent
import com.emendo.expensestracker.core.domain.transaction.controller.CreateTransactionControllerImpl
import com.emendo.expensestracker.data.api.model.AccountModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DomainModule {

  @Binds
  @Singleton
  fun bindsCreateTransactionController(createTransactionController: CreateTransactionControllerImpl): CreateTransactionController

  @Binds
  @Singleton
  fun bindsGetAccountByIdUseCase(getAccountByIdUseCase: GetAccountByIdUseCase): GetModelComponent<AccountModel>
}