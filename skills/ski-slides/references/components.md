# Ski components: what to reach for

Pick a building block from this list before writing custom layout code. Kotlin names are for code-first decks. The `deck.json` element names are the planned engine equivalents (see [deck-json.md](deck-json.md)).

> This catalog is written by hand for now. Once the engine exists, the element list and parameters will be generated from the code on each release, so they can't drift.

All Kotlin components are in `io.github.donald_okara.components.*` (artifact `io.github.donald-okara:ski`) unless noted. Each takes a `modifier` and, where it has colours, a `colors` parameter with defaults from the theme. Leave `colors` alone unless the slide is about colour.

## Text and structure

| Need | Kotlin | Element | Notes |
|---|---|---|---|
| Slide title with optional small label above | `text.SlideTitle(title, kicker = null)` | `title` | Fades and rises in once. Use a claim, not a topic. |
| Bullets that appear together | `text.BulletList(items)` | `bullets` (`reveal: false`) | Staggered entrance when the slide appears. |
| Bullets that appear one per Next press | `reveal.RevealBulletList(items)` | `bullets` (`reveal: true`) | Give the slide `steps = items.size`. |
| Anything revealed on a step | `reveal.RevealItem(index) { }` | any element's `revealAt` | `index` is 1-based; keeps its space while hidden. |
| The one line that matters | `text.CalloutCard(title) { content }` | `callout` | One per slide at most. |
| Cite a commit | `text.CommitTag(sha)` | `commit` | Short SHA in a monospace tag. |
| Two columns: idea + details | `text.SplitSlide(lead = { }, details = { })` | layout `split` | Lead on the left, details (code, demo, chart) on the right. |
| Simple title + points slide | `ke.don.demos.FeatureSlide(title, points)` (template's demos module) | `title` + `bullets` | For features easier to read than demo. |

## Code and demos

| Need | Kotlin | Element | Notes |
|---|---|---|---|
| Code inside a layout | `guides.code_viewer.KotlinCodeViewer(code, darkTheme, textScale, shouldFoldLambdas)` | `code` | Kotlin highlighting; folds lambdas by default. |
| Standalone code card with theme toggle + focus mode | `guides.code_viewer.KotlinCodeViewerCard(darkTheme, toggleFocus, toggleTheme) { code }` | `code` | Focus mode opens `FocusKotlinViewer` (zoomable). |
| Code + explanation + live result | `layout.DemoLayout(title, explanation, code) { live }` | layout `split` with `title`, `bullets`, `code` + content | **Default demo slide.** |
| Two or more resizable panes | `layout.HorizontallySegmentedScreen(initialSegments)` / `VerticallySegmentedScreen` | layout `segments` | `initialSegments` = list of weight to composable; the presenter can drag the dividers. |
| A phone/foldable around UI | `devices.DeviceFrame(spec = DeviceCatalog.Pixel8) { }` | `device` / component `frame: Device` | Catalog: `Pixel8`, `GalaxyS26`, `IPhone17`, `PixelFold`; `orientation` on `DeviceSpec`. |
| Video (recording, real device) | `ke.don.demos.VideoSlide(videoUri, title, explanation, code)` (template) | `video` | Player is `composemediaplayer`; bundled files avoid venue Wi-Fi problems. |
| Live typing / sketching during the talk | `guides.whiteboard.WhiteboardComponent(value, onValueChange)` | component `whiteboard` | Interactive; local state is fine here. |
| Something the presenter can pop out and resize | `layout.Focusable`, `layout.ResizableFocusable`, `draggable.DraggableBox` | – | For live demos only. |

## Data and history

| Need | Kotlin | Element | Notes |
|---|---|---|---|
| One headline metric with change direction | `stats.StatTile(value, unit, label, direction, improvement, sha, source)` | `stat` | Counts up on entry. Always add a note on what it doesn't measure. |
| Compare a few values | `charts.ColumnChart(points, caption)` / `charts.BarChart(points, valueSuffix)` | `columnChart` / `barChart` | `ChartPoint(label, value, highlight)`; grows in on entry. |
| How something evolved | `timeline.EvolutionTimeline(stages, gapAfterIndex, gapTitle)` | `timeline` | `TimelineStage(year, title, caption, details)`; optional "gap" marker. |
| Colours / palette | `swatches.SwatchRow(label, swatches)` | `swatches` | `swatches` = name to `Color` (hex in JSON). |
| Celebrate (end of a demo) | `charts.Confetti(trigger)` | component `confetti` | Use once per talk, if at all. |

## Images and decoration

| Need | Kotlin | Element | Notes |
|---|---|---|---|
| A photo/avatar in a playful shape | `picture.ExpressivePictureFrame(...)` | `image` (`shape: expressive`) | Speaker intro slides. |
| A "new" marker | `NewBadge(text = "NEW")` | – | Floats gently. |
| Slide frame | `FrameBuilder().setFrame { snake \| basic }` (`ke.don.domain.frames`) | theme `mainFrame` | Set deck-wide in `Deck()` / theme, not per slide. |
| Background | `backgrounds.BackgroundBuilder().setPattern(Pattern.…)` | theme `background` | Patterns: `Wavy`, `DiagonalWavy`, `AnimatedDiagonalWavyBackground`, `PerspectiveFloor`. Deck-wide. |

## Presenter-only (already wired by the deck)

`TimerComponent`, `NotesComponent`, `ShortcutsDictionary` and the flashcards are part of the presenter panel. Don't place them on slides, except a `ShortcutsDictionary` on a slide that's about the shortcuts themselves.

## When nothing fits

- **Code-first:** write a named composable using `MaterialTheme` and `Values.Dimens`, and keep it in the segments module. If it's reusable, consider proposing it for `:shared:components` with a gallery entry.
- **Data decks:** use a component from a loaded component pack, or ask the user to make one with `@SkiComponent` (see [custom-components.md](custom-components.md)). Don't fake it by stacking unrelated elements.
