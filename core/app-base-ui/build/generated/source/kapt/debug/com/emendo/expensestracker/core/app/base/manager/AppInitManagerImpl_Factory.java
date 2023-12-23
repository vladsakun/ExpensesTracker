package com.emendo.expensestracker.core.app.base.manager;

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

  public AppInitManagerImpl_Factory(
      Provider<CurrencyRateRepository> currencyRateRepositoryProvider) {
    this.currencyRateRepositoryProvider = currencyRateRepositoryProvider;
  }

  @Override
  public AppInitManagerImpl get() {
    return newInstance(currencyRateRepositoryProvider.get());
  }

  public static AppInitManagerImpl_Factory create(
      Provider<CurrencyRateRepository> currencyRateRepositoryProvider) {
    return new AppInitManagerImpl_Factory(currencyRateRepositoryProvider);
  }

  public static AppInitManagerImpl newInstance(CurrencyRateRepository currencyRateRepository) {
    return new AppInitManagerImpl(currencyRateRepository);
  }
}
