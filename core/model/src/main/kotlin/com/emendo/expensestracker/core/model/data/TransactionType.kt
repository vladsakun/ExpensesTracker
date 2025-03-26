package com.emendo.expensestracker.core.model.data

enum class TransactionType {
  INCOME,
  EXPENSE,
  TRANSFER;

  companion object {
    val DEFAULT = EXPENSE

    fun Int.toTransactionType(): TransactionType = TransactionType.entries[this]
    val TransactionType.id
      get() = this.ordinal
  }
}