package com.emendo.expensestracker.core.ui.bottomsheet

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle

object BottomScreenTransition : DestinationStyle.Animated {
  private const val DURATION_MILLIS = 300

  override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition =
    slideIntoContainer(
      towards = AnimatedContentTransitionScope.SlideDirection.Up,
      animationSpec = tween(DURATION_MILLIS),
    )

  override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition? = null

  override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? = null

  override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition =
    slideOutOfContainer(
      towards = AnimatedContentTransitionScope.SlideDirection.Down,
      animationSpec = tween(DURATION_MILLIS),
    )
}