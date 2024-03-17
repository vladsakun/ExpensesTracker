package com.emendo.expensestracker.core.app.base.shared.currency

import com.emendo.expensestracker.app.base.api.screens.SelectCurrencyScreenApi
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectCurrencyScreenDestination
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
internal class SelectCurrencyScreenApiImpl @Inject constructor() : SelectCurrencyScreenApi {

  override fun getSelectCurrencyScreenRoute(): String =
    SelectCurrencyScreenDestination().route
}