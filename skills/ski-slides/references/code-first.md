# Code-first decks (Kotlin)

A code-first Ski deck is a Kotlin program: each slide is a `@Composable`, and the deck is a list built with the `generateDeck` DSL. Read [principles.md](principles.md) first; this file is about how to apply it in code.

> **Where the deck runtime lives today.** `generateDeck`, `SlideConfig`, `DeckNavigator` and the components are in published artifacts (`io.github.donald-okara:ski`, which brings in the domain module). The presentation shell (`Deck()`, the presenter panel, shortcuts) lives in the `ski` repo's `:composeApp` module. So code-first decks are normally written in a copy of the Ski template, not in an arbitrary project. Check which situation you're in before adding dependencies.

## Where things go

In the Ski template (package names don't match folder names):

| What | Where | Package |
|---|---|---|
| The slide list | `composeApp/src/commonMain/kotlin/ke/don/ski/presentation/ui/SkiPresentationSlides.kt` | `ke.don.ski.presentation.ui` |
| Speaker notes | `composeApp/.../presentation/ui/SlidesNotes.kt`, one constant per slide | same |
| Slide composables | `segments/demos/src/commonMain/kotlin/ke/don/demos/` (or a sub-package per talk section) | `ke.don.demos…` |
| Ski components | `shared/components` (published as `ski`) | `io.github.donald_okara.components…` |
| Deck models, reveal step, deck mode | `core/domain` | `ke.don.domain` |

Put new slide composables in the existing segments module or a sub-package of it, not in a new Gradle module. In a template copy, the user can of course organise segments however they like; follow what the project already does.

## The slide list

```kotlin
@Composable
fun skiPresentationSlides(sessionDuration: Duration = SESSION_DURATION): List<SlideConfig> {
    val timerController = rememberTimerController(sessionDuration)

    return remember(timerController) {
        generateDeck(timerController = timerController) {
            slide("Introduction", transition = ScreenTransition.Fade, notes = introductionNotes, showFooter = false) {
                IntroductionScreen()
            }
            slide("Hoist the State", notes = hoistStateNotes, steps = 3) {
                HoistStateSlide()
            }
            slide("Live Demo", notes = liveDemoNotes) {
                CounterDemoSlide()
            }
        }
    }
}
```

`slide(...)` parameters:

| Parameter | Use |
|---|---|
| `label: String` | Short name for the table of contents and header (2–4 words). |
| `notes: List<AnnotatedString>?` | Speaker notes. Pass a named constant from `SlidesNotes.kt`, never an inline `listOf(...)`. |
| `transition: ScreenTransition` | `Horizontal` (default), `Fade`, `Vertical`, `None`. |
| `steps: Int` | Number of reveal steps on this slide (default 0). See "Reveals". |
| `showFooter: Boolean` | `false` hides the footer (the label chip and timer) on this slide. Use it for title and questions slides. |
| `frame`, `header`, `footer` | **Ignored by the default `Deck()`**, which draws one shared frame around every slide (`shareFrame = true`) and only reads `showFooter`. They only take effect when the deck doesn't share a frame. Don't use `footer = null` to hide the footer; it does nothing. |
| content lambda | **One call to a named slide composable.** Don't put layout code inline here; the slide list should read like a table of contents. |

Session length: the timer comes from `SlidesConstants.SESSION_DURATION`. Pass a different `sessionDuration` to `skiPresentationSlides` if the talk is a different length; don't hard-code durations elsewhere. `rememberTimerController(sessionDuration)` is defined at the bottom of `SkiPresentationSlides.kt`.

### A second deck, and choosing which one runs

To keep several talks in one project, give each its own deck function next to `skiPresentationSlides`, with a default length that suits it:

```kotlin
@Composable
fun hoistingTalkSlides(sessionDuration: Duration = 10.minutes): List<SlideConfig> { /* same shape as above */ }
```

Put its notes in their own file (`HoistingTalkNotes.kt`), and its slide composables in their own sub-package of the segments module (`ke.don.demos.hoisting`).

The entry points pick the deck. To present a different one, change the `slides = …` line in:
- desktop: `desktopApp/src/jvmMain/kotlin/ke/don/ski/main.kt` (`val slides = skiPresentationSlides()`);
- web: `webApp/src/commonMain/kotlin/ke/don/ski/web/WebMain.kt` (`DeckWebImpl`).

Change both if the talk will be given from either.

## Writing a slide composable

```kotlin
@Composable
fun HoistStateSlide(modifier: Modifier = Modifier) {
    SplitSlide(
        modifier = modifier,
        lead = {
            SlideTitle(title = "Hoist state to test it", kicker = "State")
            RevealBulletList(
                items = listOf(
                    "The composable takes value + onValueChange",
                    "The caller owns the state",
                    "Tests drive it without a UI",
                )
            )
        },
        details = {
            KotlinCodeViewer(
                code = """
                    @Composable
                    fun Counter(count: Int, onIncrement: () -> Unit) {
                        Button(onClick = onIncrement) { Text("Count: ${'$'}count") }
                    }
                """.trimIndent()
            )
        },
    )
}
```

Title, section and question slides have no dedicated component. Use `SlideTitle` in a padded `Box`, and pass `showFooter = false` in the slide list:

```kotlin
@Composable
fun TalkTitleSlide(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(Values.Dimens.largePadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        SlideTitle(title = "Hoist state, test anything", kicker = "Compose")
    }
}
```

- One public `@Composable` per slide, named after the slide (`HoistStateSlide`), with a `modifier: Modifier = Modifier` parameter.
- Use Ski's building blocks (see [components.md](components.md)) before writing custom `Column`/`Row` layouts.
- Use spacing from `Values.Dimens` (`smallPadding`, `mediumPadding`, `largePadding`) and colours/typography from `MaterialTheme`, rather than hard-coded `dp` values and colours.
- Read the deck mode with `LocalDeckMode.current` only if a slide really must behave differently in the presenter view (`DeckMode.Local`) and the audience window (`DeckMode.Presenter`). That's rare.

## Reveals

Reveals are driven by the deck, not by the slide's own state:

1. Give the slide a step count: `slide("Hoist the State", steps = 3) { ... }`.
2. Inside the slide, use the reveal components. They read `LocalRevealStep`, which the deck sets for the current slide:
   - `RevealBulletList(items)`: bullet `i` appears at step `i + 1`. Set `steps = items.size`.
   - `RevealItem(index = n) { ... }`: anything else; appears at step `n` (1-based) and keeps its space while hidden.
3. For custom reveal logic, read the step yourself: `val step = LocalRevealStep.current`, then show or animate based on `step >= n`. Outside a deck (in the gallery or a preview), `LocalRevealStep` is `Int.MAX_VALUE`, so everything shows.

Next moves through steps 1..`steps` and then to the next slide. Previous goes back step by step, and entering a slide backwards starts at its last step. Both windows share the step, so they stay in sync.

**Don't** use `remember { mutableStateOf(0) }` plus buttons or timers for presenter-paced reveals. Each window has its own copy of local state, so the audience would see something different from the presenter. (The template's "Stateful Reveals" demo slide uses local state on purpose: it demonstrates an interactive button inside a live demo, which is the case where local state *is* right.)

