package com.emendo.expensestracker.core.database.model.transaction

import androidx.room.Embedded
import androidx.room.Relation
import com.emendo.expensestracker.core.database.model.account.AccountEntity
import com.emendo.expensestracker.core.database.model.category.CategoryEntity
import com.emendo.expensestracker.core.database.model.category.SubcategoryEntity

data class TransactionFull(
  @Embedded
  val transactionEntity: TransactionEntity,
  @Relation(parentColumn = "sourceAccountId", entityColumn = "id")
  val sourceAccount: AccountEntity,
  @Relation(parentColumn = "targetAccountId", entityColumn = "id")
  val targetAccount: AccountEntity?,
  @Relation(parentColumn = "targetCategoryId", entityColumn = "id")
  val targetCategory: CategoryEntity?,
  @Relation(parentColumn = "targetSubcategoryId", entityColumn = "id")
  val targetSubcategory: SubcategoryEntity?,
)