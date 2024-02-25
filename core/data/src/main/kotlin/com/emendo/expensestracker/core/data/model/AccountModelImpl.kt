package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TextValue
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.data.api.model.AccountModel

internal data class AccountModelImpl(
  override val id: Long = 0,
  override val currency: CurrencyModel,
  override val name: TextValue.Value,
  override val icon: IconModel,
  override val color: ColorModel,
  override val ordinalIndex: Int,
  override val balance: Amount,
) : AccountModel