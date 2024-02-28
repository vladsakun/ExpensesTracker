package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.core.domain.common.GetModelComponent
import com.emendo.expensestracker.core.domain.common.GetModelDecorator
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetCategorySnapshotByIdUseCase @Inject constructor(
  private val categoryRepository: CategoryRepository,
  override val getModelComponent: GetModelComponent<CategoryModel>,
) : GetModelDecorator<CategoryModel> {

  override operator fun invoke(id: Long): Flow<CategoryModel> {
    val categorySnapshot = getCategorySnapshot(id) ?: return getModelComponent(id)
    return flowOf(categorySnapshot)
  }

  private fun getCategorySnapshot(categoryId: Long): CategoryModel? =
    categoryRepository.getCategoriesSnapshot().firstOrNull { it.id == categoryId }
}