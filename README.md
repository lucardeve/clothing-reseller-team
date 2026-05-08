# Clothing Reselling App — Team Repo

Android final project, in progress. **Kotlin + Jetpack Compose + Room + MVVM.**

This repo is the team starter. The data layer is built and tested; the UI is not. Read [`TEAMMATE_GUIDE.md`](TEAMMATE_GUIDE.md) before you do anything else — it lists who is building what, the data layer's API surface, and the deliverables.

## Team

| Name | Role |
|---|---|
| Luca Rarau | Backend + Database |
| Ryan Liautaud | Frontend / UI/UX |
| Noah Bouffard | Frontend UX, troubleshooting, final testing |

## Setup

1. Install **Android Studio Koala (2024.1)** or newer.
2. Open this folder. Let Gradle sync (downloads AGP 8.5.2, Kotlin 2.0.20, Compose BOM, Room, etc.).
3. Connect a device with USB debugging on, or start an emulator running **Android 8.0 (API 26)** or higher.
4. Hit **Run ▶**.

You'll see a placeholder pointing you to the guide. That's expected until the screens are written.

## Tests

Both suites pass on `main` today:

```
./gradlew testDebugUnitTest          # 13 unit tests (data layer + utils)
./gradlew connectedDebugAndroidTest  # 14 instrumented tests (DAOs + repositories)
```

Don't merge if either is red.
