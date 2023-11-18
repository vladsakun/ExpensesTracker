package com.emendo.expensestracker.sync;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.emendo.expensestracker.core.data.repository.api.CurrencyRateRepository;
import com.emendo.expensestracker.core.datastore.ExpePreferencesDataStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata
@QualifierMetadata("com.emendo.expensestracker.core.app.common.network.Dispatcher")
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
public final class SyncCurrencyRatesWorker_Factory {
  private final Provider<CurrencyRateRepository> currencyRateRepositoryProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  private final Provider<ExpePreferencesDataStore> expePreferencesDataStoreProvider;

  public SyncCurrencyRatesWorker_Factory(
      Provider<CurrencyRateRepository> currencyRateRepositoryProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider,
      Provider<ExpePreferencesDataStore> expePreferencesDataStoreProvider) {
    this.currencyRateRepositoryProvider = currencyRateRepositoryProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
    this.expePreferencesDataStoreProvider = expePreferencesDataStoreProvider;
  }

  public SyncCurrencyRatesWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, currencyRateRepositoryProvider.get(), ioDispatcherProvider.get(), expePreferencesDataStoreProvider.get());
  }

  public static SyncCurrencyRatesWorker_Factory create(
      Provider<CurrencyRateRepository> currencyRateRepositoryProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider,
      Provider<ExpePreferencesDataStore> expePreferencesDataStoreProvider) {
    return new SyncCurrencyRatesWorker_Factory(currencyRateRepositoryProvider, ioDispatcherProvider, expePreferencesDataStoreProvider);
  }

  public static SyncCurrencyRatesWorker newInstance(Context appContext,
      WorkerParameters workerParams, CurrencyRateRepository currencyRateRepository,
      CoroutineDispatcher ioDispatcher, ExpePreferencesDataStore expePreferencesDataStore) {
    return new SyncCurrencyRatesWorker(appContext, workerParams, currencyRateRepository, ioDispatcher, expePreferencesDataStore);
  }
}
