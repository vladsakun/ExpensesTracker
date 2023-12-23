package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.core.data.model.category.CategoryWithTransactions
import com.emendo.expensestracker.core.data.repository.api.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Todo rename
class GetUserCreateCategoriesWithNotEmptyPrepopulatedUseCase @Inject constructor(
  private val categoryRepository: CategoryRepository,
  private val isPrepopulatedCategoryUseCase: IsPrepopulatedCategoryUseCase,
) {
  operator fun invoke(): Flow<List<CategoryWithTransactions>> =
    categoryRepository.getCategoriesWithTransactions().map { it.filterNot(::isEmptyPrepopulatedCategory) }

  private fun isEmptyPrepopulatedCategory(category: CategoryWithTransactions): Boolean =
    isPrepopulatedCategoryUseCase(category.categoryModel.id) && category.transactions.isEmpty()
}