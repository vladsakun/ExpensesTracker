@file:OptIn(ExperimentalMaterial3Api::class)

package com.emendo.expensestracker.core.designsystem.component

import android.R
import androidx.annotation.StringRes
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeMediumTopBar(
  modifier: Modifier = Modifier,
  title: @Composable () -> Unit,
  navigationIcon: @Composable () -> Unit,
  actions: ImmutableList<MenuAction>? = null,
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
) {
  CenterAlignedTopAppBar(
    title = title,
    navigationIcon = navigationIcon,
    actions = {
      actions?.forEach { action ->
        IconButton(onClick = action.onClick) {
          Icon(
            imageVector = action.icon,
            contentDescription = action.text,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeTopBar(
  @StringRes titleRes: Int,
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit,
  actions: ImmutableList<MenuAction>? = null,
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
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
      actions?.forEach { action ->
        IconButton(onClick = action.onClick) {
          Icon(
            imageVector = action.icon,
            contentDescription = action.text,
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
  actions: ImmutableList<MenuAction>,
  modifier: Modifier = Modifier,
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
) {
  ExpeTopBar(
    titleRes = titleRes,
    actions = actions,
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
  actions: ImmutableList<MenuAction>? = null,
) {
  ExpeTopBar(
    titleRes = titleRes,
    colors = colors,
    modifier = modifier,
    scrollBehavior = scrollBehavior,
    navigationIcon = {},
    actions = actions,
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
  actions: ImmutableList<MenuAction>? = null,
) {
  ExpeTopBar(
    titleRes = titleRes,
    colors = colors,
    modifier = modifier,
    scrollBehavior = scrollBehavior,
    navigationIcon = { NavigationBackIcon(onNavigationClick = onNavigationBackClick) },
    actions = actions,
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
    actions = persistentListOf(
      MenuAction(
        icon = ExpeIcons.Add,
        text = "Action icon",
        onClick = {}
      )
    ),
    navigationIcon = {},
  )
}
