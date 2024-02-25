package com.emendo.expensestracker.model.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ColorModel(
  val id: Int,
  val darkColor: Color,
  val lightColor: Color? = null,
) {
  Base(1, Color(0xffd9d9d9), Color(0xff333333)),
  Gray(2, Color(0xff999999), Color(0xff999999)),
  LightGray(3, Color(0xffccc7ab), Color(0xffccc7ab)),
  Tan(4, Color(0xffdeb087), Color(0xffdeb087)),
  Gold(5, Color(0xffffcc70), Color(0xffffcc70)),
  Yellow(6, Color(0xFFBEB036), Color(0xFFBEB036)),
  Orange(7, Color(0xfffca029), Color(0xfffca029)),
  Brown(8, Color(0xffa5784f), Color(0xffa5784f)),
  DarkRed(9, Color(0xffc44c4f), Color(0xffc44c4f)),
  Red(10, Color(0xfffc5433), Color(0xfffc5433)),
  Crimson(11, Color(0xfffc0f3d), Color(0xfffc0f3d)),
  Pink(12, Color(0xfff56b99), Color(0xfff56b99)),
  Magenta(13, Color(0xfff219a0), Color(0xfff219a0)),
  Purple(14, Color(0xffba68bf), Color(0xffba68bf)),
  DarkPurple(15, Color(0xff991491), Color(0xff991491)),
  Blue(16, Color(0xff7a38b5), Color(0xff7a38b5)),
  Lavender(17, Color(0xffa061fa), Color(0xffa061fa)),
  RoyalBlue(18, Color(0xff3a42ef), Color(0xff3a42ef)),
  NavyBlue(19, Color(0xff176bb0), Color(0xff176bb0)),
  SkyBlue(20, Color(0xff199ee3), Color(0xff199ee3)),
  Turquoise(21, Color(0xff21c1e5), Color(0xff21c1e5)),
  Green(22, Color(0xff1ebf91), Color(0xff1ebf91)),
  LimeGreen(23, Color(0xff35ab4f), Color(0xff35ab4f)),
  BrightGreen(24, Color(0xff1ec923), Color(0xff1ec923)),
  Lime(25, Color(0xffabde3d), Color(0xffabde3d)),
  Olive(26, Color(0xFFAFB164), Color(0xFFAFB164));

  companion object {
    val DEFAULT_COLOR = Base
    val random
      get() = entries.random()
    val ColorModel.color: Color
      @Composable get() = if (isSystemInDarkTheme()) darkColor else lightColor ?: darkColor

    private val values = entries.associateBy { it.id }
    fun getById(id: Int) = values[id] ?: DEFAULT_COLOR
  }
}