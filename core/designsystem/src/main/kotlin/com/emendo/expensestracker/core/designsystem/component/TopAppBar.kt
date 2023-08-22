@file:OptIn(ExperimentalMaterial3Api::class)

package com.emendo.expensestracker.core.designsystem.component

import android.R
import androidx.annotation.StringRes
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
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit,
  actionIcon: ImageVector? = null,
  actionIconContentDescription: String? = null,
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  onActionClick: () -> Unit = {},
  scrollBehavior: TopAppBarScrollBehavior? = null,
) {
  LargeTopAppBar(
    title = { Text(text = stringResource(id = titleRes)) },
    navigationIcon = navigationIcon,
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
    scrollBehavior = scrollBehavior,
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
  actionIconContentDescription: String,
  modifier: Modifier = Modifier,
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  onActionClick: () -> Unit = {},
) {
  ExpeTopAppBar(
    titleRes = titleRes,
    actionIcon = actionIcon,
    actionIconContentDescription = actionIconContentDescription,
    onActionClick = onActionClick,
    colors = colors,
    modifier = modifier,
    navigationIcon = {},
  )
}

/**
 * Top app bar with no actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeTopAppBar(
  @StringRes titleRes: Int,
  modifier: Modifier = Modifier,
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
) {
  ExpeTopAppBar(
    titleRes = titleRes,
    colors = colors,
    modifier = modifier,
    scrollBehavior = scrollBehavior,
    navigationIcon = {},
  )
}

@Composable
fun NavigationBackIcon(
  onNavigationClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  IconButton(
    onClick = onNavigationClick,
    modifier = modifier,
  ) {
    Icon(
      imageVector = ExpIcons.ArrowBack,
      contentDescription = "navigate back",
      //      tint = MaterialTheme.colorScheme.onSurface,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Top App Bar")
@Composable
private fun NiaTopAppBarPreview() {
  ExpeTopAppBar(
    titleRes = R.string.untitled,
    navigationIcon = {},
    actionIcon = ExpIcons.Add,
    actionIconContentDescription = "Action icon",
  )
}
