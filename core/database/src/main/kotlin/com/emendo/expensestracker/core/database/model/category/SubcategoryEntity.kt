package com.emendo.expensestracker.core.database.model.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.emendo.expensestracker.core.database.util.CATEGORY_PRIMARY_KEY
import com.emendo.expensestracker.core.database.util.TABLE_SUBCATEGORY

@Entity(
  tableName = TABLE_SUBCATEGORY,
  foreignKeys = [
    ForeignKey(
      entity = CategoryEntity::class,
      parentColumns = [CATEGORY_PRIMARY_KEY],
      childColumns = ["categoryId"],
      onDelete = CASCADE,
    ),
  ]
)
data class SubcategoryEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  @ColumnInfo(index = true)
  val categoryId: Long,
  val name: String,
  val iconId: Int,
  val ordinalIndex: Int,
)