package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.mapper.base.Mapper
import com.emendo.expensestracker.core.database.model.budget.BudgetEntity
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.model.BudgetModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.textValueOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetMapper @Inject constructor(
  private val amountFormatter: AmountFormatter,
) : Mapper<BudgetEntity, BudgetModel> {

  override suspend fun map(from: BudgetEntity): BudgetModel = with(from) {
    val currencyModel = CurrencyModel.toCurrencyModel(currencyCode)
    BudgetModel(
      id = id,
      name = textValueOf(name),
      amount = amountFormatter.format(limit, currencyModel),
      icon = IconModel.getById(iconId),
      color = ColorModel.getById(colorId),
      period = period,
      categoryIds = categoryIds,
      currency = currencyModel,
    )
  }
}
