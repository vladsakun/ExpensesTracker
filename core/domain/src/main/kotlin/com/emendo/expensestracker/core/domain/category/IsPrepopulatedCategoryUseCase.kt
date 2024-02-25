package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.data.api.DefaultTransactionTargetExpenseId
import com.emendo.expensestracker.data.api.DefaultTransactionTargetIncomeId
import javax.inject.Inject

class IsPrepopulatedCategoryUseCase @Inject constructor() {
  operator fun invoke(categoryId: Long): Boolean =
    categoryId == DefaultTransactionTargetExpenseId || categoryId == DefaultTransactionTargetIncomeId
}