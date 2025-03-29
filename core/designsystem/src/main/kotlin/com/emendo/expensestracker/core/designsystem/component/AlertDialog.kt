package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.screenHeightDp

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
fun ExpeAlertDialog(
  onAlertDialogDismissRequest: () -> Unit,
  onCloseClick: () -> Unit,
  onConfirmClick: () -> Unit,
  title: String? = null,
  confirmActionText: String? = null,
  dismissActionText: String? = null,
  content: @Composable BoxScope.() -> Unit,
) {
  BasicAlertDialog(onDismissRequest = onAlertDialogDismissRequest) {
    Surface(
      modifier = Modifier.wrapContentSize(),
      shape = MaterialTheme.shapes.large,
      tonalElevation = AlertDialogDefaults.TonalElevation,
    ) {
      Column(
        modifier = Modifier
          .padding(vertical = Dimens.margin_large_xxx)
      ) {
        title?.let {
          Text(
            text = it,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
              .padding(horizontal = Dimens.margin_large_xxx)
              .padding(bottom = Dimens.margin_large_x)
          )
        }
        Box(
          modifier = Modifier
            .heightIn(max = screenHeightDp / 2)
            .padding(bottom = Dimens.margin_large_x)
        ) {
          content()
        }
        if (confirmActionText != null || dismissActionText != null) {
          Box(modifier = Modifier.align(Alignment.End)) {
            FlowRow(horizontalArrangement = Arrangement.End) {
              dismissActionText?.let {
                TextButton(onClick = onCloseClick) {
                  Text(it)
                }
              }
              confirmActionText?.let {
                TextButton(onClick = onConfirmClick) {
                  Text(it)
                }
              }
            }
          }
        }
      }
    }
  }
}