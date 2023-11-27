package com.emendo.expensestracker.core.data.model.transaction

enum class TransactionType {
  INCOME,
  EXPENSE,
  TRANSFER;

  companion object {
    val DEFAULT = EXPENSE

    fun Int.toTransactionType(): TransactionType = entries[this]
  }
}