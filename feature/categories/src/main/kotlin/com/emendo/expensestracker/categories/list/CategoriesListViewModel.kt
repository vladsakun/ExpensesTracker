package com.emendo.expensestracker.categories.list

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.data.di.DecimalSeparator
import com.emendo.expensestracker.core.data.helper.CalculatorCommander
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.asTransactionUiModel
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.model.category.CategoryWithTotalTransactions
import com.emendo.expensestracker.core.data.model.category.asTransactionUiModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionSource
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.repository.api.AccountsRepository
import com.emendo.expensestracker.core.data.repository.api.TransactionsRepository
import com.emendo.expensestracker.core.domain.GetCategoriesWithTotalTransactionsUseCase
import com.emendo.expensestracker.core.domain.GetLastUsedAccountUseCase
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import com.emendo.expensestracker.core.ui.bottomsheet.calculator.CalculatorBottomSheetState
import com.emendo.expensestracker.core.ui.bottomsheet.calculator.CalculatorKeyboardActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

val DEFAULT_PAGE_INDEX = CategoryType.EXPENSE.toPageIndex()

@HiltViewModel
class CategoriesListViewModel @Inject constructor(
  getCategoriesWithTotalTransactionsUseCase: GetCategoriesWithTotalTransactionsUseCase,
  accountsRepository: AccountsRepository,
  getLastUsedAccountUseCase: GetLastUsedAccountUseCase,
  @DecimalSeparator private val decimalSeparator: String,
  private val transactionsRepository: TransactionsRepository,
  private val calculatorCommander: CalculatorCommander,
) : BaseBottomSheetViewModel<BottomSheetType>(), CalculatorKeyboardActions {

  val categoriesListUiState: StateFlow<CategoriesListUiState> =
    categoriesUiState(getCategoriesWithTotalTransactionsUseCase)
      .stateIn(
        scope = vmScope,
        started = SharingStarted.Eagerly,
        initialValue = CategoriesListUiState.Empty
      )

  private val accountDialogUiState = accountsDialogUiState(accountsRepository, ::onAccountSelected)
    .stateIn(
      scope = vmScope,
      started = SharingStarted.Eagerly,
      initialValue = BaseDialogListUiState.Loading
    )

  private val _alertDialogState = MutableStateFlow<BaseDialogListUiState<CategoriesListDialogData>?>(null)
  val alertDialogState = _alertDialogState.asStateFlow()

  private val sourceUiModel = MutableStateFlow<AccountModel?>(null)
  private val source = merge(sourceUiModel, getLastUsedAccountUseCase())
    .distinctUntilChanged()
    .stateIn(
      scope = vmScope,
      started = SharingStarted.Eagerly,
      initialValue = null
    )
  private val targetUiModel = MutableStateFlow<CategoryModel?>(null)

  private val currencyUiState: MutableStateFlow<CurrencyModel?> = MutableStateFlow(null)
  private val currencyState: StateFlow<CurrencyModel?> = merge(currencyUiState, source.map { it?.currency })
    .distinctUntilChanged()
    .stateIn(
      scope = vmScope,
      started = SharingStarted.Eagerly,
      initialValue = null,
    )

  private var selectedPageIndex = CategoryType.EXPENSE.toPageIndex()
  private var createTransactionJob: Job? = null

  private val calculatorState: StateFlow<CalculatorBottomSheetState> = combine(
    calculatorCommander.calculatorTextState,
    calculatorCommander.equalButtonState,
    source,
    targetUiModel,
    currencyState,
  ) { text, equalButtonState, source, target, currency ->
    CalculatorBottomSheetState(
      text = text,
      currency = currency?.currencySymbolOrCode,
      equalButtonState = equalButtonState,
      source = source?.asTransactionUiModel(),
      target = target?.asTransactionUiModel(),
    )
  }.stateIn(
    scope = vmScope,
    started = SharingStarted.WhileSubscribed(5_000L),
    initialValue = CalculatorBottomSheetState.initial(),
  )

  init {
    calculatorCommander.setCallbacks(doneClick = ::createTransaction)
  }

  override fun onChangeSourceClick() {
    _alertDialogState.update { accountDialogUiState.value }
  }

  override fun changeTarget() {
    val categoriesListState =
      checkNotNull(categoriesListUiState.value as? CategoriesListUiState.DisplayCategoriesList) {
        "User must not be able to change target, when the categories list state != DisplayCategoriesList"
      }
    val categories = checkNotNull(categoriesListState.categories[selectedPageIndex]) {
      "Categories of selected page can't be null"
    }

    _alertDialogState.update {
      BaseDialogListUiState.DisplayList(
        CategoriesListDialogData.Categories(
          categories = categories.map { it.categoryModel }.toImmutableList(),
          onSelectCategory = ::onCategorySelected,
        )
      )
    }
  }

  override fun onCurrencyClick() {
    TODO("Not yet implemented")
  }

  @OptIn(ExperimentalMaterial3Api::class)
  override fun confirmValueChange(sheetValue: SheetValue): Boolean {
    if (calculatorCommander.isNotEmpty()) {
      vmScope.launch {
        // Todo show alert are you sure you want to close
        hideBottomSheet()
      }
      return false
    }

    return true
  }

  fun showCalculatorBottomSheet(category: CategoryWithTotalTransactions) {
    targetUiModel.update { category.categoryModel }

    showBottomSheet(
      BottomSheetType.Calculator(
        state = calculatorState,
        actions = this,
        numericKeyboardActions = calculatorCommander,
        decimalSeparator = decimalSeparator,
      )
    )
  }

  fun handleDialogDismissRequest() {
    closeAlertDialog()
  }

  fun closeDialog() {
    closeAlertDialog()
  }

  fun confirmDialog() {
    closeAlertDialog()
  }

  fun pageSelected(pageIndex: Int) {
    selectedPageIndex = pageIndex
  }

  private fun createTransaction() {
    if (createTransactionJob != null) {
      return
    }

    createTransactionJob = vmScope.launch {
      val source = checkNotNull(source.value as? TransactionSource) { "Source shouldn't be null " }
      val target = checkNotNull(targetUiModel.value as? TransactionTarget) { "Target shouldn't be null" }

      transactionsRepository.createTransaction(
        source = source,
        target = target,
        amount = calculatorCommander.currencyValue,
      )

      calculatorCommander.clear()
      hideBottomSheet()
    }.apply {
      invokeOnCompletion {
        createTransactionJob = null
      }
    }
  }

  private fun onAccountSelected(accountModel: AccountModel) {
    hideAlertDialog()
    sourceUiModel.update { accountModel }
  }

  private fun onCategorySelected(categoryModel: CategoryModel) {
    hideAlertDialog()
    targetUiModel.update { categoryModel }
  }

  private fun hideAlertDialog() {
    closeAlertDialog()
  }

  private fun closeAlertDialog() {
    _alertDialogState.update { null }
  }
}

