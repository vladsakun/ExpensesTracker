package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.domain.model.SubcategoryCreateModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import com.emendo.expensestracker.data.api.repository.SubcategoryRepository
import com.emendo.expensestracker.model.ui.ColorModel
import javax.inject.Inject

class CreateCategoryWithSubcategoriesUseCase @Inject constructor(
  private val categoryRepository: CategoryRepository,
  private val subcategoryRepository: SubcategoryRepository,
) {

  suspend operator fun invoke(
    name: String,
    icon: IconModel,
    color: ColorModel,
    type: CategoryType,
    subcategories: List<SubcategoryCreateModel>,
  ) {
    val categoryId = categoryRepository.createCategory(name, icon, color, type)
    subcategories.forEachIndexed { index, subcategory ->
      subcategoryRepository.createSubcategory(
        name = subcategory.name,
        icon = subcategory.icon,
        categoryId = categoryId,
        ordinalIndex = index,
      )
    }
  }
}