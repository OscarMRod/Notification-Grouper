package com.devpupu.notifgrouper.service;

import com.devpupu.notifgrouper.data.repository.GroupingEngine;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class NotifGrouperListenerService_MembersInjector implements MembersInjector<NotifGrouperListenerService> {
  private final Provider<GroupingEngine> groupingEngineProvider;

  public NotifGrouperListenerService_MembersInjector(
      Provider<GroupingEngine> groupingEngineProvider) {
    this.groupingEngineProvider = groupingEngineProvider;
  }

  public static MembersInjector<NotifGrouperListenerService> create(
      Provider<GroupingEngine> groupingEngineProvider) {
    return new NotifGrouperListenerService_MembersInjector(groupingEngineProvider);
  }

  @Override
  public void injectMembers(NotifGrouperListenerService instance) {
    injectGroupingEngine(instance, groupingEngineProvider.get());
  }

  @InjectedFieldSignature("com.devpupu.notifgrouper.service.NotifGrouperListenerService.groupingEngine")
  public static void injectGroupingEngine(NotifGrouperListenerService instance,
      GroupingEngine groupingEngine) {
    instance.groupingEngine = groupingEngine;
  }
}
