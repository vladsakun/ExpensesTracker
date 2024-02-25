package com.emendo.expensestracker.data.api.model.transaction

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.model.ui.TextValue

sealed interface TransactionElement {
  val id: Long
  val icon: IconModel
  val name: TextValue
  val color: com.emendo.expensestracker.model.ui.ColorModel
  val ordinalIndex: Int
}

interface TransactionSource : TransactionElement {
  val currency: CurrencyModel
}

interface TransactionTarget : TransactionElement {
  val currency: CurrencyModel?
}