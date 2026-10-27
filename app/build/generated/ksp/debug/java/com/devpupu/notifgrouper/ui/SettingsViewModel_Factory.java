package com.devpupu.notifgrouper.ui;

import com.devpupu.notifgrouper.data.repository.AppPreferencesRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<AppPreferencesRepository> repoProvider;

  public SettingsViewModel_Factory(Provider<AppPreferencesRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(repoProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<AppPreferencesRepository> repoProvider) {
    return new SettingsViewModel_Factory(repoProvider);
  }

  public static SettingsViewModel newInstance(AppPreferencesRepository repo) {
    return new SettingsViewModel(repo);
  }
}
