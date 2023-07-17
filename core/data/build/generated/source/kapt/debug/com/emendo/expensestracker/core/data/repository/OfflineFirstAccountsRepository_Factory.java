package com.emendo.expensestracker.core.data.repository;

import com.emendo.expensestracker.core.database.dao.AccountDao;
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
public final class OfflineFirstAccountsRepository_Factory implements Factory<OfflineFirstAccountsRepository> {
  private final Provider<AccountDao> accountsDaoProvider;

  public OfflineFirstAccountsRepository_Factory(Provider<AccountDao> accountsDaoProvider) {
    this.accountsDaoProvider = accountsDaoProvider;
  }

  @Override
  public OfflineFirstAccountsRepository get() {
    return newInstance(accountsDaoProvider.get());
  }

  public static OfflineFirstAccountsRepository_Factory create(
      Provider<AccountDao> accountsDaoProvider) {
    return new OfflineFirstAccountsRepository_Factory(accountsDaoProvider);
  }

  public static OfflineFirstAccountsRepository newInstance(AccountDao accountsDao) {
    return new OfflineFirstAccountsRepository(accountsDao);
  }
}
