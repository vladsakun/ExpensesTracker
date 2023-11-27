package com.emendo.expensestracker.core.app.base.manager;

import com.emendo.expensestracker.core.data.manager.cache.CurrencyCacheManager;
import com.emendo.expensestracker.core.data.repository.api.CurrencyRateRepository;
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
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class AppInitManagerImpl_Factory implements Factory<AppInitManagerImpl> {
  private final Provider<CurrencyRateRepository> currencyRateRepositoryProvider;

  private final Provider<CurrencyCacheManager> currencyCacheManagerProvider;

  private final Provider<CreateTransactionRepository> createTransactionRepositoryProvider;

  public AppInitManagerImpl_Factory(Provider<CurrencyRateRepository> currencyRateRepositoryProvider,
      Provider<CurrencyCacheManager> currencyCacheManagerProvider,
      Provider<CreateTransactionRepository> createTransactionRepositoryProvider) {
    this.currencyRateRepositoryProvider = currencyRateRepositoryProvider;
    this.currencyCacheManagerProvider = currencyCacheManagerProvider;
    this.createTransactionRepositoryProvider = createTransactionRepositoryProvider;
  }

  @Override
  public AppInitManagerImpl get() {
    return newInstance(currencyRateRepositoryProvider.get(), currencyCacheManagerProvider.get(), createTransactionRepositoryProvider.get());
  }

  public static AppInitManagerImpl_Factory create(
      Provider<CurrencyRateRepository> currencyRateRepositoryProvider,
      Provider<CurrencyCacheManager> currencyCacheManagerProvider,
      Provider<CreateTransactionRepository> createTransactionRepositoryProvider) {
    return new AppInitManagerImpl_Factory(currencyRateRepositoryProvider, currencyCacheManagerProvider, createTransactionRepositoryProvider);
  }

  public static AppInitManagerImpl newInstance(CurrencyRateRepository currencyRateRepository,
      CurrencyCacheManager currencyCacheManager,
      CreateTransactionRepository createTransactionRepository) {
    return new AppInitManagerImpl(currencyRateRepository, currencyCacheManager, createTransactionRepository);
  }
}
