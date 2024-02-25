package com.emendo.expensestracker.accounts.detail

import com.emendo.expensestracker.accounts.common.model.AccountScreenData
import com.emendo.expensestracker.data.api.model.AccountModel

typealias AccountDetailScreenData = AccountScreenData<Boolean>

fun getDefaultAccountDetailScreenState(accountModel: AccountModel) =
  AccountDetailScreenData(
    name = accountModel.name.value,
    icon = accountModel.icon,
    color = accountModel.color,
    balance = accountModel.balance,
    currency = accountModel.currency,
    confirmEnabled = false,
  )