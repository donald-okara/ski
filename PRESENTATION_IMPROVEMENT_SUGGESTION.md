### Report: Suggestions for Enhancing Developer Experience in `composeApp`

This report outlines suggestions for further abstracting the `composeApp` and the `ski` project. The primary goal is to improve developer productivity, reduce boilerplate, and make the codebase more maintainable as the presentation deck grows.

---

#### 1. Decoupled Slide Registry & DSL

**Current Situation:**
- `Slide` (sealed class in `core:domain`) and `SlideSwitcher` (in `composeApp`) are tightly coupled. 
- Every new slide requires a manual addition to the `Slide` class and a corresponding case in the `SlideSwitcher` `when` block.
- This creates a centralized "god file" that everyone must touch.

**Suggestion:**
Introduce a **Slide Registry** or a **Deck DSL**. Instead of a static sealed class, slides can be registered dynamically.

```kotlin
// Example DSL in your Deck.kt
fun MyPresentation() = PresentationDeck {
    slide("Intro") {
        IntroductionScreen()
    }
    slide("The Problem", notes = listOf("Mention scaling issues")) {
        ProblemScreen()
    }
}
```

**Benefits:**
- **Modularization:** Different teams/developers can define their slides in their own modules (segments) and just "plugin" to the main deck.
- **Type Safety:** Eliminate the manual `when` block in `SlideSwitcher`.

---

#### 2. Encapsulated Slide Metadata

**Current Situation:**
- Speaker notes, transitions, and slide properties (like `showHeader`) are scattered.
- `slidesNotes` is a separate function in `SlideSwitcher.kt`.
- `transitionFor` is a separate function in `MainContainer.kt`.

**Suggestion:**
Encapsulate all metadata within a `Slide` configuration object or the slide registration itself.

```kotlin
data class SlideConfig(
    val label: String,
    val notes: List<String> = emptyList(),
    val transition: ScreenTransition = ScreenTransition.Horizontal,
    val showHeader: Boolean = true,
    val content: @Composable () -> Unit
)
```

**Benefits:**
- **Cohesion:** All information about a slide lives in one place.
- **Ease of Discovery:** When a developer creates a slide, they see all available configuration options (notes, transitions, layout tweaks) in the constructor or DSL.

---

#### 3. State-Driven Layouts (Scaffolding Abstraction)

**Current Situation:**
- `MainContainer` and `DeckScaffolding` have many parameters and complex logic for headers/footers.
- Individual screens sometimes manually create their own `SkiFrame`.

**Suggestion:**
Standardize the "Slide Layout" via a high-level `SlideScaffold`.

```kotlin
@Composable
fun SlideScaffold(
    title: String? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    // Automatically wraps content in a SkiFrame with correct padding/theming
}
```

**Benefits:**
- **Consistency:** Ensures all slides look uniform.
- **Reduced Boilerplate:** Developers only focus on the *content* of the slide.

---

#### 4. Advanced Navigation and State Management

**Current Situation:**
- `DeckNavigator` manually manages indices and is tightly coupled to the slide list.
- Web navigation is a manual callback.

**Suggestion:**
- Use a **State Machine** or a **Backstack** approach for navigation.
- Implement a **"Smart Navigator"** that automatically handles URL syncing (for JS/Wasm) via an observer pattern rather than manual callbacks.
- Support **Nested Navigation** within slides (e.g., advancing sub-bullets before moving to the next slide).

**Benefits:**
- **Robustness:** Handles complex navigation flows (jump-to, deep-linking) more reliably.
- **Cleanliness:** Separates UI intent (Next/Prev) from the implementation of *how* the state is stored or synced.

---

#### 5. Resource and Theme Provisioning

**Current Situation:**
- The project has a dedicated `shared:design` and `shared:resources`.

**Suggestion:**
Use **CompositionLocals** more extensively to provide `Navigator`, `ContainerState`, and `Resources` without passing them down as parameters through every level of the UI tree.

```kotlin
val LocalNavigator = staticCompositionLocalOf<DeckNavigator> { error("No Navigator found") }

@Composable
fun MySlide() {
    val navigator = LocalNavigator.current
    Button(onClick = { navigator.next() }) { ... }
}
```

**Benefits:**
- **Cleaner API:** Functions like `PresenterDsl` or `SlideSwitcher` don't need to pass 5+ parameters down.
- **Decoupling:** Child components can access global state without knowing where it comes from.

---

#### 6. Tooling: Live Preview & Testing

**Current Situation:**
- Testing slide decks often requires running the whole app.

**Suggestion:**
- Create a **Slide Previewer** using Compose Previews.
- Create a **Test Runner** for the deck that can programmatically "play" the whole presentation to check for UI regressions or broken transitions.

**Benefits:**
- **Faster Iteration:** Developers can see their changes instantly without manual navigation.
- **Quality Assurance:** Ensures every slide is at least "renderable" in every theme (Light/Dark).
