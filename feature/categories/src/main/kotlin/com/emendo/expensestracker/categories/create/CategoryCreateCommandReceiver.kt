package com.emendo.expensestracker.categories.create

import com.emendo.expensestracker.categories.common.CategoryCastCommand
import com.emendo.expensestracker.categories.common.CategoryCommandReceiver

interface CategoryCreateCommandReceiver : CategoryCommandReceiver {

  fun createCategory()
  fun consumeNavigateUpEvent()
}

class CreateCategoryCommand : CategoryCastCommand<CategoryCreateCommandReceiver> {
  override fun executeCast(receiver: CategoryCreateCommandReceiver) {
    receiver.createCategory()
  }
}

class ConsumeNavigateUpEventCommand : CategoryCastCommand<CategoryCreateCommandReceiver> {
  override fun executeCast(receiver: CategoryCreateCommandReceiver) {
    receiver.consumeNavigateUpEvent()
  }
}
