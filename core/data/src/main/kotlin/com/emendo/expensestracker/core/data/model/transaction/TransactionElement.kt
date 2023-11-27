package com.emendo.expensestracker.core.data.model.transaction

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TransactionElementName
import com.emendo.expensestracker.core.model.data.CurrencyModel

sealed interface TransactionElement {
  val id: Long
  val icon: IconModel
  val name: TransactionElementName
  val color: ColorModel
}

interface TransactionSource : TransactionElement {
  val currency: CurrencyModel
}

interface TransactionTarget : TransactionElement {
  val currency: CurrencyModel?
    get() = null
}