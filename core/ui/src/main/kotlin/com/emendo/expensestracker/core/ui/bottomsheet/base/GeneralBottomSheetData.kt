package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.emendo.expensestracker.core.app.resources.models.TextValue
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette

@Stable
class GeneralBottomSheetData private constructor(
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
    internal var title: TextValue? = null
      private set
    internal var negativeAction: Action? = null
      private set

    fun title(title: TextValue) = apply { this.title = title }
    fun negativeAction(negativeAction: Action) = apply { this.negativeAction = negativeAction }

    fun build() = GeneralBottomSheetData(this)
  }
}

enum class ActionType {
  POSITIVE,
  DANGER;

  companion object {

    internal val ActionType.color: Color
      @Composable get() = when (this) {
        POSITIVE -> MaterialTheme.customColorsPalette.successColor
        DANGER -> MaterialTheme.colorScheme.error
      }
  }
}

data class Action(
  val title: TextValue,
  val action: () -> Unit,
  val type: ActionType? = null,
)
