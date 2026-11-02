# Ski as an engine

Ski is the open-source core of Ski Studio. This repo keeps the code-first framework (components, gallery, demo deck, web app) and adds the engine that draws decks stored as data. Ski Studio, the paid desktop app with the phone remote, lives in a separate repo (`ski-studio`) and depends on the artifacts this repo publishes. Its plans are in [`docs/studio/`](../studio/README.md) and move there when it's created.

| File | What it covers |
|---|---|
| this README | Repo split, Task 0 (publishing), versioning, local development with the studio |
| [01-engine.md](01-engine.md) | Phase 1: `DeckDocument`, edit operations, renderer, Kotlin export |

## The split

| Lives in `ski` (this repo, Apache 2.0, published) | Lives in `ski-studio` (separate repo, can stay private) |
|---|---|
| `:core:domain`, `:shared:components`, `:shared:design`, `:shared:resources` | `:studio` desktop UI (chat, preview, assets panel, settings, paywall) |
| `:engine:model`, `:engine:ops`, `:engine:renderer` (new) | `:ai:agent`, `:ai:mcp` |
| `:engine:export` (new, DeckDocument → Kotlin) | `:remote:protocol`, `:remote:server`, `:remote:client` |
| `:webPlayer` (new, static web app that renders a `deck.json`; released as a zip, 01 Task 1.13) | Publish to web / Deploy to Vercel flow that ships the player with a deck |
| `:composeApp`, `:webApp`, `:desktopApp`, `:androidApp`, `iosApp`: demo deck + gallery | `:billing`, `:mobileRemote` |
| Maven Central publishing on tag push | Desktop/Android/iOS apps, packaging, release CI, RevenueCat |

How to decide where something new goes: if a code-first user or a third-party tool could use it without the studio, it belongs here. If it's about the app, accounts, money or the phone remote, it belongs in `ski-studio`.

The remote protocol starts in `ski-studio` because only the studio and its phone app use it. Move it here if third-party remotes ever become a goal.

The existing apps in this repo stay as they are. They don't turn into the studio or the remote.

## Task 0: publish every library module (do this before Phase 1)

### The current gap

Only `:shared:components` applies the publish plugin (`shared/components/build.gradle.kts`). It publishes as `io.github.donald-okara:ski` and declares `api(project(":core:domain"))`. `:core:domain` has no publish plugin, group or version, so the published POM most likely points at a `:core:domain` artifact that doesn't exist on Maven Central.

Confirm before changing anything: in a blank Gradle project, depend on `io.github.donald-okara:ski:<latest>` and run `./gradlew dependencies`. If `core:domain` (or `Ski:domain:unspecified`) fails to resolve, the gap is real. The studio would hit it on day one.

### 0.1 Convention plugin

