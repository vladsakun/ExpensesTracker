package com.emendo.expensestracker.core.database.model

enum class TransactionType {
  INCOME,
  EXPENSE,
  TRANSFER;

  companion object {
    inline val TransactionType.id
      get() = ordinal

    fun getById(id: Int) = entries[id]
  }
}