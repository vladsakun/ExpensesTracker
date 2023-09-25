package com.emendo.expensestracker.core.data.model

enum class TransactionType {
  INCOME,
  EXPENSE,
  TRANSFER;

  // Todo check with Pavel SOLID
  companion object {
    fun getByTarget(target: Target): TransactionType {
      return when (target) {
        is Target.Account -> TRANSFER
        is Target.Category -> {
          when (target.type) {
            CategoryType.INCOME -> INCOME
            CategoryType.EXPENSE -> EXPENSE
          }
        }
      }
    }
  }
}