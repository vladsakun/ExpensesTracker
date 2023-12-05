package com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard

import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardNumber
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

internal object NumericKeyboardConstants {
  const val MATH_OPERATION_WEIGHT = 3f
  const val DIGIT_BUTTON_WEIGHT = 5f

  val keyboardRows: PersistentList<ImmutableList<NumericKeyboardNumber>> by lazy(LazyThreadSafetyMode.NONE) {
    persistentListOf<ImmutableList<NumericKeyboardNumber>>(
      persistentListOf(
        NumericKeyboardNumber.Seven(),
        NumericKeyboardNumber.Eight(),
        NumericKeyboardNumber.Nine(),
      ),
      persistentListOf(
        NumericKeyboardNumber.Four(),
        NumericKeyboardNumber.Five(),
        NumericKeyboardNumber.Six(),
      ),
      persistentListOf(
        NumericKeyboardNumber.One(),
        NumericKeyboardNumber.Two(),
        NumericKeyboardNumber.Three(),
      ),
      persistentListOf(
        NumericKeyboardNumber.Zero(),
      ),
    )
  }
}