package com.emendo.expensestracker.core.app.base.manager;

import com.emendo.expensestracker.core.domain.account.GetLastUsedAccountUseCase;
import com.emendo.expensestracker.core.domain.account.RetrieveLastUsedAccountUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineScope;

@ScopeMetadata
@QualifierMetadata("com.emendo.expensestracker.core.app.common.network.di.ApplicationScope")
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
public final class CreateTransactionRepositoryImpl_Factory implements Factory<CreateTransactionRepositoryImpl> {
  private final Provider<GetLastUsedAccountUseCase> getLastUsedAccountUseCaseProvider;

  private final Provider<RetrieveLastUsedAccountUseCase> retrieveLastUsedAccountUseCaseProvider;

  private final Provider<CoroutineScope> scopeProvider;

  public CreateTransactionRepositoryImpl_Factory(
      Provider<GetLastUsedAccountUseCase> getLastUsedAccountUseCaseProvider,
      Provider<RetrieveLastUsedAccountUseCase> retrieveLastUsedAccountUseCaseProvider,
      Provider<CoroutineScope> scopeProvider) {
    this.getLastUsedAccountUseCaseProvider = getLastUsedAccountUseCaseProvider;
    this.retrieveLastUsedAccountUseCaseProvider = retrieveLastUsedAccountUseCaseProvider;
    this.scopeProvider = scopeProvider;
  }

  @Override
  public CreateTransactionRepositoryImpl get() {
    return newInstance(getLastUsedAccountUseCaseProvider.get(), retrieveLastUsedAccountUseCaseProvider.get(), scopeProvider.get());
  }

  public static CreateTransactionRepositoryImpl_Factory create(
      Provider<GetLastUsedAccountUseCase> getLastUsedAccountUseCaseProvider,
      Provider<RetrieveLastUsedAccountUseCase> retrieveLastUsedAccountUseCaseProvider,
      Provider<CoroutineScope> scopeProvider) {
    return new CreateTransactionRepositoryImpl_Factory(getLastUsedAccountUseCaseProvider, retrieveLastUsedAccountUseCaseProvider, scopeProvider);
  }

  public static CreateTransactionRepositoryImpl newInstance(
      GetLastUsedAccountUseCase getLastUsedAccountUseCase,
      RetrieveLastUsedAccountUseCase retrieveLastUsedAccountUseCase, CoroutineScope scope) {
    return new CreateTransactionRepositoryImpl(getLastUsedAccountUseCase, retrieveLastUsedAccountUseCase, scope);
  }
}
