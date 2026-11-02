# Phase 1: the engine (slides as data)

> Lives in **`ski`** (this repo) and ships as published artifacts (see [README](README.md), Task 0). Ski Studio consumes it. References to studio plans (02–06) point at [`docs/studio/`](../studio/README.md), which moves to the `ski-studio` repo.

Goal: a deck is a JSON document that any Ski target can draw with the existing components, and that can be changed only through typed, undoable operations.

Done when (milestone M1): the demo deck from `skiPresentationSlides()` is rewritten as `samples/demo.ski.json`, and rendering it gives the same slides, reveal steps, notes and transitions as the Kotlin version. Some slides (video, whiteboard demos) will need the escape hatch described under "Built-in slides".

## How it fits the current code

Today the flow is:

```
skiPresentationSlides()  ->  generateDeck { slide(...) { MyComposable() } }  ->  List<SlideConfig>
                                                                                 │
Deck(mode, slides, navigator)  ->  PresentationDeck  ->  DeckHost (AnimatedContent on currentIndex)
```

`SlideConfig` (`core/domain/.../SlideConfig.kt`) holds a `content: @Composable () -> Unit`, a `steps: Int` for reveals, notes as `List<AnnotatedString>`, a `transition`, a `frame`, header/footer, and a `TimerController`.

The engine adds one step in front and leaves everything after it alone:

```
DeckDocument (JSON)  ->  rememberSlides(document)  ->  List<SlideConfig>  ->  Deck(...)  (unchanged)
```

So `DeckNavigator`, `DeckHost`, `PresentationDeck`, the two-window setup, reveals via `LocalRevealStep`, the timer and the shortcuts keep working with no changes. This is the main reason the plan is cheap: the presentation runtime already exists.

## Module: `:engine:model`

Pure Kotlin + `kotlinx.serialization`. No Compose, so it can be shared with the phone and tested on the JVM. Package: `ke.don.engine.model`.

### Task 1.1: build setup

- Add a `ke.don.ski.jvmLibrary` convention plugin in `build-logic/convention/src/main/kotlin/ke/don/ski/JvmLibrary.kt` for JVM-only modules in this repo (`:engine:export`). The studio repo keeps its own conventions. Register it in `build-logic/convention/build.gradle.kts` and add it to `[plugins]` in `gradle/libs.versions.toml` the same way `kotlinMultiplatformLibrary` is.
- Create `:engine:model` with the existing `kotlinMultiplatformLibrary` plugin, `kotlin-serialization`, and `publishedLibrary` (README, Task 0.1) with artifact `ski-engine-model`. It should not apply `composeMultiplatformPlugin`. Apply `publishedLibrary` to `:engine:ops`, `:engine:renderer` and `:engine:export` the same way when you create them.
- Add `include(":engine:model")` to `settings.gradle.kts`.
- Add `kotlin-test` to `commonTest` and confirm `./gradlew :engine:model:jvmTest` runs one trivial test.

### Task 1.2: the document schema

Start with this shape. Field names are what the AI will see in JSON, so keep them short and obvious.

```kotlin
@Serializable
data class DeckDocument(
    val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
    val id: String,                       // UUID, stable across saves
    val title: String,
    val theme: DeckTheme = DeckTheme(),
    val sessionMinutes: Int = 45,         // feeds TimerController; default mirrors SlidesConstants.SESSION_DURATION
    val slides: List<Slide>,
    val assets: List<Asset> = emptyList(),
)

@Serializable
data class Slide(
    val id: String,                       // stable ID; ops address slides by ID, never by index
    val label: String,                    // TOC + header, same as SlideConfig.label
    val layout: SlideLayout = SlideLayout.Stack,
    val elements: List<Element>,
    val notes: List<String> = emptyList(),// plain text or light markdown, one entry per paragraph
    val transition: Transition = Transition.Horizontal,
    val showFooter: Boolean = true,
    val frame: FrameRef = FrameRef.Default,
)
```

`steps` is **not stored**. The renderer works it out as the highest `revealAt` among the slide's elements (see "Reveals"). Storing it would let the AI write a value that disagrees with the elements.

