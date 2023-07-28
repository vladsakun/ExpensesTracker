package com.emendo.expensestracker.core.data.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons

enum class CategoryIconResource constructor(
  val id: Int,
  val imageVector: ImageVector,
) {
  GOVERNMENT(1, ExpIcons.Accounts),
  GROCERIES(2, ExpIcons.Accounts),
  TRANSPORT(3, ExpIcons.Accounts),
  HEALTHCARE(4, ExpIcons.Accounts),
  HOUSEHOLDS(5, ExpIcons.Accounts),
  EDUCATION(6, ExpIcons.Accounts),
  ENTERTAINMENT(7, ExpIcons.Accounts),
  OTHERS(8, ExpIcons.Accounts);

  companion object {
    private val values = values().associateBy { it.id }

    fun getById(id: Int): CategoryIconResource {
      return values[id] ?: throw IllegalArgumentException("No AccountIconResource with id $id")
    }
  }
}