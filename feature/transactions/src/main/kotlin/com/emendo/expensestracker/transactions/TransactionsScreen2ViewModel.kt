package com.emendo.expensestracker.transactions

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TransactionsScreen2ViewModel @Inject constructor() : ViewModel(), GeneralBottomSheetActions {
  val bottomSheetState = MutableStateFlow<TransactionsScreen2BottomSheets?>(null)

  private val hideBottomSheetEvent = Channel<Unit>(Channel.CONFLATED)
  val hideBottomSheetFlow = hideBottomSheetEvent.receiveAsFlow()

  fun onLogoutClick() {
    bottomSheetState.update { TransactionsScreen2BottomSheets.LogOutBottomSheet(actions = this) }
  }

  fun onDismissBottomSheet() {
    hideBottomSheet()
  }

  private fun hideBottomSheet() {
    hideBottomSheetEvent.trySend(Unit)
  }

  override fun onPositiveClick(bottomSheetType: Int) {
    if (bottomSheetType == 1) {
      Timber.d("Log out")
      hideBottomSheet()
      return
    }

    if (bottomSheetType == 2) {
      Timber.d("Cancel delete account")
      hideBottomSheet()
      return
    }
  }

  override fun onNegativeClick(bottomSheetType: Int) {
    if (bottomSheetType == 1) {
      bottomSheetState.update { TransactionsScreen2BottomSheets.ConfirmDeleteAccountBottomSheet(actions = this) }
      return
    }

    if (bottomSheetType == 2) {
      Timber.d("Delete account")
      hideBottomSheet()
      return
    }
  }

  override fun onNeutralClick(bottomSheetType: Int) {
    if (bottomSheetType == 2) {
      Timber.d("On neutral click")
      return
    }
  }

  fun onBottomSheetHidden() {
    bottomSheetState.value = null
  }
}