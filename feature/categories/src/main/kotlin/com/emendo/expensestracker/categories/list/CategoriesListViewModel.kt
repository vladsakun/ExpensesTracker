package com.emendo.expensestracker.categories.list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.categories.list.model.TabData
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.domain.category.GetUserCreatedCategoriesWithNotEmptyPrepopulatedUseCase
import com.emendo.expensestracker.core.model.data.CategoryWithOrdinalIndex
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManagerDelegate
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action.Companion.DangerAction
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.create.transaction.api.CreateTransactionScreenApi
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.category.CategoryType.Companion.label
import com.emendo.expensestracker.data.api.model.category.CategoryType.Companion.toCategoryType
import com.emendo.expensestracker.data.api.model.category.CategoryType.Companion.toPageIndex
import com.emendo.expensestracker.data.api.model.category.toCategoryWithOrdinalIndex
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import com.emendo.expensestracker.model.ui.resourceValueOf
import com.emendo.expensestracker.settings.SettingsScreenApi
import com.emendo.expensestracker.sync.initializers.Sync
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val CATEGORY_LIST_DELETE_CATEGORY_DIALOG = "category_list_delete_category_dialog"

@HiltViewModel
class CategoriesListViewModel @Inject constructor(
  private val appContext: Application,
  private val categoryRepository: CategoryRepository,
  getCategoriesUseCase: GetUserCreatedCategoriesWithNotEmptyPrepopulatedUseCase,
  @Dispatcher(ExpeDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
  private val createTransactionScreenApi: CreateTransactionScreenApi,
  private val settingsScreenApi: SettingsScreenApi,
) : ViewModel(), ModalBottomSheetStateManager by ModalBottomSheetStateManagerDelegate() {
  /**
   * Stores the latest version from db.
   * Because of equals in [CategoriesList] and [CategoryModel] [categoriesListUiState]
   * won't be notified about ordinalIndex change
   */
  private val categoriesListState: StateFlow<CategoriesListState> =
    categoriesUiState(getCategoriesUseCase)
      .stateInWhileSubscribed(
        scope = viewModelScope,
        initialValue = CategoriesListState.Empty,
      )

  /**
   * Mapping [categoriesListState] to [CategoriesListUiState] to prevent recomposition
   * @see equals in [CategoriesList] and [CategoryModel]
   */
  val categoriesListUiState: StateFlow<CategoriesListUiState> =
    categoriesListState
      .map(::toCategoriesListUiState)
      .stateInWhileSubscribed(
        scope = viewModelScope,
        initialValue = CategoriesListUiState.Empty,
      )

  private val _editMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val editMode: StateFlow<Boolean> = _editMode.asStateFlow()

  val isEditMode: Boolean
    get() = editMode.value
  val categoryType: CategoryType
    get() = selectedPageIndex.toCategoryType()

  private var selectedPageIndex = DEFAULT_PAGE_INDEX
  private var categoriesOrderedList: List<CategoryModel>? = null

  fun getCreateTransactionScreenRoute(category: CategoryModel): String =
    createTransactionScreenApi.getRoute(source = null, target = category)

  fun pageSelected(pageIndex: Int) {
    selectedPageIndex = pageIndex
  }

  fun invertEditMode() {
    _editMode.update { isEditMode ->
      if (isEditMode) {
        viewModelScope.launch {
          handleDragEvents(categoriesOrderedList)
        }
      }
      !isEditMode
    }
  }

  fun enableEditMode() {
    _editMode.update { true }
  }

  fun disableEditMode() {
    _editMode.update { false }
  }

  fun showConfirmDeleteCategoryBottomSheet(category: CategoryModel) {
    showModalBottomSheet(
      GeneralBottomSheetData
        .Builder(
          id = CATEGORY_LIST_DELETE_CATEGORY_DIALOG,
          positiveAction = DangerAction(resourceValueOf(R.string.delete)) { deleteCategory(category.id) },
        )
        .title(resourceValueOf(R.string.dialog_category_list_delete_confirm_title))
        .negativeAction(Action(resourceValueOf(R.string.cancel), ::hideModalBottomSheet))
        .build()
    )
  }

  fun onMove(newOrderedList: List<CategoryModel>) {
    categoriesOrderedList = newOrderedList
  }

  private fun deleteCategory(categoryId: Long) {
    hideModalBottomSheet()
    viewModelScope.launch {
      handleDragEvents(categoriesOrderedList)
      categoryRepository.deleteCategory(categoryId)
    }
  }

  private suspend fun handleDragEvents(eventsToHandle: List<CategoryModel>?) = withContext(defaultDispatcher) {
    if (eventsToHandle == null) {
      return@withContext
    }

    val categories: List<CategoryModel> =
      categoriesListState.value.successValue?.categories?.get(selectedPageIndex) ?: return@withContext
    val diff: MutableSet<CategoryWithOrdinalIndex> = mutableSetOf()

    categories.forEachIndexed { index, model ->
      val newOrderedCategoryByIndex = eventsToHandle[index]
      if (model.id != newOrderedCategoryByIndex.id) {
        diff.add(
          CategoryWithOrdinalIndex(
            id = newOrderedCategoryByIndex.id,
            ordinalIndex = model.ordinalIndex,
          )
        )
      }
    }

    val updateOperations = diff.map { update ->
      async {
        categoryRepository.updateOrdinalIndex(
          id = update.id,
          ordinalIndex = update.ordinalIndex,
        )
      }
    }

    updateOperations.awaitAll()

    categoriesOrderedList = null
  }

  private fun startReorderCategoriesWork() {
    val categoriesValue: List<CategoryModel> =
      categoriesListState.value.successValue?.categories?.get(selectedPageIndex) ?: return
    val eventsToHandle: List<Long> = categoriesOrderedList?.map { it.id } ?: return

    val categories = categoriesValue.map(::toCategoryWithOrdinalIndex)

    Sync.initializeReorderCategories(
      context = appContext,
      eventsToHandle = eventsToHandle,
      categories = categories,
    )
  }

  override fun onCleared() {
    startReorderCategoriesWork()
  }

  fun getSettingsRoute(): String = settingsScreenApi.getRoute()

  companion object {
    private val DEFAULT_PAGE_INDEX = CategoryType.EXPENSE.toPageIndex()
  }
}

// Todo move some parts to external Mapper
private fun categoriesUiState(
  getUserCreatedCategoriesWithNotEmptyPrepopulatedUseCase: GetUserCreatedCategoriesWithNotEmptyPrepopulatedUseCase,
): Flow<CategoriesListState> =
  getUserCreatedCategoriesWithNotEmptyPrepopulatedUseCase().asResult().map { categoriesResult ->
    when (categoriesResult) {
      is Result.Success -> {
        CategoriesListState.DisplayCategoriesList(
          tabs = persistentListOf(
            TabData(CategoryType.EXPENSE.label),
            TabData(CategoryType.INCOME.label),
          ),
          categories = mapOf(
            createCategoryPagePairState(CategoryType.EXPENSE, categoriesResult.data),
            createCategoryPagePairState(CategoryType.INCOME, categoriesResult.data),
          ),
        )
      }

      is Result.Error -> CategoriesListState.Error("No categories found ${categoriesResult.exception}")
      is Result.Loading -> CategoriesListState.Loading
      is Result.Idle -> CategoriesListState.Empty
    }
  }

private fun toCategoriesListUiState(state: CategoriesListState) =
  when (state) {
    is CategoriesListState.Empty -> CategoriesListUiState.Empty
    is CategoriesListState.DisplayCategoriesList -> CategoriesListUiState.DisplayCategoriesList(
      categories = persistentMapOf(
        createCategoryPagePair(CategoryType.EXPENSE, state.categories[CategoryType.EXPENSE.toPageIndex()]!!),
        createCategoryPagePair(CategoryType.INCOME, state.categories[CategoryType.INCOME.toPageIndex()]!!),
      ),
      tabs = state.tabs,
    )

    is CategoriesListState.Error -> CategoriesListUiState.Error(state.message)
    is CategoriesListState.Loading -> CategoriesListUiState.Loading
  }

private fun createCategoryPagePairState(
  categoryType: CategoryType,
  categoriesList: List<CategoryModel>,
): Pair<Int, List<CategoryModel>> =
  categoryType.toPageIndex() to categoriesList
    .filter { it.type == categoryType }
    .sortedBy { it.ordinalIndex }

private fun createCategoryPagePair(
  categoryType: CategoryType,
  categoriesList: List<CategoryModel>,
): Pair<Int, CategoriesList> =
  categoryType.toPageIndex() to CategoriesList(
    dataList = categoriesList
      .filter { it.type == categoryType }
      .sortedBy { it.ordinalIndex }
      .toImmutableList()
  )