package com.emendo.expensestracker.categories.list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.categories.list.model.CategoryWithTotal
import com.emendo.expensestracker.categories.list.model.TabData
import com.emendo.expensestracker.categories.list.model.toCategoryWithTotal
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEvent
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.resourceValueOf
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.model.category.CategoryType.Companion.label
import com.emendo.expensestracker.core.data.model.category.CategoryType.Companion.toCategoryType
import com.emendo.expensestracker.core.data.model.category.CategoryType.Companion.toPageIndex
import com.emendo.expensestracker.core.data.model.category.CategoryWithTotalTransactions
import com.emendo.expensestracker.core.data.model.category.toCategoryWithOrdinalIndex
import com.emendo.expensestracker.core.data.repository.api.CategoryRepository
import com.emendo.expensestracker.core.domain.category.GetCategoriesWithTotalTransactionsUseCase
import com.emendo.expensestracker.core.model.data.CategoryWithOrdinalIndex
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetStateManagerDelegate
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action.Companion.DangerAction
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.sync.initializers.Sync
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CategoriesListViewModel @Inject constructor(
  getCategoriesWithTotalTransactionsUseCase: GetCategoriesWithTotalTransactionsUseCase,
  private val appContext: Application,
  private val appNavigationEventBus: AppNavigationEventBus,
  private val categoryRepository: CategoryRepository,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  @Dispatcher(ExpeDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel(), BottomSheetStateManager by BottomSheetStateManagerDelegate() {

  /**
   * Stores the latest version from db.
   * Because of equals in [CategoriesList] and [CategoryWithTotal] [categoriesListUiState]
   * won't be notified about ordinalIndex change
   */
  private val categoriesListState: StateFlow<CategoriesListState> =
    categoriesUiState(getCategoriesWithTotalTransactionsUseCase)
      .stateInWhileSubscribed(
        scope = viewModelScope,
        initialValue = CategoriesListState.Empty,
      )

  /**
   * Mapping [categoriesListState] to [CategoriesListUiState] to prevent recomposition
   * @see equals in [CategoriesList] and [CategoryWithTotal]
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
  private var categoriesOrderedList: List<CategoryWithTotal>? = null

  fun openCreateTransactionScreen(category: CategoryWithTotal) {
    appNavigationEventBus.navigate(
      AppNavigationEvent.CreateTransaction(
        source = null,
        target = category.category,
        shouldNavigateUp = true
      )
    )
  }

  fun pageSelected(pageIndex: Int) {
    selectedPageIndex = pageIndex
  }

  fun inverseEditMode() {
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

  fun showConfirmDeleteCategoryBottomSheet(category: CategoryWithTotal) {
    showBottomSheet(
      GeneralBottomSheetData
        .Builder(DangerAction(resourceValueOf(R.string.delete)) { deleteCategory(category.category.id) })
        .title(resourceValueOf(R.string.category_list_dialog_delete_confirm_title))
        .negativeAction(Action(resourceValueOf(R.string.cancel), ::hideBottomSheet))
        .build()
    )
  }

  fun onMove(newOrderedList: List<CategoryWithTotal>) {
    categoriesOrderedList = newOrderedList
  }

  private fun deleteCategory(categoryId: Long) {
    hideBottomSheet()
    viewModelScope.launch(ioDispatcher) {
      handleDragEvents(categoriesOrderedList)
      categoryRepository.deleteCategory(categoryId)
    }
  }

  private suspend fun handleDragEvents(eventsToHandle: List<CategoryWithTotal>?) = withContext(defaultDispatcher) {
    if (eventsToHandle == null) {
      return@withContext
    }

    val categories: List<CategoryWithTotalTransactions> =
      categoriesListState.value.successValue?.categories?.get(selectedPageIndex) ?: return@withContext
    val diff: MutableSet<CategoryWithOrdinalIndex> = mutableSetOf()

    categories.forEachIndexed { index, model ->
      val newOrderedCategoryByIndex = eventsToHandle[index]
      if (model.categoryModel.id != newOrderedCategoryByIndex.category.id) {
        diff.add(
          CategoryWithOrdinalIndex(
            id = newOrderedCategoryByIndex.category.id,
            ordinalIndex = model.categoryModel.ordinalIndex,
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
    val categoriesValue: List<CategoryWithTotalTransactions> =
      categoriesListState.value.successValue?.categories?.get(selectedPageIndex) ?: return
    val eventsToHandle: List<Long> = categoriesOrderedList?.map { it.category.id } ?: return

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

  companion object {
    private val DEFAULT_PAGE_INDEX = CategoryType.EXPENSE.toPageIndex()
  }
}

// Todo move some parts to external Mapper
private fun categoriesUiState(
  getCategoriesWithTotalTransactionsUseCase: GetCategoriesWithTotalTransactionsUseCase,
): Flow<CategoriesListState> =
  getCategoriesWithTotalTransactionsUseCase().asResult().map { categoriesResult ->
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
      is Result.Empty -> CategoriesListState.Empty
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
  categoriesList: List<CategoryWithTotalTransactions>,
): Pair<Int, List<CategoryWithTotalTransactions>> =
  categoryType.toPageIndex() to categoriesList
    .filter { it.categoryModel.type == categoryType }
    .sortedBy { it.categoryModel.ordinalIndex }

private fun createCategoryPagePair(
  categoryType: CategoryType,
  categoriesList: List<CategoryWithTotalTransactions>,
): Pair<Int, CategoriesList> =
  categoryType.toPageIndex() to CategoriesList(
    dataList = categoriesList
      .filter { it.categoryModel.type == categoryType }
      .map(::toCategoryWithTotal)
      .sortedBy { it.category.ordinalIndex }
      .toImmutableList()
  )