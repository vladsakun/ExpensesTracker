package com.emendo.expensestracker.categories.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.app.base.api.screens.SelectColorScreenApi
import com.emendo.expensestracker.app.base.api.screens.SelectIconScreenApi
import com.emendo.expensestracker.categories.common.CategoryScreenData
import com.emendo.expensestracker.categories.common.CategoryViewModel
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.domain.category.CreateCategoryWithSubcategoriesUseCase
import com.emendo.expensestracker.core.domain.model.SubcategoryCreateModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.model.ui.*
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  override val selectIconScreenApi: SelectIconScreenApi,
  override val selectColorScreenApi: SelectColorScreenApi,
  private val createCategoryWithSubcategoriesUseCase: CreateCategoryWithSubcategoriesUseCase,
) : CategoryViewModel<CategoryCreateScreenData>(UiState.Data(getDefault())), CategoryCreateCommandReceiver {

  private var createCategoryJob: Job? = null
  private val categoryType: CategoryType = CreateCategoryRouteDestination.argsFrom(savedStateHandle).categoryType

  override fun consumeNavigateUpEvent() {
    _state.updateData {
      it.copy(navigateUpEvent = consumed)
    }
  }

  override fun createCategory() {
    if (createCategoryJob != null) {
      return
    }

    createCategoryJob = viewModelScope.launch {
      with(state.value.requireDataValue().categoryScreenData) {
        createCategoryWithSubcategoriesUseCase(
          name = title.textValueOrBlank(),
          icon = icon,
          color = color,
          type = categoryType,
          // TODO get rid of mapping
          subcategories = subcategories.mapIndexed { index, subcategory ->
            SubcategoryCreateModel(
              icon = subcategory.icon,
              name = subcategory.name,
              ordinalIndex = index,
            )
          }
        )
      }
      _state.updateData {
        it.copy(navigateUpEvent = triggered)
      }
    }
  }
}

private fun getDefault() =
  CategoryCreateScreenData(
    categoryScreenData = CategoryScreenData(
      title = textValueOf(""),
      icon = IconModel.random,
      color = ColorModel.random,
      confirmButtonEnabled = false,
      subcategories = persistentListOf(),
    )
  )