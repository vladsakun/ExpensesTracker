package com.emendo.transactions

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.emendo.expensestracker.core.data.model.ColorModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TransactionsScreenViewModel @Inject constructor() : ViewModel() {

  private val _state = MutableStateFlow(TransactionsScreenData.getDefaultState())
  val state: StateFlow<TransactionsScreenData> = _state

  fun nameChanged(name: String) {
    _state.value = _state.value.copy(accountName = name)
  }

  fun onColorChange(){
    _state.update { _state.value.copy(color = ColorModel.GENOE) }
  }
}