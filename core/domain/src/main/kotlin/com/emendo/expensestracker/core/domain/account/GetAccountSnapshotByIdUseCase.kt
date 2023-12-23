package com.emendo.expensestracker.core.domain.account

import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.repository.api.AccountRepository
import javax.inject.Inject

class GetAccountSnapshotByIdUseCase @Inject constructor(
  private val accountRepository: AccountRepository,
) {

  operator fun invoke(id: Long): AccountModel? {
    return accountRepository.getAccountsSnapshot().firstOrNull { it.id == id }
  }
}