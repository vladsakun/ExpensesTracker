package com.emendo.expensestracker.core.app.common.ext

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlin.jvm.internal.CallableReference
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KDeclarationContainer
import kotlin.reflect.KProperty

internal fun <T> SavedViewModelStateFlowProperty(
  savedStateHandle: SavedStateHandle,
  initialValue: T,
): PropertyDelegateProvider<ViewModel, ReadOnlyProperty<ViewModel, MutableStateFlow<T>>> =
  UnsafeSavedStateFlowProperty(savedStateHandle, initialValue)

inline fun KProperty<*>.defaultDelegateName(customPrefix: String?, separator: String = "::") =
  (customPrefix ?: ownerCanonicalName)?.let { it + separator + name } ?: name

inline val KProperty<*>.ownerCanonicalName: String? get() = owner?.canonicalName
inline val KProperty<*>.owner: KDeclarationContainer? get() = if (this is CallableReference) owner else null
inline val KDeclarationContainer.canonicalName: String? get() = if (this is KClass<*>) this.java.canonicalName else null

internal abstract class SavedStateDelegateProvider<T, out P : ReadOnlyProperty<R, T>, R> :
  ReadOnlyProperty<R, T>, PropertyDelegateProvider<R, P> {

  protected lateinit var key: String
    private set

  override fun provideDelegate(thisRef: R, property: KProperty<*>): P = (this as P).apply {
    key = property.defaultDelegateName(null)
  }

}

private class UnsafeSavedStateFlowProperty<R, T>(
  private val savedStateHandle: SavedStateHandle,
  private val initialValue: T,
  private val scope: CoroutineScope? = null,
) : SavedStateDelegateProvider<MutableStateFlow<T>, ReadOnlyProperty<R, MutableStateFlow<T>>, R>() {
  private lateinit var stateFlow: MutableStateFlow<T>

  override fun provideDelegate(thisRef: R, property: KProperty<*>): ReadOnlyProperty<R, MutableStateFlow<T>> =
    super.provideDelegate(thisRef, property).also {
      val scope = if (scope == null && thisRef is ViewModel) {
        thisRef.viewModelScope
      } else {
        requireNotNull(scope)
      }
      stateFlow = savedStateHandle.getStateFlow(scope, key, initialValue)
    }

  override fun getValue(thisRef: R, property: KProperty<*>): MutableStateFlow<T> = stateFlow
}

private fun <T> SavedStateHandle.getStateFlow(
  scope: CoroutineScope,
  key: String,
  initialValue: T,
): MutableStateFlow<T> {
  val currentValue = get(key) ?: initialValue
  val stateFlow = MutableStateFlow(currentValue)
  stateFlow
    .onEach {
      withContext(Dispatchers.Main.immediate) {
        set(key, it)
      }
    }
    .launchIn(scope)
  return stateFlow
}
