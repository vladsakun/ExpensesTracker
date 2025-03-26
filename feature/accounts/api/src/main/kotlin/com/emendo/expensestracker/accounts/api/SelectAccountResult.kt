package com.emendo.expensestracker.accounts.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectAccountResult(
  val accountId: Long,
  val isSource: Boolean,
) : Parcelable