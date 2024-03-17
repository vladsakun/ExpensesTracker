package com.emendo.expensestracker.core.app.base.shared.color

import com.emendo.expensestracker.app.base.api.screens.SelectColorScreenApi
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectColorScreenDestination
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
internal class SelectColorScreenApiImpl @Inject constructor() : SelectColorScreenApi {

  override fun getSelectColorScreenRoute(preselectedColorId: Int): String =
    SelectColorScreenDestination(preselectedColorId).route
}