package com.emendo.expensestracker.accounts.common

import androidx.compose.runtime.Immutable
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.model.ui.ColorModel

@Immutable
data class AccountScreenData<T>(
  val name: String,
  val icon: IconModel,
  val color: ColorModel,
  val balance: Amount,
  val currency: CurrencyModel,
  val confirmEnabled: Boolean,
  val additionalData: T? = null,
)
