package com.emendo.expensestracker.core.app.resources.models

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
enum class ColorModel(
  val id: Int,
  val darkColor: Color,
  val lightColor: Color? = null,
) {
  // Todo add black theme palette
  //  RED(1, Color(0xffff0000)),
  //  VERMILION(2, Color(0xffff4d00)),
  //  ORANGE(3, Color(0xffff7f00)),
  //  GOLD(4, Color(0xffffaa00)),
  //  YELLOW(5, Color(0xffffff00)),
  //  LEMON(6, Color(0xffffff66)),
  //  CHARTREUSE(7, Color(0xffccff66)),
  //  LIME(8, Color(0xff99ff33)),
  //  GREEN(9, Color(0xff00ff00)),
  //  MINT(10, Color(0xff66ffcc)),
  //  TEAL(11, Color(0xff00cccc)),
  //  CYAN(12, Color(0xff00ffff)),
  //  SKYBLUE(13, Color(0xff66ccff)),
  //  BLUE(14, Color(0xff0000ff)),
  //  NAVY(15, Color(0xff000099)),
  //  INDIGO(16, Color(0xff330099)),
  //  VIOLET(17, Color(0xff6600cc)),
  //  PURPLE(18, Color(0xff9900cc)),
  //  MAGENTA(19, Color(0xffff00ff)),
  //  PINK(20, Color(0xffff66cc)),
  //  SALMON(21, Color(0xffff6666)),
  //  CORAL(22, Color(0xffff6633)),
  //  RUST(23, Color(0xffcc3300)),
  //  BROWN(24, Color(0xff663300)),
  //  SIENNA(25, Color(0xff996633)),
  //  KHAKI(26, Color(0xffcc9966)),
  //  BEIGE(27, Color(0xffffcc99));

  Grey1(1, Color(0xffd9d9d9), Color(0xff333333)),
  Grey2(2, Color(0xff999999)),
  Grey3(3, Color(0xffccc7ab)),
  Yellow1(4, Color(0xffFFD700)),
  Yellow2(5, Color(0xffFFC300)),
  Yellow3(6, Color(0xffc44c4f)),
  Brown(7, Color(0xffa5784f)),
  Orange1(8, Color(0xffFFA500)),
  Orange2(9, Color(0xffFF8C00)),
  Orange3(10, Color(0xffFF7300)),
  PinkDark(11, Color(0xffFF6B81)),
  PinkDarker(12, Color(0xffFF4785)),
  PinkDarkest(13, Color(0xffFF2D8F)),
  RedDark(14, Color(0xffFF0000)),
  RedDarker(15, Color(0xfffc0f3d)),
  RedDarkest(16, Color(0xffFF6E6E)),
  PurpleDark(17, Color(0xffba68bf)),
  PurpleDarker(18, Color(0xff7a38b5)),
  PurpleDarkest(19, Color(0xffa061fa)),
  Blue1(20, Color(0xff003F91)),
  Blue2(21, Color(0xff0054A6)),
  Blue3(22, Color(0xff0067BB)),
  GreenDark(23, Color(0xff00D42E)),
  GreenDarker(24, Color(0xff00B82E)),
  GreenDarkest(25, Color(0xff009F2E)),
  LimeDark(26, Color(0xffA8FF00)),
  LimeDarker(27, Color(0xff7CFF00)),
  LimeDarkest(28, Color(0xff1ebf91)),
  DarkAccent1(29, Color(0xff64A69C)),
  DarkAccent2(30, Color(0xff00758F)),
  DarkAccent3(31, Color(0xff005460));

  //  DOWNRIVER(1, Color(0xff0c2f4a)),
  //  EDEN(2, Color(0xff0e4057)),
  //  GENOE(3, Color(0xff136e75)),
  //  ELM(4, Color(0xff1a8f85)),
  //  MOUNTAIN_MEADOW(5, Color(0xff1fad7e)),
  //  GREEN(6, Color(0xff24c758)),
  //  LIMA(7, Color(0xff31db28)),
  //  CHARTREUSE(8, Color(0xff79ff1f)),
  //  INCH_WORM(9, Color(0xffb7e61c)),
  //  BIRD_FLOWER(10, Color(0xffd1c219)),
  //  MANDALAY(11, Color(0xffb38717)),
  //  HAWAIIAN_TAN(12, Color(0xff994417)),
  //  FALU_RED(13, Color(0xff7a1b14)),
  //  HEALTH(14, Color(0xff591022)),
  //  LOULOU(15, Color(0xff3d0b2c)),
  //  VIOLET(16, Color(0xff1e061f)),
  //  SCARLET_GUM(17, Color(0xff3e1552)),
  //  METEORITE(18, Color(0xff492382)),
  //  GOVERNOR_BAY(19, Color(0xff2d33a1)),
  //  PERSIAN_BLUE(20, Color(0xff1c4abd)),
  //  CURIOUS_BLUE(21, Color(0xff1f7bde)),
  //  DODGER_BLUE(22, Color(0xff2ba3ff)),
  //  MALIBU(23, Color(0xff70d2ff)),
  //  MABEL(24, Color(0xffd9f4ff)),
  //  MINT_JULEP(25, Color(0xfff0ebb8)),
  //  MANZ(26, Color(0xfff0f07d)),
  //  KEY_LIME_PIE(27, Color(0xffd4d422)),
  //  YELLOW(28, Color(0xffbaba1a)),
  //  RAW_UMBER(29, Color(0xff704e14)),
  //  CIOCCOLATO(30, Color(0xff4d2b0c)),
  //  CLINKER(31, Color(0xff301108));

  companion object {
    private val values = entries.associateBy { it.id }
    val DEFAULT_COLOR = Grey1

    fun getById(id: Int) = values[id] ?: DEFAULT_COLOR ?: throw IllegalArgumentException("No EntityColor with id $id")
    inline val random get() = entries.random()

    inline val ColorModel.color: Color
      @Composable get() = if (isSystemInDarkTheme()) darkColor else lightColor ?: darkColor
  }
}