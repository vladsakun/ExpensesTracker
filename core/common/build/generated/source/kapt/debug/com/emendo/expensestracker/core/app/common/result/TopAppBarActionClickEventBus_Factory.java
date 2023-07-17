package com.emendo.expensestracker.core.app.common.result;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class TopAppBarActionClickEventBus_Factory implements Factory<TopAppBarActionClickEventBus> {
  @Override
  public TopAppBarActionClickEventBus get() {
    return newInstance();
  }

  public static TopAppBarActionClickEventBus_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TopAppBarActionClickEventBus newInstance() {
    return new TopAppBarActionClickEventBus();
  }

  private static final class InstanceHolder {
    private static final TopAppBarActionClickEventBus_Factory INSTANCE = new TopAppBarActionClickEventBus_Factory();
  }
}
