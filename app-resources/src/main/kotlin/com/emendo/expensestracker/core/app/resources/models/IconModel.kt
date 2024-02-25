package com.emendo.expensestracker.core.app.resources.models

import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.expensestracker.core.app.resources.icon.AccountIcons
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons

enum class IconModel(
  val id: Int,
  val imageVector: ImageVector,
) {
  UNKNOWN(-1, ExpeIcons.HideSource),
  GOVERNMENT(1, AccountIcons.AccountBalance),
  GROCERIES(2, AccountIcons.ShoppingCart),
  TRANSPORT(3, AccountIcons.FireTruck),
  HEALTHCARE(4, AccountIcons.Healing),
  HOUSEHOLDS(5, AccountIcons.House),
  EDUCATION(6, AccountIcons.School),
  ENTERTAINMENT(7, AccountIcons.Gamepad),
  PETS(8, AccountIcons.Pets),
  SMARTPHONE(9, AccountIcons.Smartphone),
  WATCH(10, AccountIcons.Watch),
  CHILDCARE(11, AccountIcons.ChildCare),
  CREDITCARD(12, AccountIcons.CreditCard),
  OTHERS(13, AccountIcons.Square);

  companion object {
    private val icons: List<IconModel> = entries.toMutableList().apply { remove(UNKNOWN) }
    private val values = entries.associateBy { it.id }

    fun getById(id: Int) = values[id] ?: throw IllegalArgumentException("No IconResource with id $id")
    val random
      get() = icons.random()
  }
}