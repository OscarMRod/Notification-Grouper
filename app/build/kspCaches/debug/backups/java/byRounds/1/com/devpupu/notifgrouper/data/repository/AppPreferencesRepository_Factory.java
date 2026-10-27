package com.devpupu.notifgrouper.data.repository;

import com.devpupu.notifgrouper.data.db.GroupedAppDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppPreferencesRepository_Factory implements Factory<AppPreferencesRepository> {
  private final Provider<GroupedAppDao> daoProvider;

  public AppPreferencesRepository_Factory(Provider<GroupedAppDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public AppPreferencesRepository get() {
    return newInstance(daoProvider.get());
  }

  public static AppPreferencesRepository_Factory create(Provider<GroupedAppDao> daoProvider) {
    return new AppPreferencesRepository_Factory(daoProvider);
  }

  public static AppPreferencesRepository newInstance(GroupedAppDao dao) {
    return new AppPreferencesRepository(dao);
  }
}
