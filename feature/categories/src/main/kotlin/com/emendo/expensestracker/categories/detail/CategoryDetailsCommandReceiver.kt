package com.emendo.expensestracker.categories.detail

import com.emendo.expensestracker.categories.common.CategoryCastCommand
import com.emendo.expensestracker.categories.common.CategoryCommandReceiver

interface CategoryDetailsCommandReceiver : CategoryCommandReceiver {

  fun updateCategory()
  fun showDeleteCategoryBottomSheet()
}

class UpdateCategoryCategoryDetailCommand : CategoryCastCommand<CategoryDetailsCommandReceiver> {
  override fun executeCast(receiver: CategoryDetailsCommandReceiver) {
    receiver.updateCategory()
  }
}

class ShowDeleteCategoryBottomSheetCategoryDetailCommand : CategoryCastCommand<CategoryDetailsCommandReceiver> {
  override fun executeCast(receiver: CategoryDetailsCommandReceiver) {
    receiver.showDeleteCategoryBottomSheet()
  }
}