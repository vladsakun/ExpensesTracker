package com.emendo.expensestracker.data.api.extensions

import androidx.annotation.StringRes
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.model.data.TransactionType

val TransactionType.labelResId: Int
  @StringRes get() = when (this) {
    TransactionType.INCOME -> R.string.income
    TransactionType.EXPENSE -> R.string.expense
    TransactionType.TRANSFER -> R.string.transfer
  }