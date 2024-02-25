package com.emendo.expensestracker.core.domain.account

import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.repository.AccountRepository
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetLastUsedAccountUseCase @Inject constructor(
  private val transactionRepository: TransactionRepository,
  private val accountRepository: AccountRepository,
) {

  operator fun invoke(): Flow<AccountModel?> =
    transactionRepository.getLastTransactionFull().flatMapLatest { transaction ->
      val sourceId = transaction?.source?.id ?: return@flatMapLatest accountRepository.getLastAccount()
      accountRepository.getById(sourceId)
    }
}