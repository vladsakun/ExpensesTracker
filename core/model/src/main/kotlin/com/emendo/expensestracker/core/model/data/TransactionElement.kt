package com.emendo.expensestracker.core.model.data

interface TransactionElement {
  val id: Long
}

interface TransactionSource : TransactionElement
interface TransactionTarget : TransactionElement