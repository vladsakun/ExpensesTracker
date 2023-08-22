package com.emendo.expensestracker.core.data.model

import androidx.compose.ui.graphics.Color

enum class ColorModel constructor(
  val id: Int,
  val color: Color
) {
  RED(1, Color.Red),
  BLUE(2, Color.Blue),
  GREEN(3, Color.Green),
  YELLOW(4, Color.Yellow),
  PURPLE(5, Color.Magenta),
  CYAN(6, Color.Cyan),
  PINK(7, Color(0xFFE91E63)),
  TEAL(8, Color(0xFF009688)),
  BROWN(9, Color(0xFF795548)),
  GREY(10, Color(0xFF9E9E9E)),
  BLACK(11, Color(0xFF000000)),
  WHITE(12, Color(0xFFFFFFFF));

  companion object {
    private val values = values().associateBy { it.id }

    fun getById(id: Int): ColorModel {
      return values[id] ?: throw IllegalArgumentException("No EntityColor with id $id")
    }
  }
}