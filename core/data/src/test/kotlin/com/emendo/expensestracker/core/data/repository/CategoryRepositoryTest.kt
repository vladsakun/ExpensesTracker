package com.emendo.expensestracker.core.data.repository

import app.cash.turbine.test
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.mapper.CategoryFullMapper
import com.emendo.expensestracker.core.database.dao.CategoryDao
import com.emendo.expensestracker.core.database.model.category.CategoryDetailUpdate
import com.emendo.expensestracker.core.database.model.category.CategoryEntity
import com.emendo.expensestracker.core.database.model.category.CategoryFull
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.category.CategoryWithTransactions
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryRepositoryTest {
  private val testScheduler = TestCoroutineScheduler()
  private val testDispatcher = StandardTestDispatcher(testScheduler)
  private val testScope = TestScope(testDispatcher)
  private var categoryDao: CategoryDao = mockk(relaxed = true)
  private var categoryFullMapper: CategoryFullMapper = mockk(relaxed = true)
  private lateinit var repository: OfflineFirstCategoryRepository

  @Before
  fun setUp() {
    repository = OfflineFirstCategoryRepository(
      categoryDao = categoryDao,
      categoryFullMapper = categoryFullMapper,
      ioDispatcher = testDispatcher,
      scope = testScope,
    )
  }

  @Test
  fun `getCategories returns expected categories`() = runTest(testDispatcher) {
    val mockEntity = CategoryFull(
      categoryEntity = CategoryEntity(
        id = 1,
        name = "Test",
        iconId = IconModel.UNKNOWN.id,
        colorId = ColorModel.Base.id,
        ordinalIndex = 0,
        type = CategoryType.EXPENSE.id,
      ),
      subCategories = emptyList(),
    )
    every { categoryDao.getCategoriesFull() } returns flowOf(listOf(mockEntity))

    val expected = listOf(
      CategoryModel(
        id = 1,
        name = TextValue.Value("Test"),
        icon = IconModel.UNKNOWN,
        color = ColorModel.Base,
        ordinalIndex = 0,
        currency = null,
        type = CategoryType.EXPENSE,
        subcategories = emptyList(),
      )
    )

    val repository = OfflineFirstCategoryRepository(
      categoryDao = categoryDao,
      categoryFullMapper = categoryFullMapper,
      ioDispatcher = testDispatcher,
      scope = TestScope(testDispatcher),
    )

    testDispatcher.scheduler.advanceUntilIdle()

    repository.getCategories().test {
      assertEquals(expected, awaitItem())
    }
  }

  @Test
  fun `getCategorySnapshotById returns null for missing id`() = runTest(testDispatcher) {
    every { categoryDao.getCategoriesFull() } returns flowOf(emptyList())
    val result = repository.getCategorySnapshotById(999)
    assertNull(result)
  }

  @Test
  fun `createCategory saves category and returns id`() = runTest(testDispatcher) {
    val name = "Food"
    val icon = IconModel.UNKNOWN
    val color = ColorModel.Base
    val type = CategoryType.EXPENSE
    coEvery { categoryDao.save(any<CategoryEntity>()) } returns 42L
    every { categoryDao.getCategoriesFull() } returns flowOf(emptyList())
    val id = repository.createCategory(name, icon, color, type)
    assertEquals(42L, id)
    coVerify { categoryDao.save(any<CategoryEntity>()) }
  }

  @Test
  fun `getCategories returns empty list when DAO returns empty`() = runTest(testDispatcher) {
    every { categoryDao.getCategoriesFull() } returns flowOf(emptyList())
    val result = repository.getCategoriesSnapshot()
    assertEquals(emptyList<CategoryModel>(), result)
  }

  @Test
  fun `getCategoriesWithTransactions returns empty list when DAO returns empty`() = runTest(testDispatcher) {
    every { categoryDao.getCategoriesWithTransactionsFull() } returns flowOf(emptyList())
    repository.getCategoriesWithTransactions().test {
      assertEquals(emptyList<CategoryWithTransactions>(), awaitItem())
    }
  }

  @Test
  fun `createCategory sets correct ordinalIndex for new category`() = runTest(testDispatcher) {
    val name = "New"
    val icon = IconModel.UNKNOWN
    val color = ColorModel.Base
    val type = CategoryType.EXPENSE
    val existingCategory = CategoryModel(
      id = 0,
      name = TextValue.Value("Existing"),
      icon = IconModel.UNKNOWN,
      color = ColorModel.Base,
      ordinalIndex = 0,
      currency = null,
      type = CategoryType.EXPENSE,
      subcategories = emptyList(),
    )
    val categoryEntity = CategoryEntity(
      id = existingCategory.id,
      name = name,
      iconId = icon.id,
      colorId = color.id,
      ordinalIndex = 0,
      type = type.id,
    )
    coEvery { categoryDao.save(any<CategoryEntity>()) } returns 99L
    val id = repository.createCategory(name, icon, color, type)
    assertEquals(99L, id)
    coVerify { categoryDao.save(categoryEntity) }
  }

  @Test
  fun `updateCategory updates correct fields`() = runTest(testDispatcher) {
    val update = slot<CategoryDetailUpdate>()
    coEvery { categoryDao.updateCategoryDetail(capture(update)) } just Runs
    repository.updateCategory(10L, "Name", IconModel.UNKNOWN, ColorModel.Base, CategoryType.EXPENSE)
    assertEquals(10L, update.captured.id)
    assertEquals("Name", update.captured.name)
    assertEquals(IconModel.UNKNOWN.id, update.captured.iconId)
    assertEquals(ColorModel.Base.id, update.captured.colorId)
    assertEquals(CategoryType.EXPENSE.id, update.captured.type)
  }

  @Test
  fun `updateCategory calls updateCategoryDetail`() = runTest(testDispatcher) {
    coEvery { categoryDao.updateCategoryDetail(any()) } just Runs
    repository.updateCategory(1L, "Updated", IconModel.UNKNOWN, ColorModel.Base, CategoryType.EXPENSE)
    coVerify { categoryDao.updateCategoryDetail(any()) }
  }

  @Test
  fun `updateOrdinalIndex calls updateOrdinalIndex`() = runTest(testDispatcher) {
    coEvery { categoryDao.updateOrdinalIndex(any()) } just Runs
    repository.updateOrdinalIndex(1L, 5)
    coVerify { categoryDao.updateOrdinalIndex(any()) }
  }

  @Test
  fun `deleteCategory calls deleteById`() = runTest(testDispatcher) {
    coEvery { categoryDao.deleteById(any()) } just Runs
    repository.deleteCategory(1L)
    coVerify { categoryDao.deleteById(1L) }
  }

  @Test
  fun `deleteCategory does nothing if id does not exist`() = runTest(testDispatcher) {
    coEvery { categoryDao.deleteById(any()) } just Runs
    repository.deleteCategory(9999L)
    coVerify { categoryDao.deleteById(9999L) }
  }
}
