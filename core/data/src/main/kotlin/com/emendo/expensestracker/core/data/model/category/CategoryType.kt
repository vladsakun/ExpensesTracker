package com.emendo.expensestracker.core.data.model.category

import androidx.annotation.StringRes
import com.emendo.expensestracker.core.app.resources.R

enum class CategoryType(val id: Int) {
  EXPENSE(0),
  INCOME(1);

  companion object {
    fun getById(id: Int) = entries.first { it.id == id }

    val CategoryType.label: Int
      @StringRes get() = when (this) {
        CategoryType.EXPENSE -> R.string.expense
        CategoryType.INCOME -> R.string.income
      }
  }
}