package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.domain.model.SubcategoryEditModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import com.emendo.expensestracker.data.api.repository.SubcategoryRepository
import com.emendo.expensestracker.model.ui.ColorModel
import javax.inject.Inject

class UpdateCategoryWithSubcategoriesUseCase @Inject constructor(
  private val categoryRepository: CategoryRepository,
  private val subcategoryRepository: SubcategoryRepository,
) {

  suspend operator fun invoke(
    id: Long,
    name: String,
    icon: IconModel,
    color: ColorModel,
    type: CategoryType,
    subcategories: List<SubcategoryEditModel>,
  ) {
    // TODO in transaction (maybe async transaction)
    categoryRepository.updateCategory(id, name, icon, color, type)
    subcategories.forEachIndexed { index, subcategory ->
      if (subcategory.id == null) {
        subcategoryRepository.createSubcategory(
          name = subcategory.name,
          icon = subcategory.icon,
          categoryId = id,
          ordinalIndex = index,
        )
        return@forEachIndexed
      }

      subcategoryRepository.updateSubcategory(
        id = subcategory.id,
        name = subcategory.name,
        icon = subcategory.icon,
        categoryId = id,
        ordinalIndex = index,
      )
    }
  }
}