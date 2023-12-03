package com.emendo.expensestracker

import androidx.lifecycle.ViewModel
import com.emendo.expensestracker.accounts.destinations.AccountsScreenRouteDestination
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEvent
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.base.manager.CreateTransactionRepository
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

  val navigationEvent: Flow<Direction> = appNavigationEventBus.eventFlow.map { event ->
    when (event) {
      is AppNavigationEvent.CreateTransaction -> {
        event.target?.let { target ->
          createTransactionRepository.setTarget(target)
        }
        event.source?.let { source ->
          createTransactionRepository.setSource(source)
        }
        CreateTransactionScreenDestination
      }

      is AppNavigationEvent.CreateCategory -> {
        CreateCategoryRouteDestination(event.categoryType)
      }

      is AppNavigationEvent.SelectAccount -> {
        createTransactionRepository.startSelectSourceFlow()
        AccountsScreenRouteDestination
      }
    }
  }
}

