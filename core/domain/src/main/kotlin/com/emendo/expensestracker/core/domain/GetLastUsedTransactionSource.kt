package com.emendo.expensestracker.core.domain

import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.domain.model.CalculatorTransactionUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLastUsedTransactionSource @Inject constructor(
  private val getLastUsedAccountUseCase: GetLastUsedAccountUseCase,
) {
  fun invoke(): Flow<CalculatorTransactionUiModel?> =
    getLastUsedAccountUseCase().map { it?.asTransactionUiModel() }
}

fun CategoryModel.asTransactionUiModel(): CalculatorTransactionUiModel {
  return CalculatorTransactionUiModel(
    name = name,
    icon = icon.imageVector,
  )
}

fun AccountModel.asTransactionUiModel(): CalculatorTransactionUiModel {
  return CalculatorTransactionUiModel(
    name = name,
    icon = icon.imageVector,
    currency = currency.currencyName,
  )
}
