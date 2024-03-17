package com.emendo.expensestracker.core.app.base.shared.icon

import com.emendo.expensestracker.app.base.api.screens.SelectIconScreenApi
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectIconScreenDestination
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
internal class SelectIconScreenApiImpl @Inject constructor() : SelectIconScreenApi {

  override fun getSelectIconScreenRoute(preselectedIconId: Int): String =
    SelectIconScreenDestination(preselectedIconId).route
}