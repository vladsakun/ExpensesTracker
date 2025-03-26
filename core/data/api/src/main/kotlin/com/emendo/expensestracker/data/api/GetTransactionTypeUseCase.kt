package com.emendo.expensestracker.data.api

import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.data.api.extensions.isAccount
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import javax.inject.Inject

// TODO extract to domain api
// https://medium.com/codex/from-junior-to-senior-the-real-way-to-implement-clean-architecture-in-android-8514005e85e1
class GetTransactionTypeUseCase @Inject constructor() {

  operator fun invoke(source: TransactionSource, target: TransactionTarget): TransactionType {
    if (source.isAccount() && target.isAccount()) {
      return TransactionType.TRANSFER
    }

    if (source is AccountModel && target is CategoryModel) {
      return when (target.type) {
        CategoryType.EXPENSE -> TransactionType.EXPENSE
        CategoryType.INCOME -> TransactionType.INCOME
      }
    }

    throw IllegalArgumentException("Unsupported transaction type with source: $source, target: $target")
  }
}