Add `ke.don.ski.publishedLibrary` in `build-logic/convention/src/main/kotlin/ke/don/ski/PublishedLibrary.kt`, registered in `build-logic/convention/build.gradle.kts` and `[plugins]` in `gradle/libs.versions.toml`. It:
- applies `com.vanniktech.maven.publish`;
- sets `group = "io.github.donald-okara"` and reads `version` from `-Pversion` (move the current `findProperty("version") ?: throw` logic here, but throw only when a publish task is in the task graph, so normal builds don't need `-Pversion`);
- fills the shared POM fields (licence, developer, SCM) that `shared/components/build.gradle.kts` has today;
- takes the artifact ID and description from a small extension: `skiPublishing { artifactId = "ski-engine-model"; description = "…" }`.

Then `shared/components/build.gradle.kts` shrinks to `alias(libs.plugins.publishedLibrary)` plus the extension block.

### 0.2 Artifacts

| Module | Artifact | Notes |
|---|---|---|
| `:core:domain` | `ski-domain` | new artifact; fixes the gap |
| `:shared:components` | `ski` (keep) | renaming breaks existing users; keep `ski` and add `ski-components` later only if it's worth a migration note |
| `:shared:design` | `ski-design` | only if `:engine:renderer` or the components depend on it |
| `:shared:resources` | `ski-resources` | same; Compose resources need `compose.resources { publicResClass = true; packageOfResClass = … }` to work from a published artifact |
| `:engine:model` | `ski-engine-model` | Phase 1 |
| `:engine:ops` | `ski-engine-ops` | Phase 1 |
| `:engine:renderer` | `ski-engine-renderer` | Phase 1; pulls in `composemediaplayer` for video |
| `:engine:export` | `ski-engine-export` | JVM only |
| (new) `:bom` | `ski-bom` | Gradle `java-platform` listing all of the above at one version, so the studio pins one number |

Every module publishes on every release with the same version, even when it didn't change. One version number is much easier than tracking compatibility between modules.

### 0.3 CI

`.github/workflows/publish.yml` already runs `publishToMavenCentral -Pversion=<tag>` at the root, which publishes every module with the plugin, so it needs no change beyond a check that the new artifacts show up. Phase 1 adds one step: build and attach the `ski-web-player-<version>.zip` bundle to the GitHub release (01, Task 1.13). Add a PR workflow that runs `./gradlew build` plus the engine tests once Phase 1 adds them.

Update `CLAUDE.md` ("Publishing" section) to list the new artifacts.

### 0.4 Check it

Publish to Maven Local (`./gradlew publishToMavenLocal -Pversion=0.0.0-local`) and depend on `ski-bom` + `ski` from a blank project. Both should resolve with no missing dependencies.

## Versioning and API stability

- **Semantic versions** for all artifacts: `0.x` while the engine settles, `1.0` once the studio ships publicly. The studio pins exact versions through `ski-bom`.
- **ABI checks.** Turn on Kotlin's binary-compatibility validation (the `org.jetbrains.kotlinx.binary-compatibility-validator` plugin, or the ABI validation built into recent Kotlin Gradle plugins) for every published module. Commit the `.api` dumps so any public API change shows up in PR review.
- **Mark unstable APIs.** Add an `@ExperimentalSkiApi` opt-in annotation in `:core:domain` for things still moving, so the studio can use them early without them counting as stable.
- **The file format is a separate contract from the Kotlin API.** `DeckDocument.schemaVersion` plus migrations (01, Task 1.5) mean a deck saved by any studio version keeps opening with newer engine versions. Old engines open newer files in degraded mode, showing placeholders for unknown elements.
- **`ski = …` in `libs.versions.toml`** keeps tracking the published version used by segments when `use_local_shared_components=false`, as described in `CLAUDE.md`.

## Developing ski and ski-studio together

Waiting for a Maven Central release for every engine change would slow everything down. Use a Gradle composite build in `ski-studio`, with the same idea as the existing `use_local_shared_components` flag here:

```kotlin
// ski-studio/settings.gradle.kts
val useLocalSki = providers.gradleProperty("use_local_ski").orNull == "true"
if (useLocalSki) {
    includeBuild("../ski") {
        dependencySubstitution {
            substitute(module("io.github.donald-okara:ski")).using(project(":shared:components"))
            substitute(module("io.github.donald-okara:ski-domain")).using(project(":core:domain"))
            substitute(module("io.github.donald-okara:ski-engine-model")).using(project(":engine:model"))
            substitute(module("io.github.donald-okara:ski-engine-ops")).using(project(":engine:ops"))
            substitute(module("io.github.donald-okara:ski-engine-renderer")).using(project(":engine:renderer"))
        }
    }
}
```

- Clone both repos side by side (`~/AndroidStudioProjects/ski` and `~/AndroidStudioProjects/ski-studio`).
- `use_local_ski=true` in the studio's `gradle.properties` while working on both; `false` in CI so the studio always builds against published artifacts.
- `ski`'s `build-logic` is an included build of its own. A composite build of a build that includes `build-logic` works, but check that the convention plugins don't clash with the studio's own `build-logic`. Use different plugin IDs, such as `ke.don.studio.*`, in the studio.
- Release flow for a cross-repo change: merge in `ski` → tag → publish → bump `ski-bom` in `ski-studio` → merge there.

## Where to start (day one)

1. Task 0.1–0.4 above: fix publishing and confirm with a blank consumer project.
2. [01-engine.md](01-engine.md), Task 1.1: the `jvmLibrary` convention and the empty `:engine:model` module, with `publishedLibrary` applied from the start.
3. Task 1.2: `DeckDocument` and its first element types, test-first with `./gradlew :engine:model:jvmTest`.
