package com.emendo.expensestracker.categories.detail

import com.emendo.expensestracker.categories.common.CategoryScreenData
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue

typealias CategoryDetailScreenData = CategoryScreenData<Nothing>

interface CategoryDetailScreenDataContract {
  var title: TextValue
  var icon: IconModel
  var color: ColorModel
  var confirmButtonEnabled: Boolean
}

data class CategoryDetailScreenDataImpl(
  override var title: TextValue,
  override var icon: IconModel,
  override var color: ColorModel,
  override var confirmButtonEnabled: Boolean,
  val test: Boolean = false,
) : CategoryDetailScreenDataContract
