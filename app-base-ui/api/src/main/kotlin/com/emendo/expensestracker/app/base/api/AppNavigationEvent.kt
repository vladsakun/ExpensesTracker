package com.emendo.expensestracker.app.base.api

import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget

sealed interface AppNavigationEvent {
  data class CreateTransaction(
    val source: TransactionSource?,
    val target: TransactionTarget?,
    val payload: CreateTransactionEventPayload? = null,
    val shouldNavigateUp: Boolean = false,
  ) : AppNavigationEvent

  data class CreateCategory(val categoryType: CategoryType) : AppNavigationEvent
  data class SelectAccount(val isTransferTargetSelect: Boolean = false) : AppNavigationEvent
  data object SelectCurrency : AppNavigationEvent
  data class SelectColor(val preselectedColorId: Int) : AppNavigationEvent
  data class SelectIcon(val preselectedIconId: Int) : AppNavigationEvent
}