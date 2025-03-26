package com.emendo.expensestracker.core.domain.account

import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.repository.AccountRepository
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import javax.inject.Inject

class GetLastTransferAccountOrFirstUseCase @Inject constructor(
  private val transactionRepository: TransactionRepository,
  private val accountRepository: AccountRepository,
) {

  suspend operator fun invoke(sourceAccountId: Long?): AccountModel? {
    if (sourceAccountId == null) {
      return getNextAccount(null)
    }

    val lastTransferTransaction = transactionRepository.retrieveLastTransferTransaction(sourceAccountId)
    return lastTransferTransaction?.target as? AccountModel ?: getNextAccount(sourceAccountId)
  }

  private fun getNextAccount(sourceAccountId: Long?): AccountModel? =
    accountRepository.getAccountsSnapshot().firstOrNull { it.id != sourceAccountId }
}