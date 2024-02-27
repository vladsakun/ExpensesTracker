package com.emendo.expensestracker.core.ui.bottomsheet.general

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.model.ui.TextValue

class GeneralBottomSheetData private constructor(
  override val id: String,
  val title: TextValue?,
  val positiveAction: Action,
  val negativeAction: Action?,
) : BottomSheetData {

  private constructor(builder: Builder) : this(
    id = builder.id,
    title = builder.title,
    positiveAction = builder.positiveAction,
    negativeAction = builder.negativeAction,
  )

  class Builder(val id: String, val positiveAction: Action) {
    internal var title: TextValue? = null
      private set
    internal var negativeAction: Action? = null
      private set

    fun title(title: TextValue) = apply { this.title = title }
    fun negativeAction(negativeAction: Action) = apply { this.negativeAction = negativeAction }
    fun negativeAction(
      title: TextValue,
      action: () -> Unit,
      type: ActionType? = null,
    ) = apply {
      this.negativeAction = Action(title, action, type)
    }

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
) {
  companion object {
    fun DangerAction(title: TextValue, action: () -> Unit) = Action(title, action, ActionType.DANGER)
  }
}
