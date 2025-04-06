package com.emendo.expensestracker.categories.subcategory

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.emendo.expensestracker.app.base.api.screens.SelectIconScreenApi
import com.emendo.expensestracker.categories.destinations.CreateSubcategoryRouteDestination
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class CreateSubcategoryViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val selectIconScreenApi: SelectIconScreenApi,
) : ViewModel(), CreateSubcategoryCommander {

  private val colorModel by lazy(LazyThreadSafetyMode.NONE) {
    ColorModel.getById(CreateSubcategoryRouteDestination.argsFrom(savedStateHandle).colorId)
  }

  private val _state = MutableStateFlow(CreateSubcategoryUiState.getDefault(colorModel))
  internal val state: StateFlow<CreateSubcategoryUiState> = _state

  override fun changeTitle(newTitle: String) {
    _state.update {
      it.copy(
        title = newTitle,
        confirmButtonEnabled = newTitle.isNotBlank(),
      )
    }
  }

  internal fun getSelectIconScreenRoute(): String = selectIconScreenApi.getSelectIconScreenRoute(state.value.icon.id)

  internal fun updateIcon(newIconId: Int) {
    _state.update { it.copy(icon = IconModel.getById(newIconId)) }
  }

  internal fun getResult() = CreateSubcategoryResult(state.value.title, state.value.icon.id)
}

@Parcelize
data class CreateSubcategoryResult(
  val title: String,
  val iconId: Int,
) : Parcelable