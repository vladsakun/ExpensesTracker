package com.emendo.expensestracker.core.domain.account

import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.repository.api.AccountRepository
import com.emendo.expensestracker.core.data.repository.api.TransactionRepository
import javax.inject.Inject

class GetLastTransferAccountOrRandomUseCase @Inject constructor(
  private val transactionRepository: TransactionRepository,
  private val accountRepository: AccountRepository,
) {

  suspend operator fun invoke(sourceAccountId: Long?): AccountModel? {
    if (sourceAccountId == null) {
      return getRandomAccount(null)
    }

    val lastTransferTransaction = transactionRepository.retrieveLastTransferTransaction(sourceAccountId)
    return lastTransferTransaction?.target as? AccountModel ?: getRandomAccount(sourceAccountId)
  }

  private fun getRandomAccount(sourceAccountId: Long?): AccountModel? =
    accountRepository.getAccountsSnapshot().shuffled().firstOrNull { it.id != sourceAccountId }
}