package com.emendo.expensestracker.core.database;

import com.emendo.expensestracker.core.database.dao.AccountDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class DaosModule_ProvidesAccountDaoFactory implements Factory<AccountDao> {
  private final Provider<ExpDatabase> databaseProvider;

  public DaosModule_ProvidesAccountDaoFactory(Provider<ExpDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public AccountDao get() {
    return providesAccountDao(databaseProvider.get());
  }

  public static DaosModule_ProvidesAccountDaoFactory create(
      Provider<ExpDatabase> databaseProvider) {
    return new DaosModule_ProvidesAccountDaoFactory(databaseProvider);
  }

  public static AccountDao providesAccountDao(ExpDatabase database) {
    return Preconditions.checkNotNullFromProvides(DaosModule.INSTANCE.providesAccountDao(database));
  }
}
