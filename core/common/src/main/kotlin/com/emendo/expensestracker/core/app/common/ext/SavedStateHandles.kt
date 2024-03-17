package com.emendo.expensestracker.core.app.common.ext

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

fun <T> SavedStateHandle.stateFlow(initialValue: T):
  PropertyDelegateProvider<ViewModel, ReadOnlyProperty<ViewModel, MutableStateFlow<T>>> =
  SavedViewModelStateFlowProperty(this, initialValue)
