package com.emendo.expensestracker.core.model.data

enum class TransactionType {
  INCOME,
  EXPENSE,
  TRANSFER;

  companion object {
    val DEFAULT = EXPENSE

    fun toTransactionType(id: Int): TransactionType = entries[id]
    val TransactionType.id: Int
      get() = this.ordinal
  }
}