package com.emendo.expensestracker.data.api

import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState

interface KeyboardCallbacks {
  fun doOnValueChange(formattedValue: String, equalButtonState: EqualButtonState)
  fun onMathDone(result: String)
}