## Code on a slide

- `KotlinCodeViewer(code = ..., textScale = ..., shouldFoldLambdas = ...)` for code inside a layout. **Check two defaults:**
  - `shouldFoldLambdas = true` collapses every `{ … }` block, *including function bodies*. A snippet like `fun Counter() { … }` then shows only its signature. Pass `shouldFoldLambdas = false` whenever the body is the point, which is most of the time on a slide.
  - `textScale = 0.75f` is small on a projector. Use `1f` or more for a snippet that's the focus of the slide.
  - In half of a `SplitSlide` or `DemoLayout` at `textScale = 1f`, lines longer than about 45 characters wrap. Break long lines yourself (one argument per line) so the wrap doesn't land mid-expression.
- `KotlinCodeViewerCard(darkTheme, toggleFocus, toggleTheme) { code }` for a standalone card with a theme toggle and a focus mode (`FocusKotlinViewer`) that the presenter can open.
- `DemoLayout(title, explanation, code) { liveContent() }` for the standard demo slide: title and points on the left, a code card under them, and the live result on the right. **This is the default for "here's the code and here's what it does".**
- Keep snippets to ~15 lines; use `trimIndent()`; escape `$` in raw strings as `${'$'}`.
- To walk through code in steps today, give each part its own viewer inside a `RevealItem`, and set the slide's `steps` to the number of parts. A line-focus feature for the viewer is planned in the engine plan (01, Task 1.9b).

