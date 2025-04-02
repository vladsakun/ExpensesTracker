package com.emendo.expensestracker.settings

import com.emendo.expensestracker.settings.destinations.SettingsRouteDestination
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class SettingsScreenApiImpl @Inject constructor() : SettingsScreenApi {

  override fun getRoute(): String = SettingsRouteDestination.route
}