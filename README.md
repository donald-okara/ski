<p align="center">
  <img src="ski_logo.png" alt="Ski Logo" width="180" />
</p>

# Ski: Slides as Compose

**[Live Gallery Demo](https://ski-gallery.vercel.app)** · **[Live Example](https://adaptive-compose.vercel.app/)**

<a href="https://www.buymeacoffee.com/donaldokara">
  <img src="https://img.shields.io/badge/Buy%20Me%20a%20Coffee-ffdd00?style=for-the-badge&logo=buy-me-a-coffee&logoColor=black" alt="Buy Me a Coffee" height="50">
</a>

Ski is a presentation framework built on [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/). A slide is a composable function. A deck is a list of those functions plus navigation, theming, and a presenter view. If you can build it in Compose, you can present it.

Ski runs as a **browser app (Wasm)** or a **desktop app (JVM)**, and the same deck runs on both.

---

## Who it is for

- Kotlin and Compose engineers who give technical talks and want demos that are real UI, not screenshots or screen recordings.
- Teams that want talk code in the same repo as the product, reviewed and versioned like code.
- Anyone who wants slides driven by state (animated reveals, live counters, interactive examples) instead of duplicated slide files.

Ski is not a WYSIWYG editor. Slides are written in Kotlin.

---

## What you get

| Need | Ski provides |
|---|---|
| A deck that runs | `generateDeck { slide(...) }` DSL, `Deck()` entry point, navigator with keyboard control |
| Progressive reveals | Compose state (`remember`, `Animatable`) inside a slide, not one slide per bullet |
| A presenter view | Two synced windows: a presenter panel (notes, timer, table of contents, shortcuts) and a clean audience view |
| Decorative framing | `SnakeFrame`, `BasicFrame`, and a `FrameBuilder` for custom frames, applied per slide or to the whole deck |
| Animated backgrounds | `BackgroundBuilder` with wavy, diagonal-wavy, and animated patterns, plus decorator images |
| Code on screen | `KotlinCodeViewer` with syntax highlighting, light/dark themes, and a focus mode |
| Speaker notes | `Notes` model and `NotesComponent`, shown in the presenter panel |
| Whiteboard | `WhiteboardComponent` for freeform notes during a live demo |
| Device mockups | `DeviceFrame` with Pixel 8, Galaxy S26, iPhone 17, and Pixel Fold specs |
| Layouts | Resizable and draggable boxes, focusable overlays, horizontally and vertically segmented screens, scatter layouts |
| Timing | `TimerController` with a session duration and a `TimerComponent` |
| Theme | `AppTheme` (Material 3 expressive, light and dark) |

---

## Quick start

Requirements: JDK 17+ and the Android SDK (only for the Android target). The Gradle wrapper is included.

```shell
# Web (Wasm) dev server
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# Desktop: opens the presenter panel and the slides window
./gradlew :desktopApp:run

# Component gallery (desktop)
./gradlew :desktopApp:runGallery
```

On Windows, use `gradlew.bat` in place of `./gradlew`.

### Build a production web bundle

```shell
./gradlew :webApp:wasmJsBrowserDistribution
```

The output is written under `webApp/build/`. Deploy that folder with the Vercel CLI (`vercel deploy --prod`) or any static host.

---

## Writing a deck

A deck is defined in one place. In this repo that is `composeApp/src/commonMain/kotlin/ke/don/ski/presentation/ui/SkiPresentationSlides.kt`:

```kotlin
fun skiPresentationSlides(sessionDuration: Duration = SESSION_DURATION): List<SlideConfig> {
    val timerController = rememberTimerController(sessionDuration)

    return remember(timerController) {
        generateDeck(timerController = timerController) {
            slide(
                label = "Introduction",
                transition = ScreenTransition.Fade,
                notes = listOf("Welcome. Set the agenda in one sentence.".toAnnotatedString()),
            ) {
                IntroductionScreen()
            }

            slide(label = "Code", notes = listOf("Walk through the Composable signature.".toAnnotatedString())) {
                KodeViewerSlide()
            }
        }
    }
}
```

### The `slide` parameters

| Parameter | Type | Purpose |
|---|---|---|
| `label` | `String` | Name in the table of contents and header |
| `notes` | `List<AnnotatedString>?` | Points shown in the presenter panel |
| `transition` | `ScreenTransition` | `Horizontal` (default), `Fade`, `Vertical`, or `None` |
| `frame` | `(@Composable () -> SkiFrame?)?` | Decorative border for this slide; overrides the deck frame |
| `header` / `footer` | `(@Composable () -> Unit)?` | Optional composables around the content |
| `content` | `@Composable () -> Unit` | The slide body |

### Conventions that keep decks maintainable

- **Put slide bodies in named composables** (`fun MyDemoSlide()`) in `segments/demos`, and keep the `slide { }` block as a list of references.
- **Reveal with state, not with duplicate slides.** Use `remember` and `Animatable` inside the slide.
- **Show code with `KotlinCodeViewer`**, so it is highlighted and can be focused during the talk.
- **Write presenter notes** in `SlidesNotes.kt`, one named constant per slide. The audience never sees them. See [docs/speaker-notes.md](docs/speaker-notes.md) for how to write them.
- **Use the session timer.** Take the duration from `SlidesConstants.SESSION_DURATION` instead of hardcoding it.

---

## Deck runtime

These are the parts you interact with at runtime.

**Navigation.** `DeckNavigator` holds the current index and direction as Compose state. It exposes `next()`, `previous()`, and `goTo(index)`. The deck renders the current slide inside `AnimatedContent`, and each slide's `transition` decides how it enters.

**Modes.** `DeckMode.Local` is the presenter panel: notes, timer, table of contents, shortcut guide, and hints. `DeckMode.Presenter` is the audience output with no chrome. Modes are provided through `LocalDeckMode`, so components can adapt to them without parameters being threaded through.

**Two windows, synced.**
- *Desktop:* two windows share one `DeckNavigator` in the same process, so they stay in sync automatically.
- *Web:* the main tab writes the current slide to `localStorage` and opens `/?slides` as a popup. The popup listens for the `storage` event and follows. This works, but is less robust than desktop; test it on the browser you present from.

### Keyboard shortcuts

| Key | Action | Available in |
|---|---|---|
| `→`, `Space`, `Enter` | Next slide | Both modes |
| `←`, `Backspace` | Previous slide | Both modes |
| `D` | Switch light/dark theme | Both modes |
| `↑` | Show/hide toolbar. Its pen icon opens the whiteboard | Both modes |
| `W` | Show/hide whiteboard | Both modes |
| `Esc` | Dismiss all overlays | Both modes |
| `T` | Table of contents | Presenter panel |
| `C` | Shortcut guide | Presenter panel |
| `H` or `↓` | Show/hide hint | Presenter panel |
| `]` / `[` | Timer +1 / -1 minute | Presenter panel |
| `Ctrl`/`Cmd` + `+` / `-` / `0` | Bigger / smaller / reset deck text | Both modes |

---

## The toolkit

Everything below is in `:shared:components` unless noted. The `:core:domain` module supplies the models these components use.

### Frames: `ke.don.domain.frames`

Frames are decorative borders. Build one with `FrameBuilder`:

```kotlin
val mainFrame = FrameBuilder()
    .setFrame { snake }        // or { basic }
    .setCurve(32.dp)           // optional corner radius
    .setOpacity(0.5f)          // optional
    .build()
```

Built-in frames: `SnakeFrame` (default) and `BasicFrame`. Frames are provided through `LocalSkiFrames`, so a new frame is added to `defaultSkiFrames()` in `shared/components` and then selected with `setFrame { ... }`.

### Backgrounds: `BackgroundBuilder`

```kotlin
val background = BackgroundBuilder()
    .setPattern(Pattern.AnimatedDiagonalWavyBackground(colors = PatternDefaults.colors))
    .setDecoratorImage(DecoratorImage(Resources.Images.ANDROID_ROBOT))
    .setAlignment(Alignment.BottomEnd)
    .build()
```

Patterns: `Wavy`, `DiagonalWavy` (the default), and `AnimatedDiagonalWavyBackground`. Each takes `colors`, `waveHeight`, `waveLength`, `waveCount`, and `strokeWidth`.

### Guides

| Component | What it does |
|---|---|
| `KotlinCodeViewer`, `KotlinCodeViewerCard`, `FocusKotlinViewer` | Highlighted Kotlin source, with theme toggle, focus mode, and optional lambda folding (`shouldFoldLambdas`) |
| `NotesComponent`, `NotesHint`, `Notes` | Presenter notes. `Notes(title, points: List<AnnotatedString>)` |
| `WhiteboardComponent`, `WhiteboardCard` | Freeform text area for live annotation, with theme and focus controls |
| `DeckShortcuts`, `ShortcutsDictionary`, `KeyEventHandler` | The shortcut guide shown in the presenter panel. `KeyEventHandler` is sealed, so new shortcuts are added inside `shared/components`, not from a deck |

### Layouts

| Component | What it does |
|---|---|
| `HorizontallySegmentedScreen`, `VerticallySegmentedScreen` | Two panes split by a draggable handle, with `minWeight` and `enableDrag` |
| `LazyScatterFlow` | Scattered grid of items, `itemsPerRow` configurable |
| `Focusable`, `ResizableFocusable` | Overlay that dims the rest of the slide and focuses one element |
| `DraggableBox` | A box the audience can resize and move during a demo |

### Devices

`DeviceFrame` wraps any composable in a phone mockup. Specs come from `DeviceCatalog`: `Pixel8`, `GalaxyS26`, `IPhone17`, `PixelFold`. Use it to show a live UI in context instead of a screenshot.

### Pictures and small components

- `ExpressivePictureFrame`, `ExpressiveFrame`, `ExpressiveFrameClipped`: decorative photo frames with an animated outline.
- `TimerComponent`, `TimerContent`, with `TimerController` in `:core:domain`.
- `LinearBullet`, `DotBullet`, `SkiLogo`, `NewBadge`, `IconButtonToken`.

### Theme: `:shared:design`

`AppTheme(darkTheme, enableExpressive, isGallery)` supplies the color scheme, Material 3 expressive motion, and typography. Dimensions come from `Values.Dimens` (`tinyPadding` to `extraLargePadding`).

---

## Using the component library in your own project

Ski's components are published to Maven Central as `io.github.donald-okara:ski`. Add the dependency to your `commonMain` source set and pin the version in your version catalog.

Inside this repo, `use_local_shared_components=true` in `gradle.properties` makes the segments compile against `:shared:components` directly. Set it to `false` to test against the published artifact.

---

## Project layout

```
composeApp/     Deck runtime, gallery, and the Deck() entry point (commonMain for all targets)
shared/         components (the published library), design (theme), resources (images, fonts)
core/domain/    Models and DSL: SlideConfig, DeckBuilder, DeckNavigator, DeckMode, frames, timer
segments/       Slide content: demos (ke.don.demos) and introduction (ke.don.introduction)
webApp/         Web entry point (Wasm and JS)
desktopApp/     Desktop entry point (JVM), plus the gallery launcher
androidApp/     Android entry point
iosApp/         Xcode project that embeds the ComposeApp framework
build-logic/    Gradle convention plugins shared by all modules
```

Package names do not always match folders: `core/domain` is `ke.don.domain`, and `shared/components` is `io.github.donald_okara.components`.

---

## For AI agents

If you are an AI coding agent working in this repo, read this section and `context.md` before changing anything. `CLAUDE.md` has build commands and architecture detail.

### Where things go

| You are adding | Put it in |
|---|---|
| A new slide | A composable in `segments/demos` (or a sub-package), registered in `SkiPresentationSlides.kt` |
| A new intro or section module | Do not create a module. Add a package under `segments/demos` |
| A reusable visual component | `shared/components`, following the existing package per concern |
| A new frame | `shared/components/.../frames/`, exposed through `SkiFrames` |
| A domain model or DSL change | `core/domain`. Keep it free of UI where possible |
| Theme values | `shared/design` |

### Rules for generated slides

1. Reveals use Compose state. Never duplicate a slide to show more bullets.
2. Slide content lives in named composables, not inline in the DSL.
3. Code examples use `KotlinCodeViewer` or `KotlinCodeViewerCard`.
4. Every slide gets presenter `notes`. Keep them specific enough to speak from.
5. Presenter and audience windows keep separate animation state. Do not rely on one window's animation state in the other.
6. Use `LocalSkiFrames` and `LocalDeckMode` rather than passing theme or mode parameters down.
7. Take the timer duration from `SlidesConstants.SESSION_DURATION`.

### Verify before you finish

- Build the target you changed: `./gradlew :desktopApp:run` for deck behavior, `./gradlew :webApp:wasmJsBrowserDevelopmentRun` for web-only code.
- There are no automated tests or lint rules yet. Check the slide visually in the presenter panel and in audience mode.

---

## Contributing and releases

Releases are published from a tag: `.github/workflows/publish.yml` runs `publishToMavenCentral` with the tag as `-Pversion`. Only `:shared:components` is published. Update the `ski` version in `gradle/libs.versions.toml` when the published API changes.

---

## Author

Donald Okara · [GitHub](https://github.com/donald-okara) · [Buy Me a Coffee](https://www.buymeacoffee.com/donaldokara)

Licensed under the Apache License 2.0 (the published components). See the Maven POM for details.
