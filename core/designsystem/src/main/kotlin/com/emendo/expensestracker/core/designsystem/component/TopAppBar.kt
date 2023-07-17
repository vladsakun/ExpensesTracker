@file:OptIn(ExperimentalMaterial3Api::class)

package com.emendo.expensestracker.core.designsystem.component

import android.R
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeTopAppBar(
  @StringRes titleRes: Int,
  navigationIcon: ImageVector?,
  navigationIconContentDescription: String?,
  actionIcon: ImageVector?,
  actionIconContentDescription: String?,
  modifier: Modifier = Modifier,
  colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
  onNavigationClick: () -> Unit = {},
  onActionClick: () -> Unit = {},
) {
  CenterAlignedTopAppBar(
    title = {
      Column {
        Text(text = stringResource(id = titleRes))
      }
    },
    navigationIcon = {
      navigationIcon?.let {
        IconButton(onClick = onNavigationClick) {
          Icon(
            imageVector = it,
            contentDescription = navigationIconContentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    },
    actions = {
      actionIcon?.let {
        IconButton(onClick = onActionClick) {
          Icon(
            imageVector = it,
            contentDescription = actionIconContentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    },
    colors = colors,
    modifier = modifier.testTag("expeTopAppBar"),
  )
}

/**
 * Top app bar with action, displayed on the right
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeTopAppBar(
  @StringRes titleRes: Int,
  actionIcon: ImageVector,
  actionIconContentDescription: String?,
  modifier: Modifier = Modifier,
  colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
  onActionClick: () -> Unit = {},
) {
  CenterAlignedTopAppBar(
    title = { Text(text = stringResource(id = titleRes)) },
    actions = {
      IconButton(onClick = onActionClick) {
        Icon(
          imageVector = actionIcon,
          contentDescription = actionIconContentDescription,
          tint = MaterialTheme.colorScheme.onSurface,
        )
      }
    },
    colors = colors,
    modifier = modifier.testTag("niaTopAppBar"),
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Top App Bar")
@Composable
private fun NiaTopAppBarPreview() {
  ExpeTopAppBar(
    titleRes = R.string.untitled,
    navigationIcon = ExpIcons.ArrowBack,
    navigationIconContentDescription = "Navigation icon",
    actionIcon = ExpIcons.Add,
    actionIconContentDescription = "Action icon",
  )
}
