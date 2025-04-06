package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryWithTransactions
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUserCreatedCategoriesWithNotEmptyPrepopulatedUseCase @Inject constructor(
  private val categoryRepository: CategoryRepository,
  private val isPrepopulatedCategoryUseCase: IsPrepopulatedCategoryUseCase,
) {
  operator fun invoke(): Flow<List<CategoryModel>> =
    categoryRepository.getCategoriesWithTransactions().map { categories ->
      categories
        .filterNot(::isEmptyPrepopulatedCategory)
        .map { it.categoryModel }
    }

  private fun isEmptyPrepopulatedCategory(category: CategoryWithTransactions): Boolean =
    isPrepopulatedCategoryUseCase(category.categoryModel.id) && category.transactions.isEmpty()
}