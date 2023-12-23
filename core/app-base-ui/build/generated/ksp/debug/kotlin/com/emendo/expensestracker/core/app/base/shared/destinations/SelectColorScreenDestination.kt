package com.emendo.expensestracker.core.app.base.shared.destinations

import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.emendo.expensestracker.core.app.base.shared.color.SelectColorScreen
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectColorScreenDestination.NavArgs
import com.emendo.expensestracker.core.ui.bottomsheet.BottomScreenTransition
import com.ramcosta.composedestinations.navargs.primitives.DestinationsIntNavType
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
public object SelectColorScreenDestination : AppbaseuiTypedDestination<SelectColorScreenDestination.NavArgs> {
    
    override fun invoke(navArgs: NavArgs): Direction = with(navArgs) {
        invoke(selectedColorId)
    }
     
    public operator fun invoke(
		selectedColorId: Int,
    ): Direction {
        return Direction(
            route = "$baseRoute" + 
					"/${DestinationsIntNavType.serializeValue(selectedColorId)}"
        )
    }
    
    @get:RestrictTo(RestrictTo.Scope.SUBCLASSES)
    override val baseRoute: String = "select_color_screen"

    override val route: String = "$baseRoute/{selectedColorId}"
    
	override val arguments: List<NamedNavArgument> get() = listOf(
		navArgument("selectedColorId") {
			type = DestinationsIntNavType
		}
	)

	override val style: DestinationStyle = BottomScreenTransition

    @Composable
    override fun DestinationScope<NavArgs>.Content() {
		val (selectedColorId) = navArgs
		SelectColorScreen(
			navigator = destinationsNavigator, 
			resultNavigator = resultBackNavigator(), 
			selectedColorId = selectedColorId
		)
    }
                    
	override fun argsFrom(bundle: Bundle?): NavArgs {
	    return NavArgs(
			selectedColorId = DestinationsIntNavType.safeGet(bundle, "selectedColorId") ?: throw RuntimeException("'selectedColorId' argument is mandatory, but was not present!"),
	    )
	}
                
	override fun argsFrom(savedStateHandle: SavedStateHandle): NavArgs {
	    return NavArgs(
			selectedColorId = DestinationsIntNavType.get(savedStateHandle, "selectedColorId") ?: throw RuntimeException("'selectedColorId' argument is mandatory, but was not present!"),
	    )
	}

	public data class NavArgs(
		val selectedColorId: Int,
	)
}