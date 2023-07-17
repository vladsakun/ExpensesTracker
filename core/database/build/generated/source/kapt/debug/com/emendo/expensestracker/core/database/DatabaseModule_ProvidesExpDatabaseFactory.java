package com.emendo.expensestracker.core.database;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class DatabaseModule_ProvidesExpDatabaseFactory implements Factory<ExpDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvidesExpDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ExpDatabase get() {
    return providesExpDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvidesExpDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvidesExpDatabaseFactory(contextProvider);
  }

  public static ExpDatabase providesExpDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providesExpDatabase(context));
  }
}
