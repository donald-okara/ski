# Phase 1b: your own composables on slides

Ski exists so a Compose developer can build a component and show it on a slide at a moment's notice. A data-driven engine alone can't do that, because it can only draw elements built into it. This plan closes the gap. Your composables become slide elements, and you can run decks from your own project with hot reload, using the toolchain you already have.

Lives in **`ski`**. Depends on Phase 1's `:engine:model` and `:engine:renderer` ([01-engine.md](01-engine.md)) and on publishing ([README](README.md), Task 0).

Done when (milestone M1b): in a sample project outside this repo, a composable annotated with `@SkiComponent` appears in a `deck.json` slide; `./gradlew skiPreview` shows it; editing the composable updates the slide without a restart; and `./gradlew skiWebPlayer` builds a web player that renders it.

## The three ways to show your composables

| Way | Who it's for | Your components | Needs |
|---|---|---|---|
| **Code-first** (today's Ski) | Full control, custom decks | Called directly in `slide { }` | `ski` artifacts + your IDE |
| **`./gradlew skiPreview`** (new, free) | "Show this component now" | Through `@SkiComponent`, in a `deck.json` or a code-first deck, with Compose Hot Reload | The Ski Gradle plugin |
| **Ski Studio + component pack** (paid app) | AI builds the deck around your components | Pack loaded into the installed studio; `./gradlew skiStudio` keeps it in sync | The Ski Gradle plugin + Ski Studio |

Code-first stays first-class. Nothing in this plan changes how today's decks are written.

## Modules

| Module | Artifact | Targets | What it holds |
|---|---|---|---|
| `:engine:annotations` | `ski-annotations` | all | `@SkiComponent`, `@SkiParam`, `@SkiExample`. No dependencies, so it's cheap to add to any module. |
| `:engine:renderer` (existing in plan) | `ski-engine-renderer` | all | `ComponentSpec`, `ComponentRegistry` (replaces `BuiltinRegistry` from 01, Task 1.10), `SkiComponentProvider` |
| `:engine:ksp` | `ski-ksp` | JVM | KSP processor that generates specs and registries |
| `:gradle-plugin` (included build, like `build-logic`) | plugin ID `io.github.donald-okara.ski` | Gradle | `skiPreview`, `skiPack`, `skiStudio`, `skiWebPlayer` tasks; wires KSP and dependencies |
| `:samples:custom-components` | not published | jvm, wasmJs | The M1b test project, built against Maven Local artifacts |

Publish the Gradle plugin to the Gradle Plugin Portal (or Maven Central with a plugin marker) from the same tag workflow.

## Task 1b.1: the annotation API

```kotlin
/** Makes a composable usable as a slide element. */
@Target(AnnotationTarget.FUNCTION)
annotation class SkiComponent(
    val id: String,                 // stable, kebab-case; what deck.json stores. Renaming breaks decks.
    val name: String = "",          // shown in the studio; defaults to the function name
    val description: String = "",   // what it shows and when to use it; the AI reads this
    val steps: Int = 0,             // reveal steps it handles itself via LocalRevealStep
)

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class SkiParam(val description: String = "", val min: Double = Double.NaN, val max: Double = Double.NaN)

@Repeatable
@Target(AnnotationTarget.FUNCTION)
annotation class SkiExample(val name: String, val paramsJson: String)
```

Example in a user's project:

```kotlin
@SkiComponent(
    id = "rating-bar",
    description = "Star rating with an animated fill. Good for showing state changes.",
    steps = 1,
)
@SkiExample(name = "Empty", paramsJson = """{"stars": 0}""")
@SkiExample(name = "Full", paramsJson = """{"stars": 5, "animated": true}""")
@Composable
fun RatingBarSlide(
    @SkiParam("Number of filled stars", min = 0.0, max = 5.0) stars: Int = 3,
    animated: Boolean = true,
    accent: SkiColor = SkiColor.Primary,
    modifier: Modifier = Modifier,
) { ... }
```

Rules the processor enforces (compile errors with clear messages):
- The function is `@Composable`, public, and top-level or in an `object`.
- Every parameter is a **supported type** or has a default value. A trailing `modifier: Modifier` is supplied by the host.
- Supported types:
  - `String`, `Int`, `Long`, `Float`, `Double`, `Boolean`;
  - enums;
  - `List<T>` of supported types;
  - `@Serializable` data classes;
  - `SkiColor` (theme role or hex) and `SkiDp` (float in dp);
  - `SkiAsset`, an asset reference, so components can take images or videos from the studio's Assets panel.
- Lambdas, `@Composable` slots, `State`, and other unsupported types must have defaults, and are hidden from the schema.
- `id` is unique across the compilation. Duplicates across packs are caught at load time (Task 1b.5).

Put `SkiColor`, `SkiDp` and `SkiAsset` in `:engine:model` so they serialize without Compose.

## Task 1b.2: what the processor generates

For each annotated function, in the same module:

```kotlin
// generated
@Serializable internal data class RatingBarSlideParams(val stars: Int = 3, val animated: Boolean = true, val accent: SkiColor = SkiColor.Primary)

internal val RatingBarSlideSpec = ComponentSpec(
    id = "rating-bar",
    name = "RatingBarSlide",
    fqName = "com.example.ui.RatingBarSlide",   // used by Kotlin export (01) to emit a direct call
    description = "...",
    steps = 1,
    paramsSchema = /* JSON Schema built from the params, with descriptions/min/max */,
    examples = listOf(ComponentExample("Empty", buildJsonObject { put("stars", 0) }), ...),
    render = { params: JsonObject, modifier: Modifier ->
        val p = SkiJson.decodeFromJsonElement<RatingBarSlideParams>(params)
        RatingBarSlide(stars = p.stars, animated = p.animated, accent = p.accent, modifier = modifier)
    },
)
```

And one provider per module:

```kotlin
// generated
class GeneratedSkiComponents_<module> : SkiComponentProvider {
    override val components = listOf(RatingBarSlideSpec, ...)
}
```

- **JVM:** register the provider in `META-INF/services/…SkiComponentProvider` so `ServiceLoader` finds it. That is how packs are discovered (Task 1b.5).
- **Wasm/JS/iOS/Android:** no `ServiceLoader`. The Gradle plugin generates an aggregating `skiComponents()` function in the app module that lists every provider on the classpath (Task 1b.6).
- Invalid params at runtime (wrong type, out of range) render an error card on the slide with the message. They never crash the deck. The AI gets the same message as a validation error (studio 03).

## Task 1b.3: the `component` element

Add to the element table in 01 (Task 1.2), replacing `builtin`:

```kotlin
@Serializable @SerialName("component")
data class Component(
    override val id: String,
    override val revealAt: Int = 0,
    val componentId: String,          // "rating-bar"
    val params: JsonObject = JsonObject(emptyMap()),
    val frame: ComponentFrame = ComponentFrame.None,   // None, Card, Device(deviceId) – wrap it in a DeviceFrame to show it "on a phone"
) : Element
```

- `ComponentRegistry` resolves `componentId`. Unknown IDs render a placeholder: "Component `rating-bar` isn't available here. Open this deck in a project that includes it." This is what the prebuilt web player and the phone show.
- The renderer passes reveal state through: inside the component, `LocalRevealStep` counts from the element's own `revealAt`, so a component with `steps = 2` gets steps 0..2 locally.
- Ski's own built-ins (video, whiteboard, confetti, device gallery) become `@SkiComponent`s in `:shared:components`, registered through the same mechanism. That gives one way of doing things, and it dogfoods the processor.

Validation (01, Task 1.4) gains: `componentId` known to the current registry (a warning, since the deck may be opened elsewhere) and `params` valid against the schema (an error).

## Task 1b.4: `./gradlew skiPreview` (free, with hot reload)

The "moment's notice" path. In the user's project:

```kotlin
plugins {
    id("io.github.donald-okara.ski") version "<ski version>"
}

ski {
    deck = file("talks/my-talk.ski.json")    // or: deckFunction = "com.example.slides.myDeck" for a code-first deck
}
```

- `skiPreview` runs a desktop window with the deck (presenter + audience windows, like `ski`'s `desktopApp`), with the project's code on the classpath, using **Compose Hot Reload** (the plugin applies `org.jetbrains.compose.hot-reload`; `ski` already uses it through its `composeMultiplatformPlugin` convention).
- Editing a composable reloads it in place; editing `deck.json` reloads the document through the engine, and the current slide is kept by ID.
- Includes a component browser (a gallery generated from `@SkiExample`s), so "show this component now" works without writing a deck at all. It reuses `composeApp`'s gallery UI, moved into a publishable module.
- Needs a JVM target in the module with the components. KMP projects add `jvm()`; Android-only projects are covered in Risks.
- Free and open source. This is today's Ski workflow with less setup.

## Task 1b.5: component packs (for Ski Studio)

The packaged studio has no compiler, so it loads components already compiled by the user's build.

- `./gradlew skiPack` produces `build/ski/<project>.skipack`: a zip with the module's JVM classes + runtime dependencies **excluding** Compose, kotlinx and Ski artifacts (the studio provides those), plus `pack.json`:
  ```json
  { "packFormat": 1, "skiVersion": "0.4.0", "composeVersion": "1.12.0", "kotlinVersion": "2.4.20",
    "components": [ { "id": "rating-bar", "name": "RatingBarSlide", "steps": 1 } ] }
  ```
- The studio loads each pack in its own child classloader whose parent provides Compose/Ski, then finds providers with `ServiceLoader`.
- **Version check at build time:** before writing the pack, `skiPack` compares the project's versions with the installed studio's `~/.ski/studio.json` and fails early with the exact fix ([03-third-party.md](03-third-party.md), Tasks T.1–T.3). `./gradlew skiDoctor` prints the full picture.
- **Compatibility check** on load (second line of defence): refuse a pack built against a newer `skiVersion` major/minor than the studio's engine, or a different Compose minor version. Show the fix: "Rebuild with ski 0.4.x" or "Update Ski Studio". Compose-compiled code is sensitive to runtime version differences, which is the main risk here.
- Packs can include third-party component libraries from Maven (`ski { packDependencies { include("com.acme:acme-charts") } }`, [03](03-third-party.md), Task T.5).
- `./gradlew skiStudio` = `skiPack` + open the installed studio (found as described in [03](03-third-party.md), Task T.3) with that pack (through the `ski://open-pack?path=…` URL scheme from studio 06), then keeps watching: run it with `-t` (continuous build) and the studio reloads the pack's classloader when the file changes. This is a reload, not Compose Hot Reload: slide state resets, the deck and selection don't.
- Studio side of this: [studio 02, Task 2.9](../studio/02-studio-shell.md).

## Task 1b.6: `./gradlew skiWebPlayer` (custom web player)

The prebuilt web player (01, Task 1.13) can't contain your components, because Kotlin/Wasm can't load extra code at runtime. So the plugin builds a player that includes them:

- Applied to an app module with a `wasmJs` target that depends on the component modules (the plugin's docs and a template show the setup).
- The plugin generates the `main()` that calls `skiComponents()` (the aggregated registry, Task 1b.2) and starts the player. Output is the same folder layout as the prebuilt player, so the studio's Publish flow and the README's Vercel steps work unchanged.
- Components must compile for `wasmJs`. Desktop-only components (using `java.*` APIs) are reported at build time with the component ID, and render as placeholders on the web.
- The studio's Publish dialog detects decks that use components the prebuilt player doesn't have and offers "Use a custom player folder" (Studio 02, Task 2.8).

## Task 1b.7: Kotlin export and the AI

- Kotlin export (01) emits a direct call for `component` elements using `fqName`: `RatingBarSlide(stars = 5, animated = true)`. A data deck with your components becomes a code-first deck in your own project.
- The studio's AI gets each component's description, schema and examples through a `list_components` tool (studio 03), so it can say "Here's your rating bar in three states" and fill params correctly.

## Order of work

1. 1b.1 annotations + 1b.2 processor, tested with KSP's compile-testing in `:engine:ksp` (generated code compiles; bad signatures give the right errors).
2. 1b.3 `component` element + registry, and move Ski's own built-ins onto it.
3. 1b.4 `skiPreview`. **Prototype Compose Hot Reload inside a plugin-launched window first**; it is the least certain piece.
4. 1b.6 `skiWebPlayer`.
5. 1b.5 packs, once the studio shell (studio Phase 2) exists to load them.

## Risks

- **Compose version coupling.** Packs compiled against one Compose version may fail at runtime in a studio built with another. Mitigations: version check in `pack.json`, studio releases that track Compose closely, and a clear rebuild message. `skiPreview` and `skiWebPlayer` don't have this problem because everything is built together.
- **Android-only projects.** Many component libraries are Android-only (`com.android.library`), so their code can't run in a desktop JVM window. v1: document it, and suggest moving showcase components into a KMP module. Later options: a `skiPreview` that runs on an emulator, or screenshots from Android previews (e.g. Paparazzi/Roborazzi) shown as images.
- **Hot Reload limits.** Compose Hot Reload needs the JetBrains Runtime and has limits on what changes it can apply. The plugin should fall back to a normal restart and say why.
- **Classloader leaks** from repeated pack reloads in the studio: dispose compositions using the old classloader before dropping it, and cap reloads before suggesting a restart.
- **Phone remote:** the phone can't run pack code. The desktop sends a rendered image of slides that contain custom components (studio 04).