Layouts (start small, add when a real deck needs one):

```kotlin
@Serializable sealed interface SlideLayout {
    @Serializable @SerialName("stack")   data object Stack : SlideLayout                       // Column
    @Serializable @SerialName("split")   data class Split(val leadWeight: Float = 1f) : SlideLayout  // SplitSlide: first half of elements = lead
    @Serializable @SerialName("center")  data object Center : SlideLayout                      // title/section slides
    @Serializable @SerialName("segments") data class Segments(val vertical: Boolean) : SlideLayout // Horizontally/VerticallySegmentedScreen
}
```

For `Split`, put an explicit `column: Int` on elements rather than "first half". Pick one before writing code; `column` is clearer to the AI.

Elements: one sealed type, one subtype per existing component. Each subtype maps to exactly one composable in `:shared:components`. The first batch is the components ported from the droidcon deck, because they already take plain data:

| Element (`@SerialName`) | Fields | Renders with |
|---|---|---|
| `title` | `text`, `kicker?` | `SlideTitle` |
| `bullets` | `items: List<String>`, `reveal: Boolean` | `BulletList`, or `RevealBulletList` when `reveal` |
| `text` | `text` (light markdown) | `Text` with the theme's body style |
| `code` | `code`, `language = "kotlin"`, `foldLambdas = true`, `focusSteps`, `source?` (see Task 1.9b) | `KotlinCodeViewer` |
| `callout` | `title`, `body` | `CalloutCard` |
| `commit` | `sha` | `CommitTag` |
| `stat` | `value`, `unit`, `label`, `direction`, `improvement`, `sha?`, `source?` | `StatTile` |
| `columnChart` / `barChart` | `points: List<{label, value}>`, `caption?` | `ColumnChart` / `BarChart` |
| `timeline` | `stages`, `gapAfterIndex?`, `gapTitle` | `EvolutionTimeline` |
| `swatches` | `label`, `swatches: List<{name, hex}>` | `SwatchRow` |
| `image` | `assetId`, `shape`, `fit` (see Task 1.3; GIF assets animate) | `Image` / `ExpressivePictureFrame` / `AnimatedImage` |
| `video` | `assetId`, `autoplay`, `loop`, `muted`, `startMs?`, `endMs?` (see Task 1.3) | `composemediaplayer` |
| `device` | `deviceId` (from `DeviceCatalog`), `orientation`, `child: Element` | `DeviceFrame` |
| `component` | `componentId`, `params: JsonObject`, `frame` | `ComponentRegistry` lookup: Ski's built-ins and **your own `@SkiComponent` composables** ([02-custom-components.md](02-custom-components.md)) |

Every element also has:

```kotlin
val id: String          // stable, for update_element / remove_element
val revealAt: Int = 0   // 0 = always visible; n = appears at reveal step n
```

Colors: components take `*Colors` objects (`SlideBlockDefaults.titleColors()`, `ChartDefaults.colors()`…). Do **not** expose those per element in v1. Use the deck theme (below). Add a per-element `accent: ColorRole?` later if users ask.

Theme:

```kotlin
@Serializable
data class DeckTheme(
    val darkMode: Boolean = true,
    val seedColor: String? = null,          // hex; null = AppTheme default from :shared:design
    val background: BackgroundRef = BackgroundRef.AnimatedDiagonalWavy,
    val decoratorAssetId: String? = null,   // replaces the hardcoded ANDROID_ROBOT in Deck.kt
    val mainFrame: FrameRef = FrameRef.Snake,
    val guidesFrame: FrameRef = FrameRef.Basic,
)
```

