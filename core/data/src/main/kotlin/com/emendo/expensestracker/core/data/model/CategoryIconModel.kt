package com.emendo.expensestracker.core.data.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons

enum class CategoryIconModel constructor(
  val id: Int,
  val imageVector: ImageVector,
) {
  GOVERNMENT(1, ExpIcons.CreditCard),
  GROCERIES(2, ExpIcons.CreditCard),
  TRANSPORT(3, ExpIcons.CreditCard),
  HEALTHCARE(4, ExpIcons.CreditCard),
  HOUSEHOLDS(5, ExpIcons.CreditCard),
  EDUCATION(6, ExpIcons.CreditCard),
  ENTERTAINMENT(7, ExpIcons.CreditCard),
  OTHERS(8, ExpIcons.CreditCard);

  companion object {
    private val values = values().associateBy { it.id }

    fun getById(id: Int): CategoryIconModel {
      return values[id] ?: throw IllegalArgumentException("No AccountIconResource with id $id")
    }
  }
}