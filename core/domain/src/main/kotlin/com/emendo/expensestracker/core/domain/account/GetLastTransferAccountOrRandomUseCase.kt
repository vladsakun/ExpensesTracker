package com.emendo.expensestracker.core.domain.account

import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.repository.AccountRepository
import com.emendo.expensestracker.data.api.repository.TransactionRepository
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