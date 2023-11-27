package com.emendo.expensestracker.core.ui

import androidx.compose.foundation.lazy.LazyListScope
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem

fun LazyListScope.loader() {
  uniqueItem("loader") { ExpLoadingWheel() }
}