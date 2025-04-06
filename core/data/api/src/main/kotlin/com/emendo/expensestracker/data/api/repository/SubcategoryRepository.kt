package com.emendo.expensestracker.data.api.repository

import com.emendo.expensestracker.core.app.resources.models.IconModel

interface SubcategoryRepository {

  suspend fun createSubcategory(
    name: String,
    icon: IconModel,
    categoryId: Long,
    ordinalIndex: Int,
  )
}