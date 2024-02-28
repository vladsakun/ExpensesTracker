package com.emendo.expensestracker.core.domain.account

import com.emendo.expensestracker.core.domain.common.GetModelComponent
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountByIdUseCase @Inject constructor(
  private val accountRepository: AccountRepository,
) : GetModelComponent<AccountModel> {

  override operator fun invoke(id: Long): Flow<AccountModel> =
    accountRepository.getById(id)
}