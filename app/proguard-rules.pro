# Hilt / Dagger
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.** { *; }
-keep class javax.inject.** { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @javax.inject.Inject class * { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-keepclassmembers class * {
    @dagger.** <methods>;
    @javax.inject.** <methods>;
}
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
-keep class * extends androidx.hilt.work.HiltWorker { *; }

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class androidx.room.** { *; }

# NotificationListenerService
-keep class * extends android.service.notification.NotificationListenerService { *; }
-keepclassmembers class * extends android.service.notification.NotificationListenerService {
    public <init>();
}

# Compose
-keep class androidx.compose.** { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn androidx.compose.**
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keep class * extends androidx.compose.ui.Modifier { *; }

# General
-keepattributes Signature, Exceptions, InnerClasses, EnclosingMethod
-keep class kotlin.Metadata { *; }
-dontwarn org.jetbrains.annotations.**