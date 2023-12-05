package com.emendo.expensestracker

import androidx.lifecycle.ViewModel
import com.emendo.expensestracker.accounts.destinations.AccountsScreenRouteDestination
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEvent.*
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.base.manager.CreateTransactionRepository
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectColorScreenDestination
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectCurrencyScreenDestination
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectIconScreenDestination
import com.emendo.expensestracker.createtransaction.destinations.CreateTransactionScreenDestination
import com.ramcosta.composedestinations.spec.Direction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  appNavigationEventBus: AppNavigationEventBus,
  private val createTransactionRepository: CreateTransactionRepository,
) : ViewModel() {

  // Todo rethink huge when
  val navigationEvent: Flow<Direction> = appNavigationEventBus.eventFlow.map { event ->
    when (event) {
      is CreateTransaction -> {
        event.handleEventData(createTransactionRepository)
        CreateTransactionScreenDestination
      }

      is SelectAccount -> {
        createTransactionRepository.startSelectSourceFlow()
        AccountsScreenRouteDestination
      }

      is CreateCategory -> CreateCategoryRouteDestination(event.categoryType)
      is SelectColor -> SelectColorScreenDestination(event.preselectedColorId)
      is SelectCurrency -> SelectCurrencyScreenDestination
      is SelectIcon -> SelectIconScreenDestination(event.preselectedIconId)
    }
  }
}

private fun CreateTransaction.handleEventData(
  createTransactionRepository: CreateTransactionRepository,
) {
  target?.let(createTransactionRepository::setTarget)
  source?.let(createTransactionRepository::setSource)
}
