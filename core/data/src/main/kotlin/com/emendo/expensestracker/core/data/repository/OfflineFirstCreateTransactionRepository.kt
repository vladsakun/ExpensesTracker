package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.ext.stateInEagerly
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.resourceValueOf
import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import com.emendo.expensestracker.data.api.DefaultTransactionTargetExpenseId
import com.emendo.expensestracker.data.api.DefaultTransactionTargetIncomeId
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import com.emendo.expensestracker.data.api.model.transaction.TransactionType
import com.emendo.expensestracker.data.api.repository.AccountRepository
import com.emendo.expensestracker.data.api.repository.CreateTransactionRepository
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import com.emendo.expensestracker.core.app.resources.R as AppR

class OfflineFirstCreateTransactionRepository @Inject constructor(
  private val transactionRepository: TransactionRepository,
  private val accountRepository: AccountRepository,
  @ApplicationScope private val scope: CoroutineScope,
) : CreateTransactionRepository {
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

  // Todo rethink copy-paste from GetLastUsedAccountUseCase
  private fun getLastUsedAccountUseCase(): Flow<TransactionSource?> {
    return transactionRepository.getLastTransactionFull().flatMapLatest { transaction ->
      val sourceId = transaction?.source?.id ?: return@flatMapLatest accountRepository.getLastAccount()
      accountRepository.getById(sourceId)
    }
  }
}