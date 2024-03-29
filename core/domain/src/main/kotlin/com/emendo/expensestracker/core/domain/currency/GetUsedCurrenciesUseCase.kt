package com.emendo.expensestracker.core.domain.currency

import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.repository.AccountRepository
import com.emendo.expensestracker.data.api.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUsedCurrenciesUseCase @Inject constructor(
  private val accountRepository: AccountRepository,
  private val userDataRepository: UserDataRepository,
) {

  operator fun invoke(): Flow<List<CurrencyModel>> {
    val accountCurrenciesFlow = accountRepository.getAccounts().map { accounts ->
      accounts.map { it.currency }
    }
    return combine(
      userDataRepository.generalCurrency,
      userDataRepository.favouriteCurrencies,
      accountCurrenciesFlow
    ) { generalCurrency, favouriteCurrencies, accountCurrencies ->
      (accountCurrencies + favouriteCurrencies + generalCurrency).distinct()
    }
  }
}