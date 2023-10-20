package com.emendo.expensestracker.core.domain

import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.repository.api.AccountsRepository
import com.emendo.expensestracker.core.data.repository.api.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLastUsedAccountUseCase @Inject constructor(
  private val transactionsRepository: TransactionsRepository,
  private val accountsRepository: AccountsRepository,
) {
  operator fun invoke(): Flow<AccountModel?> =
    transactionsRepository.getTransactionsFull().flatMapLatest { transactions ->
      val sourceId = transactions.lastOrNull()?.source?.id ?: return@flatMapLatest getFirstAccount()
      accountsRepository.getById(sourceId)
    }

  private fun getFirstAccount(): Flow<AccountModel?> =
    accountsRepository.getAccounts().map { it.firstOrNull() }
}