package com.emendo.expensestracker.createtransaction.transaction.domain

import com.emendo.expensestracker.core.domain.account.GetLastTransferAccountOrFirstUseCase
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.createtransaction.transaction.CreateTransactionUiState
import java.math.BigDecimal
import javax.inject.Inject

class TransactionFacade @Inject constructor(
  private val getLastTransferAccountOrFirstUseCase: GetLastTransferAccountOrFirstUseCase,
  private val getDefaultAmountUseCase: GetDefaultAmountUseCase,
  private val getConvertedFormattedValueUseCase: GetConvertedFormattedValueUseCase,
  private val getTransferReceivedAmountUseCase: GetTransferReceivedAmountUseCase,
) {

  suspend fun getLastTransferAccountOrFirst(sourceAccountId: Long?) =
    getLastTransferAccountOrFirstUseCase(sourceAccountId)

  fun getDefaultAmount(currencyModel: CurrencyModel?) =
    getDefaultAmountUseCase(currencyModel)

  fun getConvertedFormattedValue(value: BigDecimal, fromCurrency: CurrencyModel, toCurrency: CurrencyModel) =
    getConvertedFormattedValueUseCase(value, fromCurrency, toCurrency)

  fun getTransferReceivedAmount(state: CreateTransactionUiState, sourceAmount: BigDecimal) =
    getTransferReceivedAmountUseCase(state, sourceAmount)
}