package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.repository.api.CategoryRepository
import javax.inject.Inject

class GetUserCreatedCategoriesSnapshotUseCase @Inject constructor(
  private val categoryRepository: CategoryRepository,
  private val isPrepopulatedCategoryUseCase: IsPrepopulatedCategoryUseCase,
) {
  operator fun invoke(): List<CategoryModel> =
    categoryRepository.getCategoriesSnapshot()
      .filterNot { isPrepopulatedCategoryUseCase(it.id) }
}