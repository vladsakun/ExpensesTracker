package com.emendo.expensestracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.emendo.accounts.destinations.CreateAccountScreenDestination
import com.emendo.categories.destinations.CreateCategoryScreenDestination
import com.emendo.expensestracker.core.app.common.result.TopAppBarActionClickEventBus
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBar
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBarItem
import com.emendo.expensestracker.core.designsystem.component.ExpeTopAppBar
import com.emendo.expensestracker.feature.transactions.R
import com.emendo.expensestracker.navigation.ExpeNavHost
import com.emendo.expensestracker.navigation.TopLevelDestination
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.emendo.expensestracker.R as AppR

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ExpeApp(
  windowSizeClass: WindowSizeClass,
  appState: ExpeAppState = rememberExpeAppState(
    windowSizeClass = windowSizeClass
  ),
  topAppBarActionClickEventBus: TopAppBarActionClickEventBus
) {

  var showBottomSheet by remember { mutableStateOf(false) }
  val sheetState = androidx.compose.material.rememberModalBottomSheetState(
    initialValue = ModalBottomSheetValue.Hidden,
    skipHalfExpanded = true,
  )

  ModalBottomSheetLayout(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(
        WindowInsets.safeDrawing.only(
          WindowInsetsSides.Horizontal,
        ),
      ),
    sheetState = sheetState,
    sheetContent = {
      AddAccountSheet(
        onHideBottomSheet = { showBottomSheet = false }
      )
    },
  ) {
    Scaffold(
      containerColor = Color.Transparent,
      contentColor = MaterialTheme.colorScheme.onBackground,
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      bottomBar = {
        if (appState.shouldShowBottomBar) {
          ExpeBottomBar(
            destinations = appState.topLevelDestination,
            onNavigateToDestination = appState::navigateToTopLevelDestination,
            currentDestination = appState.currentDestination,
          )
        }
      },
    ) { padding ->
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .consumeWindowInsets(padding)
          .windowInsetsPadding(
            WindowInsets.safeDrawing.only(
              WindowInsetsSides.Horizontal,
            ),
          ),
      ) {
        if (appState.shouldShowNavRail) {
          // Todo
        }

        Column(modifier = Modifier.fillMaxSize()) {

          val destination = appState.currentTopLevelDestination
          if (destination != null) {
            ExpeTopAppBar(
              titleRes = appState.currentTopLevelDestination?.titleTextId ?: AppR.string.app_name,
              navigationIcon = null,
              navigationIconContentDescription = stringResource(
                id = R.string.transactions,
              ),
              actionIcon = getTopAppBarActionIcon(appState.currentComposableDestination),
              actionIconContentDescription = stringResource(
                id = R.string.transactions,
              ),
              onActionClick = {
                topAppBarActionClickEventBus.actionClicked()
              },
              onNavigationClick = { },
            )
          }
          ExpeNavHost(
            appState = appState,
            onShowSnackbar = { message, action ->
              return@ExpeNavHost true
            }
          )
        }
      }
    }
  }
}

@Composable
private fun getTopAppBarActionIcon(destinationSpec: DestinationSpec<*>?): ImageVector? {
  return null
}

@Composable
private fun AddAccountSheet(
  onHideBottomSheet: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Red)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .safeDrawingPadding(),
      verticalArrangement = Arrangement.Top
    ) {
      // Sheet content
      Button(onClick = onHideBottomSheet) {
        Text("Hide bottom sheet")
      }
    }
  }
}

@Composable
private fun ExpeBottomBar(
  destinations: List<TopLevelDestination>,
  onNavigateToDestination: (TopLevelDestination) -> Unit,
  currentDestination: NavDestination?,
  modifier: Modifier = Modifier
) {
  ExpeNavigationBar(
    modifier = modifier,
  ) {
    destinations.forEach { destination ->
      val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
      ExpeNavigationBarItem(
        selected = selected,
        onClick = { onNavigateToDestination(destination) },
        icon = {
          Icon(
            imageVector = destination.unselectedIcon,
            contentDescription = null,
          )
        },
        selectedIcon = {
          Icon(
            imageVector = destination.selectedIcon,
            contentDescription = null,
          )
        },
        label = { Text(text = stringResource(destination.iconTextId)) }
      )
    }
  }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination): Boolean {
  return this?.hierarchy?.any {
    it.route?.contains(destination.name, true) ?: false
  } ?: false
}