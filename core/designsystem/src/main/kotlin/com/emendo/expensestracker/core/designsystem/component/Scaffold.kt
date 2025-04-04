package com.emendo.expensestracker.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ExpeScaffold(
  modifier: Modifier = Modifier,
  topBar: @Composable () -> Unit = {},
  bottomBar: @Composable () -> Unit = {},
  snackbarHost: @Composable () -> Unit = {},
  floatingActionButton: @Composable () -> Unit = {},
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  containerColor: Color = MaterialTheme.colorScheme.background,
  contentColor: Color = contentColorFor(containerColor),
  content: @Composable (PaddingValues) -> Unit,
) {
  Scaffold(
    modifier = modifier,
    topBar = topBar,
    bottomBar = bottomBar,
    snackbarHost = snackbarHost,
    floatingActionButton = floatingActionButton,
    floatingActionButtonPosition = floatingActionButtonPosition,
    containerColor = containerColor,
    contentColor = contentColor,
    content = content,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeScaffoldWithTopBar(
  @StringRes titleResId: Int,
  modifier: Modifier = Modifier,
  onNavigationClick: (() -> Unit)? = null,
  actions: ImmutableList<MenuAction>? = null,
  snackbarHost: @Composable () -> Unit = {},
  floatingActionButton: @Composable () -> Unit = {},
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  containerColor: Color = MaterialTheme.colorScheme.background,
  contentColor: Color = contentColorFor(containerColor),
  content: @Composable (PaddingValues) -> Unit,
) {
  val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

  ExpeScaffold(
    topBar = {
      ExpeTopBar(
        titleResId = titleResId,
        onNavigationBackClick = onNavigationClick,
        scrollBehavior = topAppBarScrollBehavior,
        actions = actions,
      )
    },
    modifier = modifier
      .fillMaxSize()
      .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
    snackbarHost = snackbarHost,
    floatingActionButton = floatingActionButton,
    floatingActionButtonPosition = floatingActionButtonPosition,
    containerColor = containerColor,
    contentColor = contentColor,
    content = content,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeScaffoldWithTopBar(
  title: String,
  modifier: Modifier = Modifier,
  onNavigationClick: (() -> Unit)? = null,
  actions: ImmutableList<MenuAction>? = null,
  snackbarHost: @Composable () -> Unit = {},
  floatingActionButton: @Composable () -> Unit = {},
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  containerColor: Color = MaterialTheme.colorScheme.background,
  contentColor: Color = contentColorFor(containerColor),
  content: @Composable (PaddingValues) -> Unit,
) {
  val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

  ExpeScaffold(
    topBar = {
      ExpeTopBar(
        title = title,
        onNavigationBackClick = onNavigationClick,
        scrollBehavior = topAppBarScrollBehavior,
        actions = actions,
      )
    },
    modifier = modifier
      .fillMaxSize()
      .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
    snackbarHost = snackbarHost,
    floatingActionButton = floatingActionButton,
    floatingActionButtonPosition = floatingActionButtonPosition,
    containerColor = containerColor,
    contentColor = contentColor,
    content = content,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeScaffoldWithTopBar(
  @StringRes titleResId: Int,
  modifier: Modifier = Modifier,
  actions: ImmutableList<MenuAction>? = null,
  snackbarHost: @Composable () -> Unit = {},
  floatingActionButton: @Composable () -> Unit = {},
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  containerColor: Color = MaterialTheme.colorScheme.background,
  contentColor: Color = contentColorFor(containerColor),
  content: @Composable (PaddingValues) -> Unit,
) {
  val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

  ExpeScaffold(
    topBar = {
      ExpeTopBar(
        titleResId = titleResId,
        scrollBehavior = topAppBarScrollBehavior,
        actions = actions,
      )
    },
    modifier = modifier
      .fillMaxSize()
      .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
    snackbarHost = snackbarHost,
    floatingActionButton = floatingActionButton,
    floatingActionButtonPosition = floatingActionButtonPosition,
    containerColor = containerColor,
    contentColor = contentColor,
    content = content,
  )
}