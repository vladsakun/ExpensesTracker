package com.emendo.expensestracker.core.data.repository;

import com.emendo.expensestracker.core.database.dao.CategoryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class OfflineFirstCategoryRepository_Factory implements Factory<OfflineFirstCategoryRepository> {
  private final Provider<CategoryDao> categoryDaoProvider;

  public OfflineFirstCategoryRepository_Factory(Provider<CategoryDao> categoryDaoProvider) {
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public OfflineFirstCategoryRepository get() {
    return newInstance(categoryDaoProvider.get());
  }

  public static OfflineFirstCategoryRepository_Factory create(
      Provider<CategoryDao> categoryDaoProvider) {
    return new OfflineFirstCategoryRepository_Factory(categoryDaoProvider);
  }

  public static OfflineFirstCategoryRepository newInstance(CategoryDao categoryDao) {
    return new OfflineFirstCategoryRepository(categoryDao);
  }
}
