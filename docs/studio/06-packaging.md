# Phase 6: packaging, signing and updates

Goal: users download a signed installer, the app updates itself, and releases come from CI. Can start any time after Phase 2; must be done before a public release.

## What exists today

`desktopApp/build.gradle.kts` already has `compose.desktop.application.nativeDistributions` with `Dmg`, `Msi` and `Deb`, `packageName = "ke.don.ski"` and `packageVersion = "1.0.0"`. No signing, no notarization, no update mechanism. `.github/workflows/publish.yml` only publishes the `:shared:components` library to Maven Central on tag push.

## Task 6.1: choose the update path

**Recommended: Hydraulic Conveyor.** It packages Compose Desktop apps for macOS, Windows and Linux, signs and notarizes, and adds auto-update (Sparkle on macOS, MSIX on Windows) from one config file. It has a Compose Desktop integration and is free for open-source projects; check the licence terms for a paid closed-source app before committing.

Alternative: Compose's built-in `jpackage` tasks for installers plus Sparkle (macOS) via JNA, and a "new version available" check against a JSON file on our site for Windows/Linux. More work, fewer moving parts we don't control.

## Task 6.2: macOS

- Apple Developer account (Developer ID Application certificate).
- Hardened runtime + notarization. With plain Compose: `nativeDistributions.macOS { signing { sign.set(true); identity.set(...) }; notarization { appleID, password (app-specific), teamID } }`. With Conveyor: its `app.mac.signing` and notarization settings.
- Entitlements file must allow: `com.apple.security.cs.allow-jit` (JVM), `com.apple.security.network.server` (remote server, 04), `com.apple.security.network.client` (AI providers), `com.apple.security.device.camera` not needed on desktop.
- `Info.plist`: document type for `.ski` (UTI `ke.don.ski.deck`), URL scheme `ski://` (sign-in callback, 05), `NSLocalNetworkUsageDescription`.
- Universal binary or separate arm64/x64 builds. JVM apps are per-arch; ship arm64 first, add x64 if users ask.

## Task 6.3: Windows and Linux (if in v1)

- Windows: code-signing certificate (EV or Azure Trusted Signing) to avoid SmartScreen warnings. `.ski` file association in the MSI/MSIX.
- Linux: `.deb` and AppImage or Flatpak. Keychain (03, Task 3.2) requires Secret Service; document it.

## Task 6.4: versioning

- One version for the studio, separate from the library version (`ski = ...` in `libs.versions.toml` stays for `:shared:components`).
- Add `studio = "0.1.0"` to `libs.versions.toml` and feed it to `packageVersion` and to the About screen.
- Phone apps get their own `versionCode`/`versionName` (currently `1` / `"1.0"` in `androidApp/build.gradle.kts`). Keep `PROTOCOL_VERSION` (04) as the compatibility contract, not app versions.

## Task 6.5: CI

New workflow `.github/workflows/studio-release.yml`, triggered by tags like `studio-v*` (so it doesn't collide with the library's tag-triggered `publish.yml`):

1. macOS runner: build, sign, notarize, upload DMG (or run Conveyor) to GitHub Releases / our download site.
2. Windows runner: build, sign MSI (if in v1).
3. Linux runner: build `.deb` (if in v1).
4. Android: build the release AAB for `androidApp` and upload to Play internal testing (Gradle Play Publisher or `r0adkll/upload-google-play`).
5. iOS: archive and upload to TestFlight (Fastlane on the macOS runner).

Secrets in GitHub Actions: Apple ID/app-specific password/team ID, signing certificate (base64 p12 + password), Play service account JSON, App Store Connect API key. Also add a plain CI workflow on PRs that runs `./gradlew :engine:model:jvmTest :engine:ops:jvmTest :remote:protocol:jvmTest` once those modules exist.

## Task 6.6: release checklist

- [ ] Version bumped in `libs.versions.toml`.
- [ ] Sample deck opens; Present works on two displays.
- [ ] AI eval prompts pass on at least Anthropic, OpenAI and Ollama (03, Task 3.7).
- [ ] Phone pairs on a home network and on a phone hotspot.
- [ ] Purchase matrix passes in sandbox (05, Task 5.5).
- [ ] Update from the previous version installs cleanly and keeps settings, keys and recent files.
