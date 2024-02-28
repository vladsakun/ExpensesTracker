package com.emendo.expensestracker.core.domain.account

import com.emendo.expensestracker.core.domain.common.GetModelComponent
import com.emendo.expensestracker.core.domain.common.GetModelDecorator
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetAccountSnapshotByIdUseCase @Inject constructor(
  private val accountRepository: AccountRepository,
  override val getModelComponent: GetModelComponent<AccountModel>,
) : GetModelDecorator<AccountModel> {

  override operator fun invoke(id: Long): Flow<AccountModel> {
    val accountSnapshot = getAccountSnapshot(id) ?: return getModelComponent(id)
    return flowOf(accountSnapshot)
  }

  private fun getAccountSnapshot(id: Long): AccountModel? =
    accountRepository.getAccountsSnapshot().firstOrNull { it.id == id }
}