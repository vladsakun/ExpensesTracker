package com.emendo.expensestracker.data.api.model.transaction

import androidx.annotation.StringRes
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.model.data.TransactionType

fun Int.toTransactionType(): TransactionType = TransactionType.entries[this]
val TransactionType.id
  get() = this.ordinal

val TransactionType.labelResId: Int
  @StringRes get() = when (this) {
    TransactionType.INCOME -> R.string.income
    TransactionType.EXPENSE -> R.string.expense
    TransactionType.TRANSFER -> R.string.transfer
  }