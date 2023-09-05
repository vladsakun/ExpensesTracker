package com.emendo.expensestracker.core.app.resources.models

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
enum class ColorModel constructor(
  val id: Int,
  val color: Color,
) {
  DOWNRIVER(1, Color(0xff0c2f4a)),
  EDEN(2, Color(0xff0e4057)),
  GENOE(3, Color(0xff136e75)),
  ELM(4, Color(0xff1a8f85)),
  MOUNTAIN_MEADOW(5, Color(0xff1fad7e)),
  GREEN(6, Color(0xff24c758)),
  LIMA(7, Color(0xff31db28)),
  CHARTREUSE(8, Color(0xff79ff1f)),
  INCH_WORM(9, Color(0xffb7e61c)),
  BIRD_FLOWER(10, Color(0xffd1c219)),
  MANDALAY(11, Color(0xffb38717)),
  HAWAIIAN_TAN(12, Color(0xff994417)),
  FALU_RED(13, Color(0xff7a1b14)),
  HEALTH(14, Color(0xff591022)),
  LOULOU(15, Color(0xff3d0b2c)),
  VIOLET(16, Color(0xff1e061f)),
  SCARLET_GUM(17, Color(0xff3e1552)),
  METEORITE(18, Color(0xff492382)),
  GOVERNOR_BAY(19, Color(0xff2d33a1)),
  PERSIAN_BLUE(20, Color(0xff1c4abd)),
  CURIOUS_BLUE(21, Color(0xff1f7bde)),
  DODGER_BLUE(22, Color(0xff2ba3ff)),
  MALIBU(23, Color(0xff70d2ff)),
  MABEL(24, Color(0xffd9f4ff)),
  MINT_JULEP(25, Color(0xfff0ebb8)),
  MANZ(26, Color(0xfff0f07d)),
  KEY_LIME_PIE(27, Color(0xffd4d422)),
  YELLOW(28, Color(0xffbaba1a)),
  RAW_UMBER(29, Color(0xff704e14)),
  CIOCCOLATO(30, Color(0xff4d2b0c)),
  CLINKER(31, Color(0xff301108));

  companion object {
    private val values = entries.associateBy { it.id }

    fun getById(id: Int) = values[id] ?: throw IllegalArgumentException("No EntityColor with id $id")
    inline val random get() = entries.random()
  }
}