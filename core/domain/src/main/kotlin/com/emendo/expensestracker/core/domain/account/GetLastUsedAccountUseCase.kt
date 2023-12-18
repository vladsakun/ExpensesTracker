package com.emendo.expensestracker.core.domain.account

import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.repository.api.AccountRepository
import com.emendo.expensestracker.core.data.repository.api.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetLastUsedAccountUseCase @Inject constructor(
  private val transactionRepository: TransactionRepository,
  private val accountRepository: AccountRepository,
) {

  operator fun invoke(): Flow<AccountModel?> =
    transactionRepository.lastTransactionFull.flatMapLatest { transaction ->
      val sourceId = transaction?.source?.id ?: return@flatMapLatest accountRepository.getLastAccount()
      accountRepository.getById(sourceId)
    }
}