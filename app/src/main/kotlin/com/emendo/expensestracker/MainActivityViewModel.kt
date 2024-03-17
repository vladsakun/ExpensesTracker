package com.emendo.expensestracker

import androidx.lifecycle.ViewModel
import com.emendo.expensestracker.app.base.api.AppNavigationEvent.CreateCategory
import com.emendo.expensestracker.app.base.api.AppNavigationEventBus
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.core.domain.api.CreateTransactionController
import com.emendo.expensestracker.data.api.manager.ExpeLocaleManager
import com.emendo.expensestracker.data.api.manager.ExpeTimeZoneManager
import com.ramcosta.composedestinations.spec.Direction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

//@BindGenericAs.Default(BindGenericAs.Wildcard)
//interface NavigationStrategy<T : AppNavigationEvent> {
//  val navigationEvent: KClass<T>
//  fun createNavigationEvent(event: T): NavigationEvent
//}
//
//@AutoBindIntoSet
//class CreateTransactionStrategy @Inject constructor(
//  private val createTransactionController: CreateTransactionController,
//  private val priv: Provider<CreateTransactionController>
//) : NavigationStrategy<CreateTransaction> {
//
//  override val navigationEvent: KClass<CreateTransaction> = CreateTransaction::class
//
//  override fun createNavigationEvent(event: CreateTransaction): NavigationEvent {
//    event.handleEventData(createTransactionController)
//    return CreatetransactionNavGraph.asNavigationEvent(event.shouldNavigateUp)
//  }
//}
//
//@AutoBindIntoSet
//class SelectAccountStrategy @Inject constructor(
//  private val createTransactionController: CreateTransactionController,
//) : NavigationStrategy<SelectAccount> {
//
//  override val navigationEvent: KClass<SelectAccount> = SelectAccount::class
//
//  override fun createNavigationEvent(event: SelectAccount): NavigationEvent {
//    event.handleEventData(createTransactionController)
//    return AccountsScreenRouteDestination.asNavigationEvent()
//  }
//}
//
//@AutoBindIntoSet
//class CreateAccountStrategy @Inject constructor() : NavigationStrategy<CreateAccount> {
//
//  override val navigationEvent: KClass<CreateAccount> = CreateAccount::class
//
//  override fun createNavigationEvent(event: CreateAccount): NavigationEvent {
//    return CreateAccountRouteDestination.asNavigationEvent()
//  }
//}
//
//@AutoBindIntoSet
//class CreateCategoryStrategy @Inject constructor() : NavigationStrategy<CreateCategory> {
//
//  override val navigationEvent: KClass<CreateCategory> = CreateCategory::class
//
//  override fun createNavigationEvent(event: CreateCategory): NavigationEvent {
//    return CreateCategoryRouteDestination(event.categoryType).asNavigationEvent()
//  }
//}
//
//class NavigationStrategyFactory @Inject constructor(
//  private val strategies: Set<@JvmSuppressWildcards NavigationStrategy<*>>,
//) {
//
//  fun createStrategyProvider(event: AppNavigationEvent): NavigationStrategy<AppNavigationEvent> {
//    val strategy = when (event) {
//      is CreateTransaction -> strategies[CreateTransactionStrategy::class]
//      is CreateCategory -> strategies[CreateCategoryStrategy::class]
//      is SelectIcon -> strategies[SelectIconStrategy::class]
//      // Add other strategies...
//      else -> throw IllegalArgumentException("No strategy found for ${event::class}")
//    }
//
//    return strategy as NavigationStrategy<AppNavigationEvent>
//  }
//
////  private fun Set<NavigationStrategy<*>>.findByClass(kClass: KClass<CreateTransactionStrategy>): NavigationStrategy<*> =
////    first { it::class == kClass }
//
//  private operator fun <T : Any> Set<T>.get(kClass: KClass<*>): T {
//    return first { it::class == kClass }
//  }
//}
//
//@AutoBindIntoSet(bindGenericAs = BindGenericAs.Wildcard)
//class CreateTransactionStrategy @Inject constructor(
//  private val createTransactionController: CreateTransactionController,
//) : NavigationStrategy<CreateTransaction> {
//
//  override fun createNavigationEvent(event: CreateTransaction): String {
//    event.handleEventData(createTransactionController)
//    return CreatetransactionNavGraph.asNavigationEvent(event.shouldNavigateUp).direction.route
//  }
//}
//
//@AutoBindIntoSet(bindGenericAs = BindGenericAs.Wildcard)
//class CreateCategoryStrategy @Inject constructor() : NavigationStrategy<CreateCategory> {
//
//  override fun createNavigationEvent(event: CreateCategory): String {
//    return CreateCategoryRouteDestination(event.categoryType).asNavigationEvent().direction.route
//  }
//}
//
//@AutoBindIntoSet(bindGenericAs = BindGenericAs.Wildcard)
//class SelectIconStrategy @Inject constructor() : NavigationStrategy<SelectIcon> {
//
//  override fun createNavigationEvent(event: SelectIcon): String {
//    return SelectIconScreenDestination(event.preselectedIconId).asNavigationEvent().direction.route
//  }
//}

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  appNavigationEventBus: AppNavigationEventBus,
  private val createTransactionController: CreateTransactionController,
  private val localeManager: ExpeLocaleManager,
  private val timeZoneManager: ExpeTimeZoneManager,
  //  private val navigationStrategyFactory: NavigationStrategyFactory,
) : ViewModel() {

  // Todo rethink huge when
  val navigationEvent: Flow<NavigationEvent> = appNavigationEventBus.eventFlow.map { event ->
    when (event) {
      is CreateCategory -> CreateCategoryRouteDestination(event.categoryType).asNavigationEvent()
    }
  }

  //  val navigationEvent: Flow<NavigationEvent> = appNavigationEventBus.eventFlow
  //    .map { event ->
  //      NavigationEvent(
  //        direction = Direction(navigationStrategyFactory.createStrategyProvider(event).createNavigationEvent(event))
  //      )
  //      //      navigationStrategyFactory.strategyMap[event::class]!!.createNavigationEvent(event)
  //    }

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
