@file:OptIn(ExperimentalMaterial3Api::class)

package com.emendo.expensestracker.core.designsystem.component

//noinspection SuspiciousImport
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
fun ExpeCenterAlignedTopBar(
  title: @Composable () -> Unit,
  navigationIcon: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
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
            contentDescription = action.contentDescription,
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
fun ExpeCenterAlignedTopBar(
  title: String,
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit = {},
  actions: ImmutableList<MenuAction>? = null,
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
) {
  CenterAlignedTopAppBar(
    title = {
      AutoResizedText(
        text = title,
        maxLines = 1,
        minFontSize = 14.sp,
      )
    },
    navigationIcon = navigationIcon,
    actions = {
      actions?.forEach { action ->
        ExpeToolbarIcon(action)
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
  title: String,
  modifier: Modifier = Modifier,
  navigationIcon: (@Composable () -> Unit)? = null,
  actions: ImmutableList<MenuAction>? = null,
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
) {
  LargeTopAppBar(
    title = {
      AutoResizedText(
        text = title,
        maxLines = 1,
        minFontSize = 14.sp,
      )
    },
    navigationIcon = navigationIcon ?: {},
    actions = {
      actions?.forEach { action ->
        ExpeToolbarIcon(action)
      }
    },
    colors = colors,
    modifier = modifier.testTag("expeTopBar"),
    scrollBehavior = scrollBehavior,
  )
}

/**
 * Top  bar with back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeTopBar(
  @StringRes titleResId: Int,
  modifier: Modifier = Modifier,
  onNavigationBackClick: (() -> Unit)? = null,
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
  actions: ImmutableList<MenuAction>? = null,
) {
  ExpeTopBar(
    title = stringResource(titleResId),
    modifier = modifier,
    onNavigationBackClick = onNavigationBackClick,
    colors = colors,
    scrollBehavior = scrollBehavior,
    actions = actions,
  )
}

/**
 * Top  bar with back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeTopBar(
  title: String,
  modifier: Modifier = Modifier,
  onNavigationBackClick: (() -> Unit)? = null,
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
  actions: ImmutableList<MenuAction>? = null,
) {
  ExpeTopBar(
    title = title,
    colors = colors,
    modifier = modifier,
    scrollBehavior = scrollBehavior,
    navigationIcon = onNavigationBackClick?.let { { NavigationBackIcon(onNavigationClick = it) } },
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

@Composable
fun ExpeToolbarIcon(action: MenuAction) {
  IconButton(
    onClick = action.onClick,
    enabled = action.enabled,
  ) {
    Icon(
      imageVector = action.icon,
      contentDescription = action.contentDescription,
      tint = MaterialTheme.colorScheme.onSurface,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Top  Bar")
@Composable
private fun NiaTopBarPreview() {
  ExpeTopBar(
    title = stringResource(id = R.string.untitled),
    actions = persistentListOf(
      MenuAction(
        icon = ExpeIcons.Add,
        contentDescription = "Action icon",
        onClick = {}
      )
    ),
    navigationIcon = {},
  )
}
