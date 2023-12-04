package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.repository.api.CategoryRepository
import javax.inject.Inject

class GetCategorySnapshotByIdUseCase @Inject constructor(
  private val categoryRepository: CategoryRepository,
) {
  operator fun invoke(categoryId: Long): CategoryModel =
    categoryRepository.categoriesSnapshot.first { it.id == categoryId }
}