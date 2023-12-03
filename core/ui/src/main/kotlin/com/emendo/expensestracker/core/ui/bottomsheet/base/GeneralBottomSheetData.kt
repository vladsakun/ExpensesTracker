package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.app.resources.models.TextValue

@Stable
class GeneralBottomSheetData(
  val title: TextValue?,
  val positiveAction: Action,
  val negativeAction: Action?,
) : BottomSheetType {
  private constructor(builder: Builder) : this(
    title = builder.title,
    positiveAction = builder.positiveAction,
    negativeAction = builder.negativeAction,
  )

  class Builder(val positiveAction: Action) {
    var title: TextValue? = null
      private set
    var negativeAction: Action? = null
      private set

    fun title(title: TextValue) = apply { this.title = title }
    fun negativeAction(negativeAction: Action) = apply { this.negativeAction = negativeAction }

    fun create() = GeneralBottomSheetData(this)
  }
}

data class Action(
  val title: TextValue,
  val action: () -> Unit,
)
