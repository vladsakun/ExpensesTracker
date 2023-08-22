package com.emendo.expensestracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.emendo.expensestracker.ui.ExpeAppState
import com.ramcosta.composedestinations.DestinationsNavHost

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExpeNavHost(
  appState: ExpeAppState,
  onShowSnackbar: suspend (String, String?) -> Boolean,
  modifier: Modifier = Modifier,
) {
  val navController = appState.navController

  DestinationsNavHost(
    navGraph = RootNavGraph,
    navController = navController,
    startRoute = RootNavGraph.startRoute,
    modifier = modifier,
  )
}