package com.emendo.expensestracker.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.model.data.CategoryWithOrdinalIndex
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import com.emendo.expensestracker.sync.initializers.syncForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

@HiltWorker
class ReorderCategoriesWorker @AssistedInject constructor(
  @Assisted private val appContext: Context,
  @Assisted private val workerParams: WorkerParameters,
  private val categoryRepository: CategoryRepository,
  @Dispatcher(ExpeDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
) : CoroutineWorker(appContext, workerParams) {

  private val newOrderedCategoriesIdsArg: LongArray?
    get() = workerParams.inputData.getLongArray(KEY_NEW_ORDERED_CATEGORIES_IDS)
  private val oldCategoriesArg: List<CategoryWithOrdinalIndex>?
    get() = workerParams.inputData.getString(KEY_OLD_CATEGORIES)?.let(Json::decodeFromString)

  override suspend fun getForegroundInfo(): ForegroundInfo =
    appContext.syncForegroundInfo()

  override suspend fun doWork(): Result = withContext(defaultDispatcher) {
    val newOrderedCategoriesIds = newOrderedCategoriesIdsArg ?: return@withContext Result.success()
    val oldCategories = oldCategoriesArg ?: return@withContext Result.success()

    val diff: MutableSet<CategoryWithOrdinalIndex> = mutableSetOf()

    oldCategories.forEachIndexed { index, model ->
      val newOrderedCategoryId = newOrderedCategoriesIds[index]
      if (model.id != newOrderedCategoryId) {
        diff.add(
          CategoryWithOrdinalIndex(
            id = newOrderedCategoryId,
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

    Result.success()
  }

  companion object {
    const val KEY_NEW_ORDERED_CATEGORIES_IDS = "key_new_ordered_categories_ids"
    const val KEY_OLD_CATEGORIES = "key_old_categories"

    /**
     * Expedited one time work to reorder categories
     */
    fun startUpReorderCategoriesWorker(
      newOrderedCategoriesIds: List<Long>,
      oldCategories: List<CategoryWithOrdinalIndex>,
    ) = OneTimeWorkRequestBuilder<DelegatingWorker>()
      .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
      .setInputData(
        ReorderCategoriesWorker::class.delegatedDataReorderCategories(
          newOrderedCategoriesIds,
          oldCategories,
        )
      )
      .build()
  }
}