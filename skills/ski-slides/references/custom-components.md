# Showcasing the user's own composables

> **Status: outline.** `@SkiComponent`, the KSP processor and the Ski Gradle plugin are planned (`docs/engine/02-custom-components.md` in the `ski` repo) but not built yet. Until then, showcase composables in a **code-first deck** by calling them directly inside a slide composable (see [code-first.md](code-first.md), "Showing a composable"). That works today.

Ski's core job is putting a developer's own component on a slide quickly. There are three routes, from most to least control.

## 1. Code-first (works today)

Call the composable inside a named slide composable, usually inside `DemoLayout` with its code beside it, and in a `DeviceFrame` for mobile UI. Show its states with steps (`RevealItem`) or side by side. Local state is fine for the interactions the presenter does live.

## 2. As a slide element: `@SkiComponent` (planned)

The user annotates a composable in their own project:

```kotlin
@SkiComponent(
    id = "rating-bar",
    description = "Star rating with an animated fill. Good for showing state changes.",
    steps = 1,
)
@SkiExample(name = "Empty", paramsJson = """{"stars": 0}""")
@SkiExample(name = "Full", paramsJson = """{"stars": 5}""")
@Composable
fun RatingBarSlide(
    @SkiParam("Number of filled stars", min = 0.0, max = 5.0) stars: Int = 3,
    animated: Boolean = true,
    modifier: Modifier = Modifier,
) { RatingBar(stars, animated, modifier) }
```

Guidance when helping someone write one:
- Wrap the real component in a small **showcase function** with simple, serializable parameters. Don't annotate the production component itself if it takes lambdas, state holders or view models.
- Supported parameter types: strings, numbers, booleans, enums, lists, `@Serializable` data classes, `SkiColor`, `SkiDp`, `SkiAsset`. Everything else needs a default.
- `id` is permanent once decks use it. In a library meant for others, namespace it (`acme/rating-bar`).
- `description` is for the AI as much as for people: say what it shows and when it's useful.
- Add `@SkiExample`s for the states worth presenting. The studio's AI and the component browser start from them.
- If the component animates through stages, declare `steps` and read `LocalRevealStep` inside it, so Next drives it in both windows.

Then, in a deck, it's a `component` element (see [deck-json.md](deck-json.md)):

```json
{ "type": "component", "id": "e7", "componentId": "rating-bar", "params": { "stars": 5 }, "frame": { "type": "device", "deviceId": "pixel8" } }
```

## 3. Running it

| Command (planned, in the user's project) | What it does |
|---|---|
| `./gradlew skiPreview` | Opens the deck, or a component browser, with Compose Hot Reload. Free. The quickest "show it now". |
| `./gradlew skiStudio` | Builds a component pack and opens it in the installed Ski Studio; add `-t` to reload on every change. |
| `./gradlew skiPack` | Builds `build/ski/<project>.skipack` to load or share. |
| `./gradlew skiWebPlayer` | Builds a web player that includes the components, for publishing decks that use them. |
| `./gradlew skiDoctor` | Checks versions against the installed studio, JVM/wasm targets, and prints fixes. |

## Good showcase slides

- Code on the left, live component on the right, in a device frame for mobile UI.
- Three or four states, revealed one per step or side by side: empty, typical, edge case, error.
- Notes say exactly what to do live ("Tap the third star, then rotate") and what the demo doesn't prove (e.g. "This runs on desktop JVM; Android rendering may differ slightly").
- Android-only components can't run in the desktop preview. Suggest moving the showcase wrapper to a KMP module, or fall back to a video or screenshots from the device.
