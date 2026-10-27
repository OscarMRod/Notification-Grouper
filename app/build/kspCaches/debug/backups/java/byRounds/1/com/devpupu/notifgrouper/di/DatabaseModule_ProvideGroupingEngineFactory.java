package com.devpupu.notifgrouper.di;

import android.app.NotificationManager;
import android.content.Context;
import com.devpupu.notifgrouper.data.repository.GroupingEngine;
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
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class DatabaseModule_ProvideGroupingEngineFactory implements Factory<GroupingEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<NotificationManager> notificationManagerProvider;

  public DatabaseModule_ProvideGroupingEngineFactory(Provider<Context> contextProvider,
      Provider<NotificationManager> notificationManagerProvider) {
    this.contextProvider = contextProvider;
    this.notificationManagerProvider = notificationManagerProvider;
  }

  @Override
  public GroupingEngine get() {
    return provideGroupingEngine(contextProvider.get(), notificationManagerProvider.get());
  }

  public static DatabaseModule_ProvideGroupingEngineFactory create(
      Provider<Context> contextProvider,
      Provider<NotificationManager> notificationManagerProvider) {
    return new DatabaseModule_ProvideGroupingEngineFactory(contextProvider, notificationManagerProvider);
  }

  public static GroupingEngine provideGroupingEngine(Context context,
      NotificationManager notificationManager) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideGroupingEngine(context, notificationManager));
  }
}
