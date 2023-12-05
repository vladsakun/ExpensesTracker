package com.emendo.expensestracker.core.ui.bottomsheet

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle

object BottomSheetTransition : DestinationStyle.Animated {
  private const val DURATION_MILLIS = 300

  override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition? {
    return slideIntoContainer(
      towards = AnimatedContentTransitionScope.SlideDirection.Up,
      animationSpec = tween(DURATION_MILLIS),
    )
  }

  override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition? {
    return slideOutOfContainer(
      towards = AnimatedContentTransitionScope.SlideDirection.Up,
      animationSpec = tween(DURATION_MILLIS),
    )
  }

  override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {
    return slideIntoContainer(
      towards = AnimatedContentTransitionScope.SlideDirection.Down,
      animationSpec = tween(DURATION_MILLIS),
    )
  }

  override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {
    return slideOutOfContainer(
      towards = AnimatedContentTransitionScope.SlideDirection.Down,
      animationSpec = tween(DURATION_MILLIS),
    )
  }
}