private fun CategoryType.toPageIndex(): Int =
  when (this) {
    CategoryType.EXPENSE -> 0
    CategoryType.INCOME -> 1
  }

private fun categoriesUiState(
  getCategoriesWithTotalTransactionsUseCase: GetCategoriesWithTotalTransactionsUseCase,
): Flow<CategoriesListUiState> {
  return getCategoriesWithTotalTransactionsUseCase().asResult().map { categoriesResult ->
    when (categoriesResult) {
      is Result.Success -> CategoriesListUiState.DisplayCategoriesList(
        categories = persistentMapOf(
          createCategoryPagePair(CategoryType.EXPENSE, categoriesResult.data),
          createCategoryPagePair(CategoryType.INCOME, categoriesResult.data),
        )
      )

      is Result.Error -> CategoriesListUiState.Error("No categories found ${categoriesResult.exception}")
      is Result.Loading -> CategoriesListUiState.Loading
      is Result.Empty -> CategoriesListUiState.Empty
    }
  }
}

private fun accountsDialogUiState(
  accountsRepository: AccountsRepository,
  onAccountSelected: (AccountModel) -> Unit,
): Flow<BaseDialogListUiState<CategoriesListDialogData.Accounts>> {
  return accountsRepository.getAccounts().asResult().map { accountsResult ->
    when (accountsResult) {
      is Result.Success -> BaseDialogListUiState.DisplayList(
        CategoriesListDialogData.Accounts(
          accountModels = accountsResult.data.toImmutableList(),
          onSelectAccount = onAccountSelected,
        )
      )

      // Todo remove Boilerplate
      is Result.Error -> BaseDialogListUiState.Error("No accounts found")
      is Result.Loading -> BaseDialogListUiState.Loading
      is Result.Empty -> BaseDialogListUiState.Empty
    }
  }
}

private fun createCategoryPagePair(
  categoryType: CategoryType,
  categoriesList: List<CategoryWithTotalTransactions>,
): Pair<Int, ImmutableList<CategoryWithTotalTransactions>> {
  return categoryType.toPageIndex() to categoriesList.filter { it.categoryModel.type == categoryType }.toImmutableList()
}