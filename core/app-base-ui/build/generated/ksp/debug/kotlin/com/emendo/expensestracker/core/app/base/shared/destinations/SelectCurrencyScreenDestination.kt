package com.emendo.expensestracker.core.app.base.shared.destinations

import androidx.annotation.RestrictTo
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.emendo.expensestracker.core.app.base.shared.currency.SelectCurrencyScreen
import com.emendo.expensestracker.core.ui.bottomsheet.BottomScreenTransition
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.DestinationDependenciesContainer
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.scope.resultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.ramcosta.composedestinations.spec.Direction
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.ramcosta.composedestinations.spec.Route

@OptIn(ExperimentalAnimationApi::class)
public object SelectCurrencyScreenDestination : AppbaseuiDirectionDestination {
         
    public operator fun invoke(): Direction = this
    
    @get:RestrictTo(RestrictTo.Scope.SUBCLASSES)
    override val baseRoute: String = "select_currency_screen"

    override val route: String = baseRoute
    
	override val style: DestinationStyle = BottomScreenTransition

    @Composable
    override fun DestinationScope<Unit>.Content() {
		SelectCurrencyScreen(
			navigator = destinationsNavigator, 
			resultNavigator = resultBackNavigator()
		)
    }
    
}