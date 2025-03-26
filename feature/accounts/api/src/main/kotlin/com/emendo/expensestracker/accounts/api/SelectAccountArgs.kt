package com.emendo.expensestracker.accounts.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectAccountArgs(
  val isSource: Boolean,
  val selectedAccountId: Long? = null,
) : Parcelable