package com.emendo.expensestracker.core.domain

import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.repository.api.AccountsRepository
import com.emendo.expensestracker.core.data.repository.api.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetLastUsedAccountUseCase @Inject constructor(
  private val transactionRepository: TransactionRepository,
  private val accountsRepository: AccountsRepository,
) {
  operator fun invoke(): Flow<AccountModel?> =
    transactionRepository.getLastTransactionFull().flatMapLatest { transaction ->
      val sourceId = transaction?.source?.id ?: return@flatMapLatest accountsRepository.getLastAccount()
      accountsRepository.getById(sourceId)
    }
}