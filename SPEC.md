# devpupu - Android Notification Grouper
## Technical Specification (PM)

### Objective
Build an Android app that groups notifications in the notification panel by application (package), regardless of the notification's reason, priority, or channel.

### Core Requirements
1. Intercept all incoming notifications via NotificationListenerService.
2. Re-post (or replace) them grouped by originating app package name.
3. Grouping must ignore notification priority, channel, and content.
4. User can enable/disable grouping per app.
5. Settings screen to manage grouped apps and permissions.
6. Must request and handle Notification Access permission.

### Architecture
- **NotificationListenerService** subclass: captures notifications, cancels originals, posts grouped summary notification per app.
- **Grouping engine**: in-memory map of package -> list of active notifications; collapses into a single summary notification per package.
- **Room DB**: persist user preferences (which apps to group, grouped state).
- **Jetpack Compose UI**: settings screen, permission onboarding, per-app toggles.
- **WorkManager**: optional periodic cleanup of stale groups.

### Tech Stack
- Kotlin, Jetpack Compose, Room, Hilt (DI), Coroutines/Flow
- minSdk 26 (Android 8.0), targetSdk 34
- Gradle with Kotlin DSL

### Task Breakdown (for developers)
1. **T1 - Project scaffolding**: Gradle setup, dependencies, Hilt, Room, Compose theme.
2. **T2 - NotificationListenerService**: intercept, cancel original, store in grouping engine.
3. **T3 - Grouping engine**: map package->notifications, post summary notification per package, handle dismissals.
4. **T4 - Room + preferences**: DB for app toggles, repository layer.
5. **T5 - Compose UI**: onboarding (permission), settings list with per-app toggles.
6. **T6 - Tests**: unit tests for grouping engine, instrumented for listener.

### QA Criteria
- Notifications from same app collapse into one summary.
- Different apps remain separate groups.
- Dismissing a group clears all its notifications.
- Permission flow works on fresh install.
- No crashes on notification with null extras.

### Security Review Points
- No notification content persisted to disk (privacy).
- Notification access permission requested transparently with explanation.
- No network calls with notification data.
- Secure handling of sensitive notification content (banking, OTP) - consider exclusion list.
- ProGuard/R8 rules don't leak notification data.
