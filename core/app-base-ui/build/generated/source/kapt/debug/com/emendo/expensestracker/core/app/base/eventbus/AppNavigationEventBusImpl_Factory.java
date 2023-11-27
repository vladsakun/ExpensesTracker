package com.emendo.expensestracker.core.app.base.eventbus;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AppNavigationEventBusImpl_Factory implements Factory<AppNavigationEventBusImpl> {
  @Override
  public AppNavigationEventBusImpl get() {
    return newInstance();
  }

  public static AppNavigationEventBusImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AppNavigationEventBusImpl newInstance() {
    return new AppNavigationEventBusImpl();
  }

  private static final class InstanceHolder {
    private static final AppNavigationEventBusImpl_Factory INSTANCE = new AppNavigationEventBusImpl_Factory();
  }
}
