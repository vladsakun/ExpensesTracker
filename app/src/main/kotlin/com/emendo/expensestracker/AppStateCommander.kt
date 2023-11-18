package com.emendo.expensestracker

interface AppStateCommander {
  fun onPositiveActionClick()
  fun onNegativeActionClick()
  fun onAlertDialogDismissRequest()
}