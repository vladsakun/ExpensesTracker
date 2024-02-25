package com.emendo.expensestracker.data.api.model.transaction

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TextValue
import com.emendo.expensestracker.core.model.data.CurrencyModel

sealed interface TransactionElement {
  val id: Long
  val icon: IconModel
  val name: TextValue
  val color: ColorModel
  val ordinalIndex: Int
}

interface TransactionSource : TransactionElement {
  val currency: CurrencyModel
}

interface TransactionTarget : TransactionElement {
  val currency: CurrencyModel?
}