package com.emendo.expensestracker.accounts.create

import com.emendo.expensestracker.accounts.common.AccountScreenData
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.model.ui.ColorModel

typealias CreateAccountScreenData = AccountScreenData<Boolean>

fun getDefaultCreateAccountState(currency: CurrencyModel, balance: Amount) =
  CreateAccountScreenData(
    name = "",
    icon = IconModel.random,
    color = ColorModel.random,
    balance = balance,
    currency = currency,
    confirmEnabled = false,
  )
