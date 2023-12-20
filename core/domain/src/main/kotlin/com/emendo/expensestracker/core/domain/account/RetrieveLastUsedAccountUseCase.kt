package com.emendo.expensestracker.core.domain.account

import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.repository.api.AccountRepository
import com.emendo.expensestracker.core.data.repository.api.TransactionRepository
import javax.inject.Inject

class RetrieveLastUsedAccountUseCase @Inject constructor(
  private val transactionRepository: TransactionRepository,
  private val accountRepository: AccountRepository,
) {
  suspend operator fun invoke(): AccountModel? {
    val lastTransaction = transactionRepository.retrieveLastTransaction()
      ?: return accountRepository.retrieveLastAccount()

    val sourceId = lastTransaction.source.id
    return accountRepository.retrieveById(sourceId)
  }
}