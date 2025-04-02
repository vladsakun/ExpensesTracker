package com.emendo.expensestracker.core.domain.transaction.controller

import com.emendo.expensestracker.core.app.common.ext.stateInEagerly
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.domain.account.GetLastUsedAccountUseCase
import com.emendo.expensestracker.core.domain.api.CreateTransactionController
import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.data.api.DefaultTransactionTargetExpenseId
import com.emendo.expensestracker.data.api.DefaultTransactionTargetIncomeId
import com.emendo.expensestracker.data.api.DefaultTransactionTargetOrdinalIndex
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.resourceValueOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import com.emendo.expensestracker.app.resources.R as AppR

/**
 * Controller responsible for managing the creation of transactions.
 * This class handles the state and logic required to create, update, and clear transactions.
 * It provides methods to set and get transaction sources and targets, as well as to manage transaction payloads.
 */
class CreateTransactionControllerImpl @Inject constructor(
  getLastUsedAccountUseCase: GetLastUsedAccountUseCase,
  @ApplicationScope private val scope: CoroutineScope,
) : CreateTransactionController {

  private val transactionTargetState: MutableStateFlow<TransactionTarget?> by lazy { MutableStateFlow(null) }

  private val transactionSourceMutableState: MutableStateFlow<TransactionSource?> by lazy { MutableStateFlow(null) }
  private val transactionSourceState: StateFlow<TransactionSource?> =
    merge(transactionSourceMutableState, getLastUsedAccountUseCase())
      .stateInEagerly(scope = scope, initialValue = null)

  private var payload: CreateTransactionEventPayload? = null

  override fun getTarget(): Flow<TransactionTarget?> {
    return transactionTargetState
  }

  override fun getTargetSnapshot(): TransactionTarget? {
    return transactionTargetState.value
  }

  override fun setTarget(target: TransactionTarget?) {
    transactionTargetState.update { target }
  }

  override fun setSource(source: TransactionSource?) {
    transactionSourceMutableState.update { source }
  }

  override fun getSource(): Flow<TransactionSource?> {
    return transactionSourceState
  }

  override fun getSourceSnapshot(): TransactionSource? {
    return transactionSourceState.value
  }

  override fun getDefaultNonTransferTarget(transactionType: TransactionType): TransactionTarget? {
    if (transactionType == TransactionType.TRANSFER) {
      return null
    }

    return CategoryModel(
      id = if (transactionType == TransactionType.EXPENSE) {
        DefaultTransactionTargetExpenseId
      } else {
        DefaultTransactionTargetIncomeId
      },
      name = resourceValueOf(AppR.string.uncategorized),
      icon = IconModel.UNKNOWN,
      color = ColorModel.Base,
      type = if (transactionType == TransactionType.EXPENSE) {
        CategoryType.EXPENSE
      } else {
        CategoryType.INCOME
      },
      ordinalIndex = DefaultTransactionTargetOrdinalIndex,
    )
  }

  override fun getTransactionPayload(): CreateTransactionEventPayload? = payload

  override fun setTransactionPayload(newPayload: CreateTransactionEventPayload) {
    payload = newPayload
  }

  override fun clear() {
    transactionTargetState.update { null }
    payload = null
  }
}