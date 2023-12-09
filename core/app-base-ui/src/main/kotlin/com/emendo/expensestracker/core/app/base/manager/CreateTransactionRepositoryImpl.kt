package com.emendo.expensestracker.core.app.base.manager

import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.resourceValueOf
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
      .stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = null,
      )

  private var isSelectSourceFlow: Boolean = false

  override suspend fun init() {
    transactionSourceMutableState.update {
      retrieveLastUsedAccountUseCase()
    }
  }

  override fun getTarget(): Flow<TransactionTarget?> {
    return transactionTargetState
  }

  override fun getTargetSnapshot(): TransactionTarget? {
    return transactionTargetState.value
  }

  override fun setTarget(target: TransactionTarget) {
    transactionTargetState.update { target }
  }

  override fun setSource(source: TransactionSource) {
    transactionSourceMutableState.update { source }
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

  override fun isSelectSourceFlow(): Boolean {
    return isSelectSourceFlow
  }

  override fun startSelectSourceFlow() {
    isSelectSourceFlow = true
  }

  override fun finishSelectSourceFlow() {
    isSelectSourceFlow = false
  }

  override fun clear() {
    transactionTargetState.update { null }
  }
}