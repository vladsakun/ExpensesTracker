package com.emendo.expensestracker.core.app.resources.models

import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.expensestracker.core.app.resources.icon.AccountIcons

enum class IconModel(
  val id: Int,
  val imageVector: ImageVector,
) {
  UNKNOWN(-1, AccountIcons.Unknown),
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
  OTHERS(13, AccountIcons.Square),
  SCIENCE(14, AccountIcons.Science),
  ENERGY(15, AccountIcons.EnergySavingsLeaf),
  BATHTUB(16, AccountIcons.Bathtub),
  SPEAKER(17, AccountIcons.Speaker),
  WORK(18, AccountIcons.Work),
  TAXI(19, AccountIcons.LocalTaxi),
  CAR(20, AccountIcons.DirectionsCar),
  TOYS(21, AccountIcons.Toys),
  LUGGAGE(22, AccountIcons.Luggage),
  CELLTOWER(23, AccountIcons.CellTower),
  CRUELTYFREE(24, AccountIcons.CrueltyFree),
  CONTENTCUT(25, AccountIcons.ContentCut),
  LOCALDINING(26, AccountIcons.LocalDining),
  DISCOUNT(27, AccountIcons.Discount),
  ARCHIVE(28, AccountIcons.Archive),
  DANGEROUS(29, AccountIcons.Dangerous),
  WALLET(30, AccountIcons.Wallet),
  HOUSE(31, AccountIcons.House),
  CIRCLE(32, AccountIcons.Circle);

  companion object {
    private val icons: List<IconModel> = entries.toMutableList().apply { remove(UNKNOWN) }
    private val values = entries.associateBy { it.id }

    fun getById(id: Int) = values[id] ?: throw IllegalArgumentException("No IconResource with id $id")
    val random
      get() = icons.random()
  }
}