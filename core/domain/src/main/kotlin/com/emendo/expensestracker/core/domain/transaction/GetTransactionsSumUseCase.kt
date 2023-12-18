package com.emendo.expensestracker.core.domain.transaction

import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import com.emendo.expensestracker.core.data.model.transaction.TransactionValueWithType
import java.math.BigDecimal
import javax.inject.Inject

class GetTransactionsSumUseCase @Inject constructor() {

  operator fun invoke(transactions: List<TransactionValueWithType>): BigDecimal =
    transactions.sumOf { transaction ->
      if (transaction.type != TransactionType.TRANSFER) {
        transaction.value
      } else {
        BigDecimal.ZERO
      }
    }
}