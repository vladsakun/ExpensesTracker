package com.emendo.expensestracker.data.api.model

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.BudgetPeriod
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue

data class BudgetModel(
  val id: Long = 0,
  val currency: CurrencyModel,
  val name: TextValue.Value,
  val icon: IconModel,
  val color: ColorModel,
  val amount: Amount,
  val period: BudgetPeriod = BudgetPeriod.MONTHLY,
  val categoryId: Long,
)
