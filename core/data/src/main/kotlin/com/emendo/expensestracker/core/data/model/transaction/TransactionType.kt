package com.emendo.expensestracker.core.data.model.transaction

import androidx.annotation.StringRes
import com.emendo.expensestracker.core.app.resources.R

enum class TransactionType {
  INCOME,
  EXPENSE,
  TRANSFER;

  companion object {
    val DEFAULT = EXPENSE

    fun Int.toTransactionType(): TransactionType = entries[this]

    val TransactionType.labelResId: Int
      @StringRes get() = when (this) {
        INCOME -> R.string.income
        EXPENSE -> R.string.expense
        TRANSFER -> R.string.transfer
      }
  }
}