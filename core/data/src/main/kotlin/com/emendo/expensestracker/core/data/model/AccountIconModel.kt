package com.emendo.expensestracker.core.data.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.expensestracker.core.app.resources.icon.AccountIcons

enum class AccountIconModel constructor(
  val id: Int,
  val imageVector: ImageVector,
) {
  GOVERNMENT(1, AccountIcons.ShoppingCart),
  GROCERIES(2, AccountIcons.ShoppingCart),
  TRANSPORT(3, AccountIcons.ShoppingCart),
  HEALTHCARE(4, AccountIcons.ShoppingCart),
  HOUSEHOLDS(5, AccountIcons.ShoppingCart),
  EDUCATION(6, AccountIcons.ShoppingCart),
  ENTERTAINMENT(7, AccountIcons.ShoppingCart),
  OTHERS(8, AccountIcons.ShoppingCart);

  companion object {
    private val values = values().associateBy { it.id }

    fun getById(id: Int): AccountIconModel {
      return values[id] ?: throw IllegalArgumentException("No AccountIconResource with id $id")
    }
  }
}