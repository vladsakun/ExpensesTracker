package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.core.domain.common.GetModelComponent
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryByIdUseCase @Inject constructor(
  private val categoryRepository: CategoryRepository,
) : GetModelComponent<CategoryModel> {

  override operator fun invoke(id: Long): Flow<CategoryModel> =
    categoryRepository.getById(id)
}