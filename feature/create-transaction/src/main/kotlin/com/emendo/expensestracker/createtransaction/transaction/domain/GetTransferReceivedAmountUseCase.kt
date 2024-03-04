package com.emendo.expensestracker.createtransaction.transaction.domain

import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.createtransaction.transaction.CreateTransactionUiState
import java.math.BigDecimal
import javax.inject.Inject

class GetTransferReceivedAmountUseCase @Inject constructor(
  private val getConvertedTransferAmountUseCase: GetConvertedTransferAmountUseCase,
) {

  /**
   * Converts [sourceAmount] if the user didn't put any custom value into Transfer target amount
   *
   * @param state - screen ui state
   * @param sourceAmount - source amount [Amount]
   * @return if user put any custom value into Transfer target amount - Transfer target amount, otherwise - converted [sourceAmount]
   */
  operator fun invoke(
    state: CreateTransactionUiState,
    sourceAmount: BigDecimal,
  ): Amount? {
    if (state.isCustomTransferAmount) {
      return state.transferReceivedAmount
    }

    return getConvertedTransferAmountUseCase(sourceAmount)
  }
}