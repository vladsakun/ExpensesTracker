package com.emendo.expensestracker.core.data.repository.api

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.model.category.CategoryWithTransactions
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
  val categories: Flow<List<CategoryModel>>
  val categoriesWithTransactions: Flow<List<CategoryWithTransactions>>

  val categoriesSnapshot: List<CategoryModel>

  suspend fun createCategory(
    name: String,
    icon: IconModel,
    color: ColorModel,
    type: CategoryType,
  )

  suspend fun updateCategory(
    id: Long,
    name: String,
    icon: IconModel,
    color: ColorModel,
    type: CategoryType,
  )

  suspend fun updateOrdinalIndex(
    id: Long,
    ordinalIndex: Int,
  )

  suspend fun deleteCategory(id: Long)
}