package com.emendo.expensestracker.core.domain

import com.emendo.expensestracker.core.app.resources.models.CalculatorTransactionUiModel
import com.emendo.expensestracker.core.data.model.asTransactionUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLastUsedTransactionSource @Inject constructor(
  private val getLastUsedAccountUseCase: GetLastUsedAccountUseCase,
) {
  fun invoke(): Flow<CalculatorTransactionUiModel?> =
    getLastUsedAccountUseCase().map { it?.asTransactionUiModel() }
}
