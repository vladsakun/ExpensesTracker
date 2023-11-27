package com.emendo.expensestracker.core.model.data.keyboard

sealed interface NumericKeyboardNumber {
  val number: Int

  data class Zero(override val number: Int = 0) : NumericKeyboardNumber
  data class One(override val number: Int = 1) : NumericKeyboardNumber
  data class Two(override val number: Int = 2) : NumericKeyboardNumber
  data class Three(override val number: Int = 3) : NumericKeyboardNumber
  data class Four(override val number: Int = 4) : NumericKeyboardNumber
  data class Five(override val number: Int = 5) : NumericKeyboardNumber
  data class Six(override val number: Int = 6) : NumericKeyboardNumber
  data class Seven(override val number: Int = 7) : NumericKeyboardNumber
  data class Eight(override val number: Int = 8) : NumericKeyboardNumber
  data class Nine(override val number: Int = 9) : NumericKeyboardNumber
}