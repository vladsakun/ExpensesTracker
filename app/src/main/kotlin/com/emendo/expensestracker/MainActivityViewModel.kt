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
import com.emendo.expensestracker.core.data.manager.ExpeLocaleManager
import com.emendo.expensestracker.core.data.manager.ExpeTimeZoneManager
import com.emendo.expensestracker.createtransaction.CreatetransactionNavGraph
import com.ramcosta.composedestinations.spec.Direction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  appNavigationEventBus: AppNavigationEventBus,
  private val createTransactionRepository: CreateTransactionRepository,
  private val localeManager: ExpeLocaleManager,
  private val timeZoneManager: ExpeTimeZoneManager,
) : ViewModel() {

  // Todo rethink huge when
  val navigationEvent: Flow<NavigationEvent> = appNavigationEventBus.eventFlow.map { event ->
    when (event) {
      is CreateTransaction -> {
        event.handleEventData(createTransactionRepository)
        CreatetransactionNavGraph.asNavigationEvent(event.shouldNavigateUp)
      }

      is SelectAccount -> {
        createTransactionRepository.startSelectSourceFlow()
        AccountsScreenRouteDestination.asNavigationEvent()
      }

      is CreateCategory -> CreateCategoryRouteDestination(event.categoryType).asNavigationEvent()
      is SelectColor -> SelectColorScreenDestination(event.preselectedColorId).asNavigationEvent()
      is SelectCurrency -> SelectCurrencyScreenDestination.asNavigationEvent()
      is SelectIcon -> SelectIconScreenDestination(event.preselectedIconId).asNavigationEvent()
    }
  }

  fun updateLocale() {
    localeManager.onLocaleChange()
  }

  fun updateTimeZone() {
    timeZoneManager.onZoneChange()
  }
}

data class NavigationEvent(
  val direction: Direction,
  val navigateUp: Boolean = false,
)

fun Direction.asNavigationEvent(navigateUp: Boolean = false) =
  NavigationEvent(direction = this, navigateUp = navigateUp)

private fun CreateTransaction.handleEventData(
  createTransactionRepository: CreateTransactionRepository,
) {
  target?.let(createTransactionRepository::setTarget)
  source?.let(createTransactionRepository::setSource)
  payload?.let(createTransactionRepository::setTransactionPayload)
}
