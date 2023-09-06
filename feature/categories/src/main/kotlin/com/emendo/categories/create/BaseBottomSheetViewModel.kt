package com.emendo.categories.create

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

abstract class BaseBottomSheetViewModel<BSType> : ViewModel() {

  protected val _bottomSheetState = MutableStateFlow<BSType?>(null)
  val bottomSheetState = _bottomSheetState.asStateFlow()

  protected val hideBottomSheetChannel = Channel<Unit>(Channel.CONFLATED)
  val hideBottomSheetEvent = hideBottomSheetChannel.receiveAsFlow()

  protected val navigateUpChannel = Channel<Unit>(Channel.CONFLATED)
  val navigateUpEvent = navigateUpChannel.receiveAsFlow()

  fun onDismissBottomSheetRequest() {
    hideBottomSheetChannel.trySend(Unit)
    _bottomSheetState.update { null }
  }
}