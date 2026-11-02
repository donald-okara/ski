---
name: ski-slides
description: Write and edit Ski presentation decks the Ski way. Use when creating, changing or reviewing slides built with Ski (Compose Multiplatform), whether as Kotlin code (generateDeck, slide composables), as a deck.json document, or through Ski Studio's deck tools. Covers slide structure, step-based reveals, code walkthroughs, showcasing composables, speaker notes and timing.
---

# Making slides the Ski way

Ski is a presentation framework built on Compose Multiplatform. Its purpose is showing code, and Compose components especially, on a slide at a moment's notice. A slide is either a composable (code-first decks) or a data element tree drawn by Ski's engine (`deck.json`, Ski Studio). The rules below apply to both.

## First, work out which kind of deck this is

| You see | Kind | Read next |
|---|---|---|
| Kotlin with `generateDeck { slide(...) { ... } }`, `SlideConfig`, `@Composable` slide functions | **Code-first deck** | [references/code-first.md](references/code-first.md) |
| A `deck.json` / `.ski` file, or tools named `add_slide`, `update_element`, `get_deck_outline` | **Data deck** (engine / Ski Studio) | [references/deck-json.md](references/deck-json.md) |
| `@SkiComponent`, `skiPreview`, `skiPack`, component packs, "show my component" | **Showcasing the user's own composables** | [references/custom-components.md](references/custom-components.md) |

Always read [references/principles.md](references/principles.md) before writing more than one slide. Use [references/components.md](references/components.md) to pick the right building block instead of hand-rolling layouts.

## The rules that matter most

1. **One idea per slide.** A title states the idea; the body proves or shows it. If you need "and" in the title, split the slide.
2. **Reveal with steps, never with duplicate slides.** Progressive disclosure uses the deck's step system: a slide declares how many steps it has, and items appear as the presenter presses Next. Never copy a slide to show one more bullet, and never drive presenter-paced reveals from local `remember` state. The presenter and audience windows each keep their own local state, so they would drift apart.
3. **Show real code, a little at a time.** Use Ski's code viewer for code, never a plain `Text`. Keep snippets short enough to read from the back of the room (about 15 lines). When a snippet is longer, walk through it in steps rather than shrinking the font.
4. **Show the real thing.** When the talk is about a component or behaviour, put the live composable on the slide (in a device frame if it's a mobile UI) next to the code that builds it. A screenshot is the fallback, not the default.
5. **Notes are the script.** Every slide gets speaker notes: 2–4 spoken sentences, one idea each (each line becomes a flashcard). Put exact numbers and names in the notes. Any metric on screen gets a note saying what it does *not* measure.
6. **Fit the session.** Budget roughly 1–2 minutes per slide against the session length the deck declares. Don't hard-code timer values.
7. **Use what Ski provides.** Frames, backgrounds, titles, bullet lists, callouts, charts, timelines, stat tiles and device frames already exist and are themed. Reach for them before writing custom layout code, so the deck looks consistent.

## Before you finish

- Every slide has a short label (it appears in the table of contents) and speaker notes.
- Reveal step counts match the revealable items (no Next press that shows nothing, no item that never appears).
- Code on screen compiles, or is clearly marked as pseudo-code in the notes.
- No placeholder text remains in titles or notes.
- For code-first decks: the deck builds, and you've stepped through it in the presenter view (`DeckMode.Local`). For data decks: the deck validates with no errors.
