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
import androidx.compose.ui.unit.sp
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeTopBar(
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
    title = {
      AutoResizedText(
        text = stringResource(id = titleRes),
        maxLines = 1,
        minFontSize = 14.sp,
      )
    },
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
    modifier = modifier.testTag("expeTopBar"),
    scrollBehavior = scrollBehavior,
  )
}

/**
 * Top  bar with action, displayed on the right
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeTopBar(
  @StringRes titleRes: Int,
  actionIcon: ImageVector,
  actionIconContentDescription: String,
  modifier: Modifier = Modifier,
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  onActionClick: () -> Unit = {},
) {
  ExpeTopBar(
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
 * Top  bar with no actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeTopBar(
  @StringRes titleRes: Int,
  modifier: Modifier = Modifier,
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
) {
  ExpeTopBar(
    titleRes = titleRes,
    colors = colors,
    modifier = modifier,
    scrollBehavior = scrollBehavior,
    navigationIcon = {},
  )
}

/**
 * Top  bar with back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeTopBar(
  @StringRes titleRes: Int,
  onNavigationBackClick: () -> Unit,
  modifier: Modifier = Modifier,
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
) {
  ExpeTopBar(
    titleRes = titleRes,
    colors = colors,
    modifier = modifier,
    scrollBehavior = scrollBehavior,
    navigationIcon = { NavigationBackIcon(onNavigationClick = onNavigationBackClick) },
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
      imageVector = ExpeIcons.ArrowBack,
      contentDescription = "navigate back",
      //      tint = MaterialTheme.colorScheme.onSurface,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Top  Bar")
@Composable
private fun NiaTopBarPreview() {
  ExpeTopBar(
    titleRes = R.string.untitled,
    navigationIcon = {},
    actionIcon = ExpeIcons.Add,
    actionIconContentDescription = "Action icon",
  )
}
