package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUserCreatedCategoriesUseCase @Inject constructor(
  private val categoryRepository: CategoryRepository,
  private val isPrepopulatedCategoryUseCase: IsPrepopulatedCategoryUseCase,
) {
  operator fun invoke(): Flow<List<CategoryModel>> =
    categoryRepository
      .getCategories()
      .map { categories -> categories.filterNot { isPrepopulatedCategoryUseCase(it.id) } }
}