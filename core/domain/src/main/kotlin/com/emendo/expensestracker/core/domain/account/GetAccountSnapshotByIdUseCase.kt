package com.emendo.expensestracker.core.domain.account

import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.repository.AccountRepository
import javax.inject.Inject

class GetAccountSnapshotByIdUseCase @Inject constructor(
  private val accountRepository: AccountRepository,
) {

  operator fun invoke(id: Long): AccountModel? =
    accountRepository.getAccountsSnapshot().firstOrNull { it.id == id }
}