package com.emendo.expensestracker.core.domain

import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.repository.api.AccountRepository
import javax.inject.Inject

class GetAccountSnapshotById @Inject constructor(
  private val accountRepository: AccountRepository,
) {

  operator fun invoke(id: Long): AccountModel? {
    return accountRepository.accountsSnapshot.find { it.id == id }
  }
}