`BackgroundRef` mirrors `Pattern` (`Wavy`, `DiagonalWavy`, `AnimatedDiagonalWavyBackground`, `PerspectiveFloor`, plus `None`). `FrameRef` mirrors `defaultSkiFrames()` (`Snake`, `Basic`, `None`, `Default` = use the theme's main frame).

### Task 1.3: assets

Images and video can't go inline in JSON. A deck on disk is a folder (or a zip with the `.ski` extension, see studio plan [02](../studio/02-studio-shell.md), Task 2.2):

```
my-talk.ski/
  deck.json
  assets/
    3f2a….png
```

Users add assets through the Assets side panel in the studio ([02](../studio/02-studio-shell.md), Task 2.7). The engine only defines the model and how assets render; importing files is the studio's job. Three kinds are supported:

```kotlin
@Serializable
data class Asset(
    val id: String,              // UUID; file is stored as assets/<id>.<ext>
    val kind: AssetKind,         // Image, Gif, Video
    val name: String,            // what the user sees; editable, also how the AI refers to it
    val fileName: String,        // original file name, for display only
    val mimeType: String,        // image/png, image/jpeg, image/webp, image/svg+xml, image/gif, video/mp4, video/webm, video/quicktime
    val sha256: String,          // dedupe on import + phone cache key
    val widthPx: Int? = null,
    val heightPx: Int? = null,
    val durationMs: Long? = null,     // gif + video
    val description: String = "",     // alt text; also given to the AI so it can pick the right asset
    val posterAssetId: String? = null // video: generated still frame for thumbnails and the phone
)
```

The renderer gets an `AssetResolver` interface instead of reading files itself:

```kotlin
interface AssetResolver {
    @Composable fun painter(assetId: String): Painter?          // images, and the first frame of a GIF
    @Composable fun AnimatedImage(assetId: String, modifier: Modifier, playing: Boolean)  // GIF
    @Composable fun Video(assetId: String, modifier: Modifier, autoplay: Boolean, loop: Boolean, muted: Boolean)
}
```

The desktop implements it from the unpacked deck folder. The studio's phone remote gets assets over its own connection ([04](../studio/04-mobile-remote.md)) or shows the poster frame. The code-first path can implement `AssetResolver` over Compose resources.

Element types for assets (add to the table in Task 1.2):

| Element (`@SerialName`) | Fields | Renders with |
|---|---|---|
| `image` | `assetId`, `shape: plain/expressive`, `fit: contain/cover` | `Image` / `ExpressivePictureFrame`. When the asset is a GIF, `AnimatedImage` |
| `video` | `assetId`, `autoplay = true`, `loop = false`, `muted = true`, `startMs?`, `endMs?` | `composemediaplayer` (already in `libs.versions.toml`, used by `VideoSlide` in `:segments:demos`) |

Playback rules:
- GIFs and autoplay videos start when their slide (and reveal step) becomes visible and pause when it leaves. Use `LocalRevealStep` and the current slide index so off-screen slides in `AnimatedContent` don't keep decoding.
- Thumbnails (Task 1.11) show the poster or first frame and never play.
- Only the audience window plays audio. The presenter window plays muted so the sound doesn't come out twice.

Animated GIF decoding: Compose Multiplatform has no built-in animated GIF support. On JVM, iOS and web, use Skia's `org.jetbrains.skia.Codec` (Skiko), which exposes frames and frame durations. On Android, use `ImageDecoder` + `AnimatedImageDrawable`. Put this behind an `expect`/`actual` in `:engine:renderer`. Prototype this early, since it's the least certain piece of the asset work.

### Task 1.4: validation

`fun DeckDocument.validate(): List<ValidationError>` in `:engine:model`. Ops call it before committing; the AI receives the errors as tool results so it can fix its own mistakes. Check at least:

- IDs unique within the deck (slides) and within each slide (elements).
- `revealAt` values form 0..n with no gaps (a gap means a "next" press that shows nothing).
- `assetId` references exist in `assets`, and the kind matches the element (`video` elements need a video asset; `image` elements need an image or GIF).
- `deviceId` exists in `DeviceCatalog` (needs the catalog IDs exposed as data; small change in `:shared:components`).
- Text length limits (titles ≤ 80 chars, bullets ≤ 8 items) as **warnings**, not errors. These encode the authoring rules so the AI gets nudged toward readable slides.
- `slides` not empty (`DeckNavigator` requires at least one slide and throws otherwise).

### Task 1.5: schema versions and migrations

- `const val CURRENT_SCHEMA_VERSION = 1`.
- `object DeckMigrations { fun migrate(json: JsonObject): JsonObject }` runs step by step (v1→v2→…) on the raw JSON before decoding.
- Decode with `Json { ignoreUnknownKeys = true; classDiscriminator = "type" }` so a newer file opened in an older app degrades instead of crashing. Unknown element types should decode to an `Element.Unknown` placeholder (custom serializer), which the renderer draws as a grey "Update Ski to see this element" box.

Tests for 1.2–1.5: JSON round-trip of every element type, a golden `samples/demo.ski.json` that must keep decoding, validation cases, and one fake v0→v1 migration to prove the mechanism.

## Module: `:engine:ops`

Pure Kotlin. Package `ke.don.engine.ops`. Depends on `:engine:model`.

### Task 1.6: operations

```kotlin
@Serializable sealed interface DeckOp {
    @Serializable @SerialName("add_slide")       data class AddSlide(val slide: Slide, val afterSlideId: String?) : DeckOp
    @Serializable @SerialName("remove_slide")    data class RemoveSlide(val slideId: String) : DeckOp
    @Serializable @SerialName("move_slide")      data class MoveSlide(val slideId: String, val afterSlideId: String?) : DeckOp
    @Serializable @SerialName("update_slide")    data class UpdateSlide(val slideId: String, val patch: SlidePatch) : DeckOp   // label, layout, notes, transition…
    @Serializable @SerialName("add_element")     data class AddElement(val slideId: String, val element: Element, val afterElementId: String?) : DeckOp
    @Serializable @SerialName("update_element")  data class UpdateElement(val slideId: String, val element: Element) : DeckOp  // replace by element.id
    @Serializable @SerialName("remove_element")  data class RemoveElement(val slideId: String, val elementId: String) : DeckOp
    @Serializable @SerialName("set_theme")       data class SetTheme(val theme: DeckTheme) : DeckOp
    @Serializable @SerialName("set_title")       data class SetTitle(val title: String) : DeckOp
    @Serializable @SerialName("add_asset")       data class AddAsset(val asset: Asset) : DeckOp          // Assets panel only, not exposed to the AI
    @Serializable @SerialName("update_asset")    data class UpdateAsset(val assetId: String, val name: String?, val description: String?) : DeckOp
    @Serializable @SerialName("remove_asset")    data class RemoveAsset(val assetId: String) : DeckOp    // rejected while any element references it
}
```

`fun DeckDocument.apply(op: DeckOp): OpResult` where `OpResult` is `Applied(newDoc, inverse: DeckOp)` or `Rejected(errors)`. Every op produces its own inverse at apply time (e.g. `RemoveSlide` returns an `AddSlide` with the removed slide and its old position). That is what makes undo trivial.

Ops are serializable on purpose: the studio's AI layer, its MCP server and a future collaborative mode all send the same JSON. That makes `DeckOp` part of the public API: renaming an op or a field is a breaking change for every studio version in the wild, so treat `@SerialName`s as frozen once published.

### Task 1.7: transactions and history

- `DeckTransaction(label: String, ops: List<DeckOp>)` applies all or nothing. One AI turn is one transaction, so one Undo reverts the whole turn ("Added 3 slides about state hoisting").
- `DeckHistory` holds a past stack and a future stack of transactions, capped (e.g. 200). Exposes `StateFlow<DeckDocument>`, `undo()`, `redo()`, `canUndo`, `canRedo`.
- `DeckEditor` is the single entry point the app uses: `val document: StateFlow<DeckDocument>`, `fun commit(tx): Result`, `undo()`, `redo()`. Nothing else in the app holds a mutable document.

Tests: every op's inverse restores the original document (property-style test over the sample deck), transaction rollback on a failing op, undo/redo order.

## Module: `:engine:renderer`

Compose Multiplatform, all targets. Package `ke.don.engine.renderer`. Depends on `:engine:model`, `:core:domain`, `:shared:components`, `:shared:design`, `:shared:resources`.

### Task 1.8: document → `SlideConfig`

```kotlin
@Composable
fun rememberSlides(
    document: DeckDocument,
    assets: AssetResolver,
    registry: ComponentRegistry = ComponentRegistry.Default,
): List<SlideConfig>
```

- Creates one `TimerController` with `document.sessionMinutes` (same pattern as `rememberTimerController` in `SkiPresentationSlides.kt`).
- Maps each `Slide` to `SlideConfig(label, notes = notes.map(::parseLightMarkdown), transition, frame, showFooter, steps = maxRevealAt, content = { SlideBody(slide) })`.
- Keys `remember` on `document` so a new document gives new configs. **Watch out:** `DeckNavigator` is built with a fixed `List<SlideConfig>` (`DeckNavigator(slides)`). When the AI adds or removes slides, the navigator must be rebuilt or updated. Plan: add `fun updateSlides(newSlides: List<SlideConfig>)` to `DeckNavigator` in `:core:domain` that clamps `currentIndex`/`currentStep`, and make `slides` a `mutableStateOf`. Keep the constructor as is so the code-first path does not change.
- Also: `currentIndex` should follow the slide the user is looking at by **ID** across edits. When a slide is inserted before the current one, the preview should not jump. `DeckEditor` reports the current slide ID; the renderer maps it back to an index.

### Task 1.9: element composables

`@Composable fun ElementView(element: Element)` with a `when` over element types, wrapping each in `RevealItem(index = element.revealAt)` when `revealAt > 0`. `RevealItem` already reads `LocalRevealStep`, so reveals work in both windows with no new code.

`SlideBody(slide)` arranges `ElementView`s according to `slide.layout`.

Map deck theme → `Deck()` parameters. `Deck()` in `composeApp/.../Deck.kt` currently hardcodes the frames, the background pattern and `Resources.Images.ANDROID_ROBOT`. Refactor it to take `mainFrame`, `guidesFrame` and `background` as parameters with today's values as defaults, so the code-first deck is unchanged and the studio passes values from `DeckTheme`. This refactor is small and should be its own PR.

### Task 1.9b: code elements built for demos

Ski is mostly used to demo code, and the studio's built-in skills (03, Task 3.8) lean on these. Extend the `code` element:

```kotlin
@Serializable @SerialName("code")
data class Code(
    override val id: String,
    override val revealAt: Int = 0,
    val code: String,
    val language: String = "kotlin",
    val foldLambdas: Boolean = true,
    val title: String? = null,                         // file name shown above the snippet
    val focusSteps: List<LineFocus> = emptyList(),     // reveal step n highlights focusSteps[n-1]; dims other lines
    val source: CodeSource? = null,                    // where the snippet came from, for "Refresh from project"
) : Element

@Serializable data class LineFocus(val from: Int, val to: Int)                 // 1-based, inclusive
@Serializable data class CodeSource(val path: String, val fromLine: Int, val toLine: Int, val commit: String? = null)
```

- `focusSteps` count toward the slide's `steps`, so pressing Next walks through the code a few lines at a time. `KotlinCodeViewer` has no line highlighting today: add a `focusedLines: IntRange?` parameter to it in `:shared:components` (dim other lines with an animated alpha). This is a library feature code-first users get too, so add a gallery entry.
- `language`: `KotlinCodeViewer` only highlights Kotlin. For other languages, show plain monospaced text in v1 and plan a small multi-language highlighter later. Many talks show Gradle KTS, XML, Swift or shell next to Kotlin.
- `source` is metadata only. The engine never reads files; the studio uses it to re-pull code from an attached project folder (03, Task 3.8).

### Task 1.10: built-in slides (escape hatch)

Some current slides are real composables that don't fit a data model (video, whiteboard, the device gallery, `SlidesAreComposablesDemo`). The `component` element covers them **and your own composables**, through one `ComponentRegistry` filled from `@SkiComponent` annotations. That is specified in [02-custom-components.md](02-custom-components.md) (Phase 1b), which replaces the `BuiltinRegistry` idea from earlier drafts.

For Phase 1 itself, build `ComponentRegistry` with hand-written `ComponentSpec`s for Ski's built-ins, so M1 doesn't wait on the KSP processor. Phase 1b then generates them.

### Task 1.11: thumbnails

The studio's slide strip and the phone's next-slide preview both need small renders. Use a fixed design size (1920×1080) scaled with `Modifier.graphicsLayer` inside a `Box` of the thumbnail size, with animations disabled through a `LocalThumbnailMode` composition local (entrance animations in `SlideTitle`/`BulletList` would otherwise run in every thumbnail). Reveal step for thumbnails = the max step, so everything is visible.

### Task 1.12: gallery entry

Add a "Rendered from JSON" example to the gallery (`composeApp/.../gallery/data/`) that shows a slide's JSON next to its rendering. It doubles as a manual test and as the docs the AI prompt will point to.

## Module: `:engine:export` (can wait until the studio has chat editing)

JVM only. `fun DeckDocument.toKotlin(packageName: String): Map<String, String>` (file name → source). Output follows `context.md`: one named composable per slide, `generateDeck { slide(...) }` registration, `KotlinCodeViewer` for code, reveals using `RevealItem`. The exported project depends on the published `io.github.donald-okara:ski` artifact. Use KotlinPoet to avoid string-building bugs.

Test: export the sample deck, compile it in a Gradle test fixture, and check it builds.

## Module: `:webPlayer` (Task 1.13)

The web player keeps decks portable: a static site that renders any `deck.json` in the browser. The studio's "Publish to web / Deploy to Vercel" ([02](../studio/02-studio-shell.md), Task 2.8) ships this bundle with the deck.

- New app module `:webPlayer` (wasmJs, plus js as a fallback like `:webApp`). On load it fetches `deck.json` next to `index.html`, decodes it with `:engine:model`, and renders with `:engine:renderer`. Assets load from `assets/<id>.<ext>` through a web `AssetResolver`.
- Reuse the presenter/audience flow from `webApp/.../WebMain.kt`: the main tab is the presenter view (notes, timer) and opens `/?slides` as the audience popup, synced through `localStorage`. Move that sync code out of `:webApp` into a small shared web module so both apps use it.
- Ship a `vercel.json` with the rewrite-to-`index.html` rule, the same one `webApp`'s distribution already has.
- Show a friendly error page when `deck.json` is missing or has a newer `schemaVersion` than the player supports.
- **Release it as a versioned bundle:** extend `.github/workflows/publish.yml` to run `./gradlew :webPlayer:wasmJsBrowserDistribution`, zip `productionExecutable/` as `ski-web-player-<version>.zip`, and attach it to a GitHub release for the tag. The studio downloads the matching version at build time.
- Anyone can use it without the studio: drop a hand-written or AI-written `deck.json` and its `assets/` into the unzipped bundle and deploy, following the README's existing Vercel steps.

Add it to the engine README's split table and to the README "Deploy to Vercel" section.

## Converting the demo deck (M1)

Write `samples/demo.ski.json` by hand (or ask Claude with the schema) to mirror `skiPresentationSlides()`. Then add a temporary toggle in `desktopApp/.../main.kt` to load either the Kotlin deck or the JSON deck, and compare them window by window. Differences show where the schema or renderer still falls short.

## Risks

- **Animation parity.** Components like `SlideTitle` run entrance animations in `LaunchedEffect(Unit)`. When the document changes, recomposition must not restart animations on slides the user isn't editing. Key slides by `slide.id` in `DeckHost`'s `AnimatedContent`, not by index alone.
- **Schema growth.** Every new component tempts a new element type. Rule: core layout and text building blocks get element types; everything else, including Ski's richer built-ins, is a `@SkiComponent` (02).
- **Notes formatting.** `SlideConfig.notes` is `List<AnnotatedString>`. Keep the stored format to bold/italic/code spans so `parseLightMarkdown` stays small.
