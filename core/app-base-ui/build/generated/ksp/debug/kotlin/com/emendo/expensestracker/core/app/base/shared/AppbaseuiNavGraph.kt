package com.emendo.expensestracker.core.app.base.shared

import com.emendo.expensestracker.core.app.base.shared.destinations.*
import com.ramcosta.composedestinations.spec.*

public object AppbaseuiNavGraph : NavGraphSpec {
    
    override val route: String = "appbaseui"
    
    override val startRoute: Route = DummyStartDestination
    
    override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
		DummyStartDestination,
		SelectColorScreenDestination,
		SelectCurrencyScreenDestination,
		SelectIconScreenDestination
    ).associateBy { it.route }

}
