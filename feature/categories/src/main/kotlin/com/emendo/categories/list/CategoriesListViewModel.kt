package com.emendo.categories.list

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.app.resources.models.CalculatorTransactionUiModel
import com.emendo.expensestracker.core.data.CalculatorInput
import com.emendo.expensestracker.core.data.CalculatorInputCallbacks
import com.emendo.expensestracker.core.data.DEFAULT_CALCULATOR_NUM_1
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.CategoryModel
import com.emendo.expensestracker.core.data.model.CategoryWithTransactions
import com.emendo.expensestracker.core.data.model.asTransactionUiModel
import com.emendo.expensestracker.core.data.repository.AccountsRepository
import com.emendo.expensestracker.core.data.repository.CategoryRepository
import com.emendo.expensestracker.core.data.repository.TransactionRepository
import com.emendo.expensestracker.core.model.data.*
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesListViewModel @Inject constructor(
  categoryRepository: CategoryRepository,
  accountsRepository: AccountsRepository,
  private val amountFormatter: AmountFormatter,
  private val transactionRepository: TransactionRepository,
  private val calculatorInput: CalculatorInput,
) : BaseBottomSheetViewModel<BottomSheetType>(),
    // Todo extract to commons
    CalculatorKeyboardActions,
    CalculatorInputCallbacks {

  val uiState: StateFlow<CategoriesListUiState> = categoriesUiState(categoryRepository)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = CategoriesListUiState.Empty
    )

  private val accountDialogUiState = accountsDialogUiState(accountsRepository, ::onAccountSelected)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = BaseDialogListUiState.Loading
    )

  private val categoryDialogUiState = categoriesDialogUiState(uiState, ::onCategorySelected)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = BaseDialogListUiState.Loading
    )

  private val _alertDialogState = MutableStateFlow<BaseDialogListUiState<CategoriesListDialogData>?>(null)
  val alertDialogState = _alertDialogState.asStateFlow()

  // Todo extract to commons
  private val calculatorTextState = MutableStateFlow(DEFAULT_CALCULATOR_NUM_1)
  private val currencyState = MutableStateFlow("$")
  private val equalButtonState = MutableStateFlow(EqualButtonState.Default)
  private val sourceUiModel = MutableStateFlow<CalculatorTransactionUiModel?>(null)
  private val targetUiModel = MutableStateFlow<CalculatorTransactionUiModel?>(null)

  private val source = merge(
    sourceUiModel,
    accountsRepository.getLastUsedTransactionSourceUiModel()
  ).distinctUntilChanged()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = null
    )

  // Todo extract to commons
  init {
    calculatorInput.initCallbacks(this)
  }

  override fun doOnValueChange(formattedValue: String, equalButtonState: EqualButtonState) {
    calculatorTextState.update { formattedValue }
    this.equalButtonState.update { equalButtonState }
  }

  override fun onChangeSourceClick() {
    _alertDialogState.update { accountDialogUiState.value }
  }

  override fun onChangeTargetClick() {
    _alertDialogState.update { categoryDialogUiState.value }
  }

  override fun onCurrencyClick() {
    TODO("Not yet implemented")
  }

  override fun onClearClick() {
    calculatorInput.onClearClick()
  }

  override fun onMathOperationClick(mathOperation: MathOperation) {
    calculatorInput.input(mathOperation)
  }

  override fun onNumberClick(numKeyboardNumber: NumKeyboardNumber) {
    calculatorInput.input(numKeyboardNumber)
  }

  override fun onPrecisionClick() {
    calculatorInput.onPrecisionClick()
  }

  override fun onDoneClick() {
    viewModelScope.launch {
      val source = source.value?.element as? TransactionSource ?: return@launch
      val target = targetUiModel.value?.element as? TransactionTarget ?: return@launch

      transactionRepository.createTransaction(
        source = source,
        target = target,
        amount = calculatorInput.currentValue,
      )

      calculatorInput.clear()
      hideBottomSheet()
    }
  }

  override fun onEqualClick() {
    calculatorInput.doMath()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  override fun confirmValueChange(sheetValue: SheetValue): Boolean {
    if (calculatorInput.isNotEmpty()) {
      viewModelScope.launch {
        // Todo show alert are you sure you want to close
        hideBottomSheet()
      }
      return false
    }

    return true
  }

  fun onCategoryClick(category: CategoryWithTransactions) {
    targetUiModel.update { category.categoryModel.asTransactionUiModel() }

    showBottomSheet(
      BottomSheetType.Calculator(
        text = calculatorTextState,
        actions = this,
        equalButtonState = equalButtonState,
        decimalSeparator = amountFormatter.decimalSeparator.toString(),
        currencyState = currencyState,
        source = source,
        target = targetUiModel,
      )
    )
  }

  fun onAlertDialogDismissRequest() {
    closeAlertDialog()
  }

  fun onCloseClick() {
    closeAlertDialog()
  }

  fun onConfirmClick() {
    closeAlertDialog()
  }

  private fun onAccountSelected(accountModel: AccountModel) {
    hideAlertDialog()
    sourceUiModel.update { accountModel.asTransactionUiModel() }
  }

  private fun onCategorySelected(categoryModel: CategoryModel) {
    hideAlertDialog()
    targetUiModel.update { categoryModel.asTransactionUiModel() }
  }

  private fun hideAlertDialog() {
    closeAlertDialog()
  }

  private fun closeAlertDialog() {
    _alertDialogState.update { null }
  }
}

private fun categoriesUiState(categoryRepository: CategoryRepository): Flow<CategoriesListUiState> {
  return categoryRepository.getCategoriesWithTransactions().asResult().map { categoriesResult ->
    when (categoriesResult) {
      is Result.Success -> CategoriesListUiState.DisplayCategoriesList(categoriesResult.data.toImmutableList())
      is Result.Error -> CategoriesListUiState.Error("No categories found")
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

      is Result.Error -> BaseDialogListUiState.Error("No accounts found")
      is Result.Loading -> BaseDialogListUiState.Loading
      is Result.Empty -> BaseDialogListUiState.Empty
    }
  }
}

private fun categoriesDialogUiState(
  categoriesState: StateFlow<CategoriesListUiState>,
  onCategorySelected: (CategoryModel) -> Unit,
): Flow<BaseDialogListUiState<CategoriesListDialogData.Categories>> {
  return categoriesState.map { state ->
    when (state) {
      is CategoriesListUiState.DisplayCategoriesList -> BaseDialogListUiState.DisplayList(
        CategoriesListDialogData.Categories(
          categories = state.categories.map { it.categoryModel }.toImmutableList(),
          onSelectCategory = onCategorySelected,
        )
      )

      else -> BaseDialogListUiState.Error("No categories found")
    }
  }
}