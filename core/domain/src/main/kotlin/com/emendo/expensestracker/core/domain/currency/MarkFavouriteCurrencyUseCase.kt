package com.emendo.expensestracker.core.domain.currency

import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.repository.UserDataRepository
import javax.inject.Inject

class MarkFavouriteCurrencyUseCase @Inject constructor(
  private val userDataRepository: UserDataRepository,
) {
  suspend operator fun invoke(currencyModel: CurrencyModel) {
    userDataRepository.addFavouriteCurrency(currencyModel.currencyCode)
  }
}