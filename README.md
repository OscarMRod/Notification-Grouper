# Notification Grouper

An Android app that groups notifications from the same app into a single summary notification, keeping the originals accessible as silent, clickable notifications.

## Features

- Groups multiple notifications from the same app into one summary (using `NotificationCompat.InboxStyle` with `setGroupSummary(true)`)
- Original notifications are reposted as silent (no sound/vibration) so they remain clickable
- Summary notification is alerting and opens the source app on tap
- Foreground service keeps the NotificationListenerService alive on aggressive OEMs (Samsung One UI)
- Sensitive packages and secret-visibility notifications are passed through untouched
- File logging (`devpupu.log`) for on-device debugging

## Tested Devices

| Device | OS | Result |
|---|---|---|
| Samsung (One UI 8.5) | Android 16 | Notifications grouped but not displayed (OEM suppression) |
| Google Pixel 6 Pro | Android 17 | Grouping works correctly; summary shows as alerting notification |

## Build

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Requirements

- Android 8.0+ (API 26)
- Notification access permission (Settings > Apps > Special access > Notification access)
- Disable battery optimization for the app on aggressive OEMs

## License

MIT
