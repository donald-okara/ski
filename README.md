<p align="center">
  <img src="ski_logo.png" 
       alt="Ski Logo" 
       width="180" />
</p>

# Ski: A Compose Multiplatform Presentation Framework

Ski is a programmable presentation framework built on Compose Multiplatform. It allows Kotlin engineers to build slide decks the same way they build UI: with composables, state, and reusable components.

Ski targets Web (Wasm) and Desktop (JVM), enabling you to run the same presentation as a browser app or a desktop application.

---

## Why Ski Exists

Traditional slide tools are document-based. Engineers build interfaces using components, state, and predictable layout systems. That mismatch creates friction.

Ski aligns presentations with how Kotlin developers already work.

Built on Compose Multiplatform, Ski treats slides as composables instead of static pages. Layout is declarative. Animations are state-driven. Components are reusable. Slide progression can be modeled as UI state instead of duplicated screens.

If you can build it in Compose, you can present it.

---

## Core Capabilities

### Programmable Slides

Slides are composables.

You can:

* Drive progression using state instead of duplicating slides
* Conditionally render content
* Reuse components across multiple slides
* Structure your deck like a real UI tree

Your presentation becomes an application, not a sequence of static documents.

---

### Stateful Animations

Reveals and transitions are powered by Compose state.

Build animated lists, counters, diagrams, and charts using Compose animation APIs. No timeline editors. No slide duplication to simulate progression. Behavior is defined entirely in Kotlin.

---

### Dual-Window Presenter Mode

Ski launches two synchronized windows:

* **Presenter Panel** — Notes, table of contents, and keyboard shortcuts
* **Presentation Surface** — Clean output intended for projection

Both windows stay synchronized on the current slide index. Screen-specific animation state is intentionally isolated per window.

On Web, this feature relies on browser popup APIs and is currently less stable than the Desktop implementation.

---

### Composable UI for Live Demos

For UI-focused talks, you can demonstrate composables directly inside your slides.

Trigger state changes. Show layout behavior. Animate transitions. Walk through UI concepts without switching applications or embedding screen recordings.

---

## Project Structure

```
./composeApp   -> Application host for Web and Desktop targets
./shared       -> Shared design system, resources, and reusable components
./segments     -> Individual slide decks or modular presentation sections
```

* `composeApp` contains the Compose Multiplatform entry points (`commonMain`, `jvmMain`, `wasmJsMain`).
* `shared` centralizes reusable UI primitives and design elements.
* `segments` encourages modular talk organization and reuse across presentations.

---

## Key Components

### PresentationDeck

`PresentationDeck` is the runtime engine that manages slide lifecycle, navigation, theming, and background rendering.

```kotlin
@Composable
fun PresentationDeck(
    slides: List<SlideConfig>,
    navigator: DeckNavigator,
    background: (@Composable () -> Unit)? = null,
    guidesFrame: SkiFrame
)
```

* **slides** — Ordered `SlideConfig` definitions
* **navigator** — Controls slide navigation and index state
* **background** — Optional composable rendered behind slide content
* **guidesFrame** — A `SkiFrame` overlay applied consistently across slides

---

### Frames

Frames provide decorative overlays or structural borders around slide content.

Built-in frames are available via `defaultSkiFrames()`.

```kotlin
val guidesFrame = defaultSkiFrames()
    .basic
    .create(cornerRadius, opacity)
```

Frames are composable-driven and can be extended or replaced.

---

## Using Shared Components

Ski components can be used locally within the repository or consumed as a dependency.

To use local shared components:

```toml
use_local_shared_components = true
```

This flag can be toggled in `gradle.properties`.

---

## Getting Started

### Run on Web (Wasm)

```shell
# macOS/Linux
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Windows
.\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
```

### Run on Desktop (JVM)

```shell
# macOS/Linux
./gradlew :composeApp:run

# Windows
.\gradlew.bat :composeApp:run
```

---
Please check the [suggested prompt](PROMPT.md) to use with your AI tool of choice.

Happy pitching!