package com.emendo.expensestracker.core.app.base.manager

import com.emendo.expensestracker.core.app.common.ext.stateInEagerly
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.resourceValueOf
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.model.transaction.TransactionSource
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import com.emendo.expensestracker.core.data.repository.DefaultTransactionTargetExpenseId
import com.emendo.expensestracker.core.data.repository.DefaultTransactionTargetIncomeId
import com.emendo.expensestracker.core.data.repository.DefaultTransactionTargetOrdinalIndex
import com.emendo.expensestracker.core.domain.account.GetLastUsedAccountUseCase
import com.emendo.expensestracker.core.domain.account.RetrieveLastUsedAccountUseCase
import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import com.emendo.expensestracker.core.app.resources.R as AppR

class CreateTransactionRepositoryImpl @Inject constructor(
  getLastUsedAccountUseCase: GetLastUsedAccountUseCase,
  private val retrieveLastUsedAccountUseCase: RetrieveLastUsedAccountUseCase,
  @ApplicationScope private val scope: CoroutineScope,
) : CreateTransactionRepository {
  // Todo need to be smart on switching between transfer and expense transaction type
  private val transactionTargetState: MutableStateFlow<TransactionTarget?> by lazy { MutableStateFlow(null) }

  private val transactionSourceMutableState: MutableStateFlow<TransactionSource?> by lazy { MutableStateFlow(null) }
  private val transactionSourceState: StateFlow<TransactionSource?> =
    merge(transactionSourceMutableState, getLastUsedAccountUseCase())
      .stateInEagerly(
        scope = scope,
        initialValue = null,
      )

  private var isSelectSourceFlow: Boolean = false
  private var isSelectTransferTargetFlow: Boolean = false
  private var payload: CreateTransactionEventPayload? = null

  override suspend fun init() {
    //    transactionSourceMutableState.update {
    //      retrieveLastUsedAccountUseCase()
    //    }
  }

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

  override fun selectAccount(account: AccountModel) {
    if (isSelectSourceFlow()) {
      setSource(account)
      return
    }

    setTarget(account)
  }

  override fun getSource(): Flow<TransactionSource?> {
    return transactionSourceState
  }

  override fun getSourceSnapshot(): TransactionSource? {
    return transactionSourceState.value
  }

  override fun getDefaultTarget(transactionType: TransactionType): TransactionTarget =
    CategoryModel(
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

  override fun isSelectMode(): Boolean =
    isSelectSourceFlow() || isSelectTransferTargetFlow()

  override fun finishSelectMode() {
    finishSelectSourceFlow()
    finishSelectTransferTargetFlow()
  }

  override fun startSelectSourceFlow() {
    isSelectSourceFlow = true
  }

  override fun startSelectTransferTargetFlow() {
    isSelectTransferTargetFlow = true
  }

  override fun getTransactionPayload(): CreateTransactionEventPayload? =
    payload

  override fun setTransactionPayload(newPayload: CreateTransactionEventPayload) {
    payload = newPayload
  }

  private fun isSelectSourceFlow(): Boolean =
    isSelectSourceFlow

  private fun finishSelectSourceFlow() {
    isSelectSourceFlow = false
  }

  private fun isSelectTransferTargetFlow(): Boolean {
    return isSelectTransferTargetFlow
  }

  private fun finishSelectTransferTargetFlow() {
    isSelectTransferTargetFlow = false
  }

  override fun clear(shouldClearTarget: Boolean) {
    if (shouldClearTarget) {
      transactionTargetState.update { null }
    }
    finishSelectSourceFlow()
    finishSelectTransferTargetFlow()
    payload = null
  }
}