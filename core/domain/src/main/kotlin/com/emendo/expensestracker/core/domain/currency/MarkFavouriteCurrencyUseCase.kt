package com.emendo.expensestracker.core.domain.currency

import com.emendo.expensestracker.core.data.repository.api.UserDataRepository
import com.emendo.expensestracker.core.model.data.CurrencyModel
import javax.inject.Inject

class MarkFavouriteCurrencyUseCase @Inject constructor(
  private val userDataRepository: UserDataRepository,
) {
  suspend operator fun invoke(currencyModel: CurrencyModel) {
    userDataRepository.addFavouriteCurrency(currencyModel.currencyCode)
  }
}