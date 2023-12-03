package com.emendo.expensestracker.core.data.model.transaction

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TextValue
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType

sealed interface TransactionTargetUiModel {
  val id: Long
  val name: TextValue
  val icon: IconModel
  val color: ColorModel
  val transactionType: TransactionType

  class Account(account: AccountModel) : TransactionTargetUiModel {
    override val id: Long = account.id
    override val name: TextValue = account.name
    override val icon: IconModel = account.icon
    override val color: ColorModel = account.color
    override val transactionType: TransactionType = TransactionType.TRANSFER
  }

  class Category(category: CategoryModel) : TransactionTargetUiModel {
    override val id: Long = category.id
    override val name: TextValue = category.name
    override val icon: IconModel = category.icon
    override val color: ColorModel = category.color
    override val transactionType: TransactionType = getTransactionType(category)
    val type: CategoryType = category.type

    private fun getTransactionType(category: CategoryModel) =
      when (category.type) {
        CategoryType.INCOME -> TransactionType.INCOME
        CategoryType.EXPENSE -> TransactionType.EXPENSE
      }
  }
}