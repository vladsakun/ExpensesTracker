package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.model.data.CurrencyModel

sealed interface TransactionElement {
  val id: Long
}

interface TransactionSource : TransactionElement {
  val currency: CurrencyModel
}

interface TransactionTarget : TransactionElement {
  val currency: CurrencyModel?
    get() = null
}