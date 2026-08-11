# Ski Project Context

Ski is a programmable presentation framework built on **Compose Multiplatform**. It allows engineers to build slide decks using composables, state, and reusable components.

## Project Structure

- **`:composeApp`**: Application host for Web (Wasm) and Desktop (JVM) targets. Contains entry points and global navigation logic.
- **`:shared:components`**: Design system and UI primitives (Frames, Guides, Layouts, Backgrounds).
- **`:shared:resources`**: Centralized assets (Images, Strings, Fonts).
- **`:shared:design`**: Theming (Colors, Typography, Dimensions).
- **`:core:domain`**: Core models (`SlideConfig`, `DeckNavigator`) and DSL logic.
- **`:segments`**: Modular presentation sections or individual decks.

## Core DSL: `generateDeck`

Decks are defined using a DSL in `skiPresentationSlides()`. Use the `slide` function to add content.

```kotlin
val slides = generateDeck(timerController) {
    slide(
        label = "Title",
        transition = ScreenTransition.Fade,
        notes = listOf("Point A".toAnnotatedString()),
        frame = { MyCustomFrame() }
    ) {
        MySlideContent()
    }
}
```

### `SlideConfig` Parameters
- `label`: The name of the slide (used in TOC and Header).
- `notes`: `List<AnnotatedString>` for the Presenter View.
- `transition`: Entry animation (e.g., `Horizontal`, `Fade`, `Vertical`).
- `timer`: Managed by `timerController`.
- `frame`: An optional decorative overlay.
- `content`: The composable body of the slide.

## Component Library (`:shared:components`)

Use these pre-built components to maintain visual consistency:
- **Frames**: `SnakeFrame`, `BasicFrame` (use `FrameBuilder`).
- **Guides**: `KotlinCodeViewer` (for snippets), `NotesComponent`, `WhiteboardComponent`.
- **Layouts**: `LazyScatterColumn`, `SegmentedScreens`.
- **Backgrounds**: `BackgroundBuilder` with patterns like `AnimatedDiagonalWavyBackground`.

## AI Implementation Rules

When generating or modifying slides, agents **MUST** follow these rules:

1. **State-Driven Reveals**: Never duplicate slides to show bullet points. Use internal composable state (`remember`, `Animatable`) to handle progressive disclosure.
2. **Modular Content**: Define slide content in separate functions (e.g., `fun MySlideContent()`) rather than inlining everything in the DSL.
3. **Presenter Mode Awareness**: Ensure `notes` are detailed. Remember that state is isolated between the Presenter window and the Audience window.
4. **Code Snippets**: Always use `KotlinCodeViewer` for code examples.
5. **No New Modules**: All new presentation content should be added to the `:segments:demos` module or a new sub-package within it.
6. **Idiomatic Compose**: Leverage `CompositionLocalProvider` (e.g., `LocalSkiFrames`, `LocalDeckMode`) instead of passing theme/mode parameters deeply.
7. **Session Duration**: If modifying `Deck.kt`, ensure `SlidesConstants.SESSION_DURATION` is respected for the timer.

## Common Workflows

### Adding a New Slide
1. Create a new composable function in a sub-package of `:segments:demos`.
2. Open `SkiPresentationSlides.kt`.
3. Add a `slide { ... }` block to the `generateDeck` builder.
4. Register the new slide function.

### Customizing the Theme
Global styles are in `:shared:design`. To apply a custom look to a specific slide, use `AppTheme` or override `CompositionLocal` values within that slide's scope.
