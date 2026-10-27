package com.devpupu.notifgrouper.di;

import com.devpupu.notifgrouper.data.db.AppDatabase;
import com.devpupu.notifgrouper.data.db.GroupedAppDao;
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
public final class DatabaseModule_ProvideGroupedAppDaoFactory implements Factory<GroupedAppDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideGroupedAppDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public GroupedAppDao get() {
    return provideGroupedAppDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideGroupedAppDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideGroupedAppDaoFactory(dbProvider);
  }

  public static GroupedAppDao provideGroupedAppDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideGroupedAppDao(db));
  }
}
