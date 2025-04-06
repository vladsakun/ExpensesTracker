package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.database.dao.SubcategoryDao
import com.emendo.expensestracker.core.database.model.category.SubcategoryEntity
import com.emendo.expensestracker.data.api.repository.SubcategoryRepository
import javax.inject.Inject

class OfflineFirstSubcategoryRepository @Inject constructor(
  private val subcategoryDao: SubcategoryDao,
) : SubcategoryRepository {

  override suspend fun createSubcategory(name: String, icon: IconModel, categoryId: Long, ordinalIndex: Int) {
    subcategoryDao.save(
      SubcategoryEntity(
        categoryId = categoryId,
        name = name,
        iconId = icon.id,
        ordinalIndex = ordinalIndex
      ),
    )
  }
}