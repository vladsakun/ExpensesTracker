package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import javax.inject.Inject

class GetCategorySnapshotByIdUseCase @Inject constructor(
  private val categoryRepository: CategoryRepository,
) {
  operator fun invoke(categoryId: Long): CategoryModel =
    categoryRepository.getCategoriesSnapshot().first { it.id == categoryId }
}