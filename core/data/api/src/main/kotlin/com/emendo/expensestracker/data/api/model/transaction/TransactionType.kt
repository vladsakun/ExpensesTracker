package com.emendo.expensestracker.data.api.model.transaction

import androidx.annotation.StringRes
import com.emendo.expensestracker.app.resources.R
import javax.annotation.concurrent.Immutable

@Immutable
enum class TransactionType {
  INCOME,
  EXPENSE,
  TRANSFER;

  companion object {
    val DEFAULT = EXPENSE

    fun Int.toTransactionType(): TransactionType = entries[this]
    val TransactionType.id
      get() = this.ordinal

    val TransactionType.labelResId: Int
      @StringRes get() = when (this) {
        INCOME -> R.string.income
        EXPENSE -> R.string.expense
        TRANSFER -> R.string.transfer
      }
  }
}