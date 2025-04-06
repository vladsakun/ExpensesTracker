package com.emendo.expensestracker.data.api.model.transaction

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue

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

fun TransactionTarget(
  id: Long,
  icon: IconModel,
  name: TextValue,
  color: ColorModel,
  ordinalIndex: Int,
) = object : TransactionTarget {
  override val id: Long = id
  override val icon: IconModel = icon
  override val name: TextValue = name
  override val color: ColorModel = color
  override val ordinalIndex: Int = ordinalIndex
  override val currency: CurrencyModel? = null
}