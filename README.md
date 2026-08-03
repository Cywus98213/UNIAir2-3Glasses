# UNIAir23Glasses

Comprehensive README for the UNIAir23Glasses Android application.

**Project summary**
- **Name:** UNIAir23Glasses
- **Package / ApplicationId:** `com.example.UNIAir23Glasses` (see [app/build.gradle.kts](app/build.gradle.kts#L1))
- **Main activity:** `com.example.UNIAir23Glasses.MainActivity` (see [app/src/main/AndroidManifest.xml](app/src/main/AndroidManifest.xml#L1))
- **App label:** `uniserverphoneapp` (defined in AndroidManifest)

**Key details**
- **Minimum SDK:** 24
- **Target / Compile SDK:** 36
- **Java compatibility:** Java 11
- **Gradle:** Uses Gradle Kotlin DSL (see [settings.gradle.kts](settings.gradle.kts#L1))

**Prerequisites**
- **Android Studio:** Recommended (Arctic Fox or later); ensure the Android SDK for API 36 is installed.
- **JDK:** Java 11 (matching `compileOptions` in [app/build.gradle.kts](app/build.gradle.kts#L1)).
- **Gradle wrapper:** This project uses the included Gradle wrapper (`gradlew` / `gradlew.bat`).

**Quick start — build & run**

From Android Studio
- Open the project directory in Android Studio.
- Let Gradle sync, then select a target device (emulator or physical device) and press Run.

From the command line (Windows)

```bash
cd UNIAir23GlassesApp
./gradlew.bat assembleDebug
./gradlew.bat installDebug
```

From the command line (Unix / WSL / macOS)

```bash
cd UNIAir23GlassesApp
./gradlew assembleDebug
./gradlew installDebug
```

If you see errors about `sdk.dir`, create or edit `local.properties` at the project root to point to your Android SDK installation:

```properties
sdk.dir=C:\Users\<YourUser>\AppData\Local\Android\Sdk
```

**Testing**
- Unit tests: `./gradlew test` — runs JVM unit tests in `app/src/test`.
- Instrumentation tests: `./gradlew connectedAndroidTest` — runs device/emulator tests in `app/src/androidTest`.

**Permissions**
The app requests the following permissions in [app/src/main/AndroidManifest.xml](app/src/main/AndroidManifest.xml#L1):
- `INTERNET`
- `RECORD_AUDIO`
- `ACCESS_WIFI_STATE`
- `CHANGE_WIFI_STATE`

**Project structure (high level)**
- **app/**: Android application module. Main sources and resources live here.
- **app/src/main/AndroidManifest.xml**: Application manifest and component declarations.
- **app/build.gradle.kts**: Module build configuration with `applicationId`, SDK targets, and dependencies.
- **gradle.properties** and **settings.gradle.kts**: Repository and Gradle configuration.

See these files in the repo:
- [app/build.gradle.kts](app/build.gradle.kts#L1)
- [app/src/main/AndroidManifest.xml](app/src/main/AndroidManifest.xml#L1)
- [settings.gradle.kts](settings.gradle.kts#L1)

**Common troubleshooting**
- Gradle sync errors: In Android Studio, try _File > Sync Project with Gradle Files_ and check the IDE's SDK settings.
- Emulator/device install fails: Verify a device is connected (`adb devices`) and uninstall any older conflicting builds.
- JDK mismatch: Ensure Android Studio is configured to use JDK 11.

**Contributing**
- Fork the repository and open a pull request with a clear description of changes.
- Follow the existing code style and test new behavior where appropriate.

**License**
- No license file detected in this repository. Add a `LICENSE` file if you want to set explicit terms.
