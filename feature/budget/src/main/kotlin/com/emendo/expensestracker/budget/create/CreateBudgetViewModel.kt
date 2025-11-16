package com.emendo.expensestracker.budget.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.app.base.api.helper.NumericKeyboardCommander
import com.emendo.expensestracker.app.base.api.screens.SelectCurrencyScreenApi
import com.emendo.expensestracker.app.base.api.screens.SelectIconScreenApi
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.BudgetPeriod.MONTHLY
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.create.transaction.api.SelectCategoryScreenApi
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.amount.CalculatorFormatter
import com.emendo.expensestracker.data.api.repository.BudgetRepository
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import com.emendo.expensestracker.data.api.repository.UserDataRepository
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.NetworkViewState
import com.emendo.expensestracker.model.ui.successData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateBudgetViewModel @Inject constructor(
  override val numericKeyboardCommander: NumericKeyboardCommander,
  override val calculatorFormatter: CalculatorFormatter,
  override val amountFormatter: AmountFormatter,
  private val budgetRepository: BudgetRepository,
  private val userDataRepository: UserDataRepository,
  val selectIconScreenApi: SelectIconScreenApi,
  val selectCategoryScreenApi: SelectCategoryScreenApi,
  val categoryRepository: CategoryRepository,
  val selectCurrencyScreenApi: SelectCurrencyScreenApi,
) : ViewModel(),
    BudgetStateManager<CreateBudgetScreenData> by BudgetStateManagerDelegate(
      defaultState = NetworkViewState.Success(
        CreateBudgetScreenData.getDefault(
          color = ColorModel.random,
          iconId = null,
          name = null,
          limit = null,
          categoryId = null,
          currency = userDataRepository.getUserDataSnapshot()?.generalCurrencyCode?.let {
            CurrencyModel.toCurrencyModel(
              it
            )
          } ?: CurrencyModel.USD,
        )
      )
    ),
    ModalBottomSheetStateManager by BudgetBottomSheetDelegate(numericKeyboardCommander),
    CreateBudgetCommander,
    CreateBudgetBottomSheetContract {

  override val budgetStateManager: BudgetStateManager<*>
    get() = this
  override val modalBottomSheetStateManager: ModalBottomSheetStateManager
    get() = this

  fun createBudget() {
    val data = requireDataValue()
    viewModelScope.launch {
      budgetRepository.createBudget(
        name = data.name,
        iconId = data.icon.id,
        colorId = data.color.id,
        amount = data.limit.value,
        period = MONTHLY,
        categoryId = data.category!!.id,
        currencyCode = data.currency.currencyCode,
      )
      navigateUp()
    }
  }

  fun showLimitBottomSheet() {
    showLimitBottomSheet(
      userDataRepository.getUserDataSnapshot()?.generalCurrencyCode?.let { CurrencyModel.toCurrencyModel(it) }
        ?: CurrencyModel.USD,
    )
  }

  override fun changeName(newName: String) = updateName(newName)
  override fun changeIcon(iconId: Int) = updateIcon(iconId)
  override fun changeColor(colorId: Int) = updateColor(colorId)
  override fun changeCategory(category: Long) = updateCategory(categoryRepository.getCategorySnapshotById(category)!!)
  override fun changeCurrency(currencyCode: String) {
    updateCurrency(amountFormatter, CurrencyModel.toCurrencyModel(currencyCode))
  }

  override fun processCommand(command: CreateBudgetCommand) {
    command.execute(this)
  }

  fun getSelectIconScreenRoute(): String {
    val iconId = state.value.successData?.icon?.id ?: IconModel.random.id
    return selectIconScreenApi.getSelectIconScreenRoute(iconId)
  }

  fun getSelectCategoryScreenRoute(): String = selectCategoryScreenApi.getRoute()

  fun getSelectCurrencyScreenRoute(): String =
    selectCurrencyScreenApi.getSelectCurrencyScreenRoute()
}
