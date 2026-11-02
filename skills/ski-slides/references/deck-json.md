# Data decks (`deck.json`, Ski Studio)

> **Status: outline.** The engine that reads `deck.json` is planned (`docs/engine/01-engine.md` in the `ski` repo) but not built yet. Field names below follow that plan and may change. When the engine ships, this file is updated from its schema, and the element list is generated from the code.

A data deck is a JSON document that Ski's engine draws with the same components code-first decks use. Ski Studio edits it only through typed operations (its tools). Read [principles.md](principles.md) first; this file is about applying it to the data model.

## Shape

```json
{
  "schemaVersion": 1,
  "id": "…",
  "title": "State hoisting in practice",
  "sessionMinutes": 30,
  "theme": { "darkMode": true, "background": "animatedDiagonalWavy", "mainFrame": "snake" },
  "slides": [
    {
      "id": "s-hook",
      "label": "Hook",
      "layout": { "type": "center" },
      "transition": "fade",
      "elements": [
        { "type": "title", "id": "e1", "text": "Screens you can test without a device", "kicker": "Compose" }
      ],
      "notes": [
        "Promise: by the end you can test any screen's logic without launching it.",
        "Ask who has a screen they're afraid to touch."
      ]
    }
  ],
  "assets": []
}
```

- **Slides and elements are addressed by `id`, never by position.** Keep IDs stable when editing.
- **`notes`** is a list of strings, one spoken sentence each (they become flashcards).
- **`steps` is not stored.** A slide's step count is the highest `revealAt` among its elements (plus code `focusSteps`). Number reveals 1, 2, 3… with no gaps.

## Layouts

| `layout.type` | Use |
|---|---|
| `stack` | Default: elements top to bottom. |
| `center` | Title, section and question slides. |
| `split` | Two columns. Elements carry `column: 0 \| 1`. Idea on the left, code or demo on the right. |
| `segments` | Resizable panes (`vertical: true/false`). |

## Elements

See [components.md](components.md) for what each one looks like and when to use it.

| `type` | Key fields |
|---|---|
| `title` | `text`, `kicker?` |
| `bullets` | `items`, `reveal` (one per step when true) |
| `text` | `text` (light markdown) |
| `code` | `code`, `language`, `foldLambdas`, `title?`, `focusSteps?` (line ranges highlighted step by step), `source?` (where it came from) |
| `callout` | `title`, `body` |
| `commit` | `sha` |
| `stat` | `value`, `unit`, `label`, `direction`, `improvement`, `sha?`, `source?` |
| `columnChart` / `barChart` | `points: [{label, value, highlight?}]`, `caption?` |
| `timeline` | `stages: [{year, title, caption, details}]`, `gapAfterIndex?`, `gapTitle?` |
| `swatches` | `label`, `swatches: [{name, hex}]` |
| `image` | `assetId`, `shape` (`plain` / `expressive`), `fit` |
| `video` | `assetId`, `autoplay`, `loop`, `muted`, `startMs?`, `endMs?` |
| `device` | `deviceId`, `orientation`, `child` (one element) |
| `component` | `componentId`, `params`, `frame` (`none` / `card` / `device`). Ski's richer built-ins and the user's own `@SkiComponent`s; see [custom-components.md](custom-components.md). |

Every element has `id` and `revealAt` (0 = always visible).

## Editing through Ski Studio's tools

When you have deck tools rather than the file:

1. Call `get_deck_outline` first. Use `get_slide` for the slides you'll change, and `get_selection` if the user says "this slide".
2. Prefer `update_element` / `update_slide` over removing and re-adding, so IDs stay stable and undo stays readable.
3. Use `list_assets` before placing images or video; refer to assets by `assetId`, and use the asset's description to pick the right one.
4. Use `list_components` / `get_component` before placing a `component`; fill `params` from its schema, and start from its examples.
5. If a tool returns validation errors, fix them in the same turn. They're written for you to act on.
6. Group related changes into one request turn. The user undoes a whole turn at once.
7. If a project folder is attached, read the real code (`read_project_file`) instead of inventing it, and keep `source` on code elements so it can be refreshed.

## Code walkthroughs in data decks

```json
{ "type": "code", "id": "c1", "title": "CounterScreen.kt",
  "code": "…15 lines…",
  "focusSteps": [ {"from": 1, "to": 3}, {"from": 5, "to": 9}, {"from": 11, "to": 15} ],
  "source": { "path": "app/src/main/kotlin/CounterScreen.kt", "fromLine": 10, "toLine": 24 } }
```

Each step highlights one range and dims the rest. Write one note line per step saying what to point at.
