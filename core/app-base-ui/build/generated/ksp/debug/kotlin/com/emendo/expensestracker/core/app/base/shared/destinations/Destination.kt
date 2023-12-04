package com.emendo.expensestracker.core.app.base.shared.destinations

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.*

/**
 * Handy typealias of [AppbaseuiTypedDestination] when you don't
 * care about the generic type (probably most cases for app's use)
 */
public typealias AppbaseuiDestination = AppbaseuiTypedDestination<*>

/**
 * AppbaseuiTypedDestination is a sealed version of [DestinationSpec]
 */
public sealed interface AppbaseuiTypedDestination<T>: DestinationSpec<T>

/**
 * AppbaseuiDirectionDestination is a sealed version of [DirectionDestinationSpec]
 */
public sealed interface AppbaseuiDirectionDestination: AppbaseuiTypedDestination<Unit>, DirectionDestinationSpec

