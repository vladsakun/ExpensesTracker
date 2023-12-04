package com.emendo.expensestracker.core.app.base.shared.destinations

import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.emendo.expensestracker.core.app.base.shared.SelectColorScreen
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectColorScreenDestination.NavArgs
import com.emendo.expensestracker.core.app.base.shared.navtype.colorModelEnumNavType
import com.emendo.expensestracker.core.app.resources.models.ColorModel
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

public object SelectColorScreenDestination : AppbaseuiTypedDestination<SelectColorScreenDestination.NavArgs> {
    
    override fun invoke(navArgs: NavArgs): Direction = with(navArgs) {
        invoke(selectedColor)
    }
     
    public operator fun invoke(
		selectedColor: ColorModel,
    ): Direction {
        return Direction(
            route = "$baseRoute" + 
					"/${colorModelEnumNavType.serializeValue(selectedColor)}"
        )
    }
    
    @get:RestrictTo(RestrictTo.Scope.SUBCLASSES)
    override val baseRoute: String = "select_color_screen"

    override val route: String = "$baseRoute/{selectedColor}"
    
	override val arguments: List<NamedNavArgument> get() = listOf(
		navArgument("selectedColor") {
			type = colorModelEnumNavType
		}
	)

    @Composable
    override fun DestinationScope<NavArgs>.Content() {
		val (selectedColor) = navArgs
		SelectColorScreen(
			navigator = destinationsNavigator, 
			resultNavigator = resultBackNavigator(), 
			selectedColor = selectedColor
		)
    }
                    
	override fun argsFrom(bundle: Bundle?): NavArgs {
	    return NavArgs(
			selectedColor = colorModelEnumNavType.safeGet(bundle, "selectedColor") ?: throw RuntimeException("'selectedColor' argument is mandatory, but was not present!"),
	    )
	}
                
	override fun argsFrom(savedStateHandle: SavedStateHandle): NavArgs {
	    return NavArgs(
			selectedColor = colorModelEnumNavType.get(savedStateHandle, "selectedColor") ?: throw RuntimeException("'selectedColor' argument is mandatory, but was not present!"),
	    )
	}

	public data class NavArgs(
		val selectedColor: ColorModel,
	)
}