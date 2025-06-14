package com.emendo.expensestracker.core.app.common.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

inline fun <T> Flow<T>.collectWhenStarted(
  owner: LifecycleOwner,
  crossinline action: suspend CoroutineScope.(T) -> Unit,
) {
  owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      collect { action(it) }
    }
  }
}

inline fun <reified T : S, S> MutableStateFlow<S>.updateIfType(function: (T) -> T) {
  update {
    if (it is T) {
      return@update function(it)
    }

    it
  }
}

fun <T> Flow<T>.stateInWhileSubscribed(
  scope: CoroutineScope,
  initialValue: T,
  stopTimeoutMillis: Long = 5_000L,
  replayExpirationMillis: Long = 5_000L,
): StateFlow<T> {
  return stateIn(scope, SharingStarted.WhileSubscribed(stopTimeoutMillis, replayExpirationMillis), initialValue)
}

fun <T> Flow<T>.stateInLazily(
  scope: CoroutineScope,
  initialValue: T,
): StateFlow<T> {
  return stateIn(scope, SharingStarted.Lazily, initialValue)
}

fun <T> Flow<T>.stateInEagerly(
  scope: CoroutineScope,
  initialValue: T,
): StateFlow<T> {
  return stateIn(scope, SharingStarted.Eagerly, initialValue)
}

inline fun <reified T : List<*>> Flow<T>.stateInLazilyList(
  scope: CoroutineScope,
): StateFlow<T> {
  return stateInLazily(scope, emptyList<T>() as T)
}

inline fun <reified T : List<*>> Flow<T>.stateInEagerlyList(
  scope: CoroutineScope,
): StateFlow<T> {
  return stateInEagerly(scope, emptyList<T>() as T)
}

// Todo discover
/**
 * This resulting [Flow] has following properties:
 * - when there's more than one these Flows, reload trigger will be handled only once
 * - when there's no such Flow being collected, BE will be called later when there's a collector again
 */
fun <T> Flow<T>.enableReloadWhenSubscribed(vararg flow: Flow<*>): Flow<T> = channelFlow {
  flow.forEach { it.launchIn(this) }
  collect(::send)
}