package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import javax.inject.Inject

class GetUserCreatedCategoriesSnapshotUseCase @Inject constructor(
  private val categoryRepository: CategoryRepository,
  private val isPrepopulatedCategoryUseCase: IsPrepopulatedCategoryUseCase,
) {
  operator fun invoke(): List<CategoryModel> =
    categoryRepository.getCategoriesSnapshot()
      .filterNot { isPrepopulatedCategoryUseCase(it.id) }
}