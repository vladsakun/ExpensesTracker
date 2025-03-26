package com.emendo.expensestracker.data.api.extensions

import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionElement

fun TransactionElement.isAccount(): Boolean = this is AccountModel
fun TransactionElement.isCategory(): Boolean = this is CategoryModel

fun TransactionElement.asAccount(): AccountModel = this as AccountModel
fun TransactionElement.asCategory(): CategoryModel = this as CategoryModel