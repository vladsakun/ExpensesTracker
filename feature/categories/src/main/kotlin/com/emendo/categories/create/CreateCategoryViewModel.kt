package com.emendo.categories.create

import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.model.Category
import com.emendo.expensestracker.core.data.repository.CategoryRepository
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
  private val categoryRepository: CategoryRepository,
) : BaseBottomSheetViewModel<BottomSheetType>() {

  private val _state = MutableStateFlow(CreateCategoryScreenData.getDefault())
  val state = _state.asStateFlow()

  fun onTitleChanged(newTitle: String) {
    _state.update {
      it.copy(
        title = newTitle,
        isCreateButtonEnabled = newTitle.isNotBlank(),
      )
    }
  }

  fun onIconSelectClick() {
    updateBottomSheet(BottomSheetType.Icon(state.value.icon, ::onIconSelected))
  }

  fun onColorSelectClick() {
    updateBottomSheet(BottomSheetType.Color(state.value.color, ::onColorSelected))
  }

  fun onCreateCategoryClick() {
    viewModelScope.launch {
      categoryRepository.upsertCategory(
        Category(
          name = state.value.title,
          icon = state.value.icon,
          color = state.value.color,
        )
      )
      navigateUp()
    }
  }

  private fun onIconSelected(iconModel: IconModel) {
    _state.update { it.copy(icon = iconModel) }
  }

  private fun onColorSelected(colorModel: ColorModel) {
    _state.update { it.copy(color = colorModel) }
  }
}