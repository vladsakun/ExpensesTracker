package com.emendo.expensestracker.core.designsystem.component.bottomsheet

sealed interface NumKeyboardNumber {
  val number: Int

  data class Zero(override val number: Int = 0) : NumKeyboardNumber
  data class One(override val number: Int = 1) : NumKeyboardNumber
  data class Two(override val number: Int = 2) : NumKeyboardNumber
  data class Three(override val number: Int = 3) : NumKeyboardNumber
  data class Four(override val number: Int = 4) : NumKeyboardNumber
  data class Five(override val number: Int = 5) : NumKeyboardNumber
  data class Six(override val number: Int = 6) : NumKeyboardNumber
  data class Seven(override val number: Int = 7) : NumKeyboardNumber
  data class Eight(override val number: Int = 8) : NumKeyboardNumber
  data class Nine(override val number: Int = 9) : NumKeyboardNumber
}