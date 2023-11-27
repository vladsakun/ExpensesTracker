package com.emendo.expensestracker.core.domain

import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.repository.api.AccountsRepository
import com.emendo.expensestracker.core.data.repository.api.TransactionRepository
import javax.inject.Inject

class RetrieveLastUsedAccountUseCase @Inject constructor(
  private val transactionRepository: TransactionRepository,
  private val accountsRepository: AccountsRepository,
) {
  suspend operator fun invoke(): AccountModel? {
    val lastTransaction = transactionRepository.retrieveLastTransactionFull()
      ?: return accountsRepository.retrieveLastAccount()

    val sourceId = lastTransaction.source.id
    return accountsRepository.retrieveById(sourceId)
  }
}