```kotlin
// slide("Hoisted Code", notes = hoistedCodeNotes, steps = 2) { HoistedCodeSlide() }
details = {
    RevealItem(index = 1) { KotlinCodeViewer(code = counterCode) }        // step 1: the stateless composable
    RevealItem(index = 2) { KotlinCodeViewer(code = counterScreenCode) }  // step 2: the caller that owns the state
}
```

  Write one note line per step that says what that part is, so the presenter knows what Next will show.

## Showing a composable

```kotlin
@Composable
fun CounterDemoSlide(modifier: Modifier = Modifier) {
    DemoLayout(
        title = "A hoisted counter",
        explanation = listOf("The screen owns the state", "Counter only renders it"),
        code = "Counter(count = count, onIncrement = { count++ })",
        modifier = modifier,
    ) {
        DeviceFrame(spec = DeviceCatalog.Pixel8) {
            var count by remember { mutableIntStateOf(0) }   // fine: the presenter taps this live
            Counter(count = count, onIncrement = { count++ })
        }
    }
}
```

- Devices: `DeviceCatalog.Pixel8`, `GalaxyS26`, `IPhone17`, `PixelFold`. Set orientation through `DeviceSpec.orientation` (`PORTRAIT`, `LANDSCAPE`, `HALF_OPENED`).
- Several states side by side: put several `DeviceFrame`s in a `Row`, or use `HorizontallySegmentedScreen` / `VerticallySegmentedScreen` (resizable panes the presenter can drag).
- Video of a component (e.g. a recording from a real device): `VideoSlide(videoUri, title, explanation, code)` from the demos module.

## Speaker notes

In `SlidesNotes.kt`, one constant per slide, named `<slideName>Notes`:

```kotlin
val hoistStateNotes = listOf(
    AnnotatedString("Last slide showed the bug; here's the fix in three steps."),
    AnnotatedString("Press Next for each point. The key idea is that the caller owns the state."),
    AnnotatedString("Takeaway to return to: a composable that takes its state is a composable you can test."),
)
```

Follow the notes rules in [principles.md](principles.md). The full guide is `docs/speaker-notes.md` in the template.

## Deck-wide look

`Deck()` (in `composeApp/.../ski/Deck.kt`) sets the frames (`FrameBuilder().setFrame { snake }` / `{ basic }`), the background (`BackgroundBuilder` with a `Pattern`: `Wavy`, `DiagonalWavy`, `AnimatedDiagonalWavyBackground`, `PerspectiveFloor`) and the decorator image. Change these there for the whole deck, not per slide. Theme colours and typography live in `shared/design` (`AppTheme`).

## Running and checking

```shell
./gradlew :desktopApp:run                       # presenter panel + audience window
./gradlew :webApp:wasmJsBrowserDevelopmentRun   # browser; opens on the gallery, press G for the deck; allow popups for the audience window
./gradlew :desktopApp:runGallery                # browse components with examples
```

Before finishing, run the desktop app and step through every changed slide in the presenter panel, checking that notes, step counts and the audience window all match. Look at how each code snippet actually renders: folding and text size are the usual surprises. If you can't run it, say so rather than claiming it works.

When you can only drive a browser (no desktop window): the web presenter tab opens the audience view with `window.open`, which some embedded browsers turn into a same-tab navigation, leaving only `/?slides` views. The audience view follows `localStorage`, so from a second tab on the same origin you can move it with `localStorage.setItem('deckState', JSON.stringify({slideIndex: 3, direction: 'Forward', step: 1}))`. This checks slides and steps but not the presenter panel (notes, timer).
