package com.emendo.expensestracker.core.domain.transaction

import com.emendo.expensestracker.core.domain.currency.ConvertCurrencyUseCase
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import com.emendo.expensestracker.data.api.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Instant
import javax.inject.Inject

class GetTotalInPeriodUseCase @Inject constructor(
  private val transactionRepository: TransactionRepository,
  private val convertCurrencyUseCase: ConvertCurrencyUseCase,
  private val amountFormatter: AmountFormatter,
  private val userDataRepository: UserDataRepository,
) {

  operator fun invoke(transactionType: TransactionType, startDate: Instant, endDate: Instant): Flow<Amount> {
    val transactionsFlow = transactionRepository.getTransactionsByTypeInPeriod(transactionType, startDate, endDate)
    val generalCurrencyFlow = userDataRepository.generalCurrency

    return combine(generalCurrencyFlow, transactionsFlow) { generalCurrency, transactions ->
      val generalCurrencyCode = generalCurrency.currencyCode
      val totalValue = transactions
        .sumOf { transaction ->
          convertCurrencyUseCase(
            value = transaction.value,
            fromCurrencyCode = transaction.currency.currencyCode,
            toCurrencyCode = generalCurrencyCode,
            conversionDate = transaction.date,
            usdToOriginalRate = transaction.usdToOriginalRate,
          )
        }
        .abs()

      amountFormatter.format(totalValue, generalCurrency)
    }
  }
}