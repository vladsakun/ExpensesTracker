package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel

sealed interface Target {
  val id: Long
  val name: String
  val icon: IconModel
  val color: ColorModel

  class Account(account: AccountModel) : Target {
    override val id: Long = account.id
    override val name: String = account.name
    override val icon: IconModel = account.icon
    override val color: ColorModel = account.color
  }

  class Category(category: CategoryModel) : Target {
    override val id: Long = category.id
    override val name: String = category.name
    override val icon: IconModel = category.icon
    override val color: ColorModel = category.color
    val type: CategoryType = category.type
  }
}