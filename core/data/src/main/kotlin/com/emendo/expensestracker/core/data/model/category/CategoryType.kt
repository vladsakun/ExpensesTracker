package com.emendo.expensestracker.core.data.model.category

import androidx.annotation.StringRes
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.data.model.transaction.TransactionType

enum class CategoryType(val id: Int) {
  EXPENSE(0),
  INCOME(1);

  companion object {
    fun getById(id: Int) = entries.first { it.id == id }

    val CategoryType.label: Int
      @StringRes get() = when (this) {
        EXPENSE -> R.string.expense
        INCOME -> R.string.income
      }

    fun CategoryType.toPageIndex(): Int =
      when (this) {
        EXPENSE -> 0
        INCOME -> 1
      }

    fun CategoryType.toTransactionType(): TransactionType =
      when (this) {
        EXPENSE -> TransactionType.EXPENSE
        INCOME -> TransactionType.INCOME
      }

    fun Int.toCategoryType(): CategoryType =
      when (this) {
        0 -> EXPENSE
        1 -> INCOME
        else -> throw IllegalArgumentException("Category type with index $this is not supported")
      }